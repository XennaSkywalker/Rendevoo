# Rendevoo

> A location-based meetup platform — discover, create, and chat around real-world meetups near you.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![JavaScript](https://img.shields.io/badge/Frontend-Vanilla%20JS-yellow?style=flat-square&logo=javascript)
![WebSocket](https://img.shields.io/badge/Real--Time-WebSocket%2FSTOMP-purple?style=flat-square)
![GitHub](https://img.shields.io/badge/Repo-XennaSkywalker%2FRendevoo-black?style=flat-square&logo=github)

---

## What is Rendevoo?

Rendevoo is a full-stack web application where users can create meetups pinned to real-world map coordinates, discover active meetups within a set radius of their location, and communicate in real-time through per-meetup chat rooms.

The interesting engineering is not the CRUD. It is the **geospatial proximity query** (spherical law of cosines in SQL with a bounding-box pre-filter), the **lazy meetup expiry** (no background job — expiry is checked at query time), and the **WebSocket chat** (STOMP topic per meetup, messages persisted to PostgreSQL).

---

## Features

| Feature                      | Status                 | Notes                                                    |
| ---------------------------- | ---------------------- | -------------------------------------------------------- |
| Map-based meetup creation    | ✅ Implemented         | Click a point on Google Maps to set coordinates          |
| Nearby meetup search         | ✅ Implemented         | Spherical law of cosines query, user-configurable radius |
| Lazy meetup expiry           | ✅ Implemented         | No `@Scheduled` job — filtered at query time             |
| JWT authentication           | ✅ Implemented         | Stateless, HS256-signed, BCrypt password hashing         |
| WebSocket chat               | ✅ Implemented (basic) | Persists + broadcasts messages per meetup topic          |
| Chat history loading         | 🔧 Partial             | Service method exists, not yet wired to a UI endpoint    |
| Chat auth / participant gate | 🔧 Planned             | JWT not yet threaded through STOMP connect headers       |
| Google Places integration    | 📋 Planned             | Named in roadmap, not yet implemented                    |
| Pagination / rate limiting   | 📋 Planned             | Post-ship polish                                         |

---

## Architecture

```
Browser (Vanilla JS + Google Maps)
  │
  ├── REST/JSON (HTTP) ──────────────────> Spring MVC Controllers
  │      Authorization: Bearer <JWT>            │
  │                                       MeetupService
  │                                       UserService
  │                                       JwtFilter → SecurityConfig
  │                                             │
  │                                       Spring Data JPA / Hibernate
  │                                             │
  │                                       PostgreSQL
  │                                         ├── users
  │                                         ├── meetups  (INDEX lat, lng)
  │                                         └── messages
  │
  └── WebSocket /ws (STOMP) ─────────────> ChatController
         STOMP CONNECT                        ChatService
         SUBSCRIBE /topic/meetup/{id}         ChatRepository
         SEND /app/chat/{id}                       │
                                             PostgreSQL (messages)
```

**Stack at a glance**

| Layer       | Technology                                      |
| ----------- | ----------------------------------------------- |
| Backend     | Java 17, Spring Boot 4.0.2                      |
| API         | Spring MVC (REST)                               |
| Auth        | Spring Security, JJWT 0.13.0, BCrypt            |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL          |
| Real-time   | Spring WebSocket, STOMP, STOMP.js 7.0.0         |
| Frontend    | Vanilla JS (ES modules), HTML, Tailwind CSS CDN |
| Maps        | Google Maps JavaScript API                      |
| Build       | Maven 3.9.12                                    |

---

## How It Works — The Interesting Parts

### 1. Geospatial Proximity Search

Finding meetups near a user requires computing real-world distance between two coordinate pairs. The approach is a **two-stage SQL query**:

**Stage 1 — Bounding box pre-filter (uses the composite index on `lat`, `lng`)**

```sql
WHERE lat  BETWEEN :lat  - :radius / 111.0 AND :lat  + :radius / 111.0
  AND lng  BETWEEN :lng  - :radius / 111.0 AND :lng  + :radius / 111.0
```

This eliminates the vast majority of rows using simple arithmetic on the indexed columns, avoiding a full table scan.

**Stage 2 — Spherical law of cosines (exact distance on remaining candidates)**

```sql
6371 * ACOS(
  SIN(RADIANS(:lat)) * SIN(RADIANS(m.lat))
  + COS(RADIANS(:lat)) * COS(RADIANS(m.lat)) * COS(RADIANS(m.lng - :lng))
)
```

**Why spherical law of cosines and not Haversine?**

The Haversine formula is mathematically equivalent for small distances and is the common choice in application-layer code. However, in a SQL query, the spherical law of cosines expresses as a single `ACOS` call with no intermediate `ASIN(SQRT(...))` — it is simpler to read and reason about in a `@Query` annotation, and most SQL engines optimise trigonometric functions equivalently. At the scale of a meetup platform (distances under a few hundred km) the two formulas produce identical results to any meaningful precision.

A composite B-tree index on `(lat, lng)` is declared on `MeetupEntity` so the bounding-box pre-filter actually uses the index rather than triggering a sequential scan.

---

### 2. Lazy Meetup Expiry

Meetups have a `scheduledTime` and a `MeetupStatus` (`ACTIVE` / `EXPIRED`). Instead of a `@Scheduled` background job polling the table on a timer, expiry is checked **at the point of data fetch**:

- The list endpoint (`GET /backend/meetup`) loads active meetups, compares their date/time to the current JVM time, marks stale ones `EXPIRED`, saves the updates, and returns only active records.
- The nearby endpoint (`POST /backend/nearby`) runs a native PostgreSQL `UPDATE` query comparing meetup date/time with `NOW()` before the proximity select.

**Trade-off:** A scheduler would expire meetups at an exact moment regardless of whether anyone is looking. Lazy expiry means a meetup technically "lives" in the database past its time until the next read. This is a deliberate choice — it removes threading complexity, has no overhead when the table is idle, and is completely sufficient at prototype scale. A `@Scheduled` job becomes the right answer if exact-time chat-room locking or push notifications are required.

---

### 3. JWT Authentication Flow

```
POST /auth/signup  →  BCrypt.hash(password)  →  INSERT user  →  return JWT
POST /auth/login   →  BCrypt.matches()        →  SELECT user  →  return JWT

Every protected request:
  JwtFilter (OncePerRequestFilter)
    → extract Bearer token
    → JwtUtil.validateToken()   (HMAC-SHA256 verify + expiry check)
    → load UserDetails from DB
    → set UsernamePasswordAuthenticationToken in SecurityContextHolder
    → next filter
```

Tokens are signed with HS256. Passwords are stored as BCrypt hashes (never plaintext). The session policy is `STATELESS` — no server-side session is created. `SecurityConfig` explicitly permits `/auth/**` and `GET /backend/meetup`; all other routes require authentication.

---

### 4. WebSocket / STOMP Chat

Each meetup has its own STOMP topic. The flow:

```
Client connects to:    ws://localhost:8080/ws
Client subscribes to:  /topic/meetup/{meetupId}
Client sends to:       /app/chat/{meetupId}

Server (ChatController):
  → persists MessageEntity to PostgreSQL
  → broadcasts to /topic/meetup/{meetupId}
  → all subscribers receive the message in real time
```

The in-memory Spring simple broker handles topic routing. For horizontal scaling, this would be replaced with an external broker (RabbitMQ + STOMP plugin, or Redis pub/sub) so messages can be broadcast across multiple backend instances.

**Current state:** Chat persists and broadcasts correctly. The chat page currently uses a hardcoded meetup ID and a generated sender label. JWT is not yet threaded through the STOMP `CONNECT` frame — participant authentication and meetup membership checks are planned next.

---

## Database Schema

```
┌─────────────────────────┐      ┌──────────────────────────────────┐
│         users           │      │            meetups               │
├─────────────────────────┤      ├──────────────────────────────────┤
│ id          BIGINT  PK  │      │ id           BIGINT  PK          │
│ username    VARCHAR     │      │ creator_id   BIGINT  (scalar FK) │
│ email       VARCHAR     │      │ meetup_name  VARCHAR             │
│ password_hash VARCHAR   │      │ lat          DOUBLE              │
└─────────────────────────┘      │ lng          DOUBLE              │
                                 │ meetup_date  DATE                │
                                 │ meetup_time  TIME                │
                                 │ meetup_status VARCHAR (enum)     │
                                 └──────────────────────────────────┘
                                          │
                                 ┌──────────────────────────────────┐
                                 │           messages               │
                                 ├──────────────────────────────────┤
                                 │ id         BIGINT  PK            │
                                 │ meetup_id  BIGINT  (scalar)      │
                                 │ sender     VARCHAR               │
                                 │ content    TEXT                  │
                                 │ sent_at    TIMESTAMP             │
                                 └──────────────────────────────────┘

INDEX: (lat, lng) — composite B-tree on meetups, used by bounding-box pre-filter
```

**Note:** `creator_id` and `meetup_id` in messages are currently stored as scalar values. JPA `@ManyToOne` relationships are planned but not yet mapped in the entity layer.

---

## API Reference

### Auth

| Method | Endpoint       | Body                            | Auth | Returns               |
| ------ | -------------- | ------------------------------- | ---- | --------------------- |
| `POST` | `/auth/signup` | `{ username, email, password }` | None | `{ username, email }` |
| `POST` | `/auth/login`  | `{ username, password }`        | None | `{ token }`           |
| `POST` | `/auth/verify` | `{ token }`                     | None | Subject string        |

### Meetups

| Method | Endpoint          | Body                                               | Auth       | Returns                            |
| ------ | ----------------- | -------------------------------------------------- | ---------- | ---------------------------------- |
| `GET`  | `/backend/meetup` | —                                                  | None       | Array of active meetups            |
| `POST` | `/backend/meetup` | `{ meetupName, lat, lng, meetupDate, meetupTime }` | Bearer JWT | Created meetup                     |
| `POST` | `/backend/nearby` | `{ lat, lng, radius }`                             | Bearer JWT | Nearby meetups with distance field |

### WebSocket (STOMP)

| Action          | Destination                             |
| --------------- | --------------------------------------- |
| Connect         | `ws://localhost:8080/ws`                |
| Subscribe       | `/topic/meetup/{meetupId}`              |
| Send message    | `/app/chat/{meetupId}`                  |
| Message payload | `{ sender, content, meetupID, sentAt }` |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL (local instance, database named `rendezvoo`)
- A Google Maps JavaScript API key

### Backend

```bash
# Clone the repo
git clone https://github.com/XennaSkywalker/Rendevoo.git
cd Rendevoo/Backend/meetup-service

# Configure your database and API key
# Edit src/main/resources/application.properties:
#   spring.datasource.url=jdbc:postgresql://localhost:5432/rendezvoo
#   spring.datasource.username=<your_pg_user>
#   spring.datasource.password=<your_pg_password>
#   jwt.secret=<your_256bit_base64_secret>   # generate: openssl rand -base64 32

# Run
./mvnw spring-boot:run
# Backend starts on http://localhost:8080
```

### Frontend

The frontend is static HTML/JS — no build step required.

```bash
# Serve with any static server, e.g. VS Code Live Server (port 5500)
# Or:
cd Rendevoo/Frontend
npx serve -l 5500 .
```

Open `http://localhost:5500/index.html`. The frontend expects the backend at `localhost:8080` and the Google Maps key in `index.html`.

### Environment variables to set before running

| Variable                     | Where                    | Description                          |
| ---------------------------- | ------------------------ | ------------------------------------ |
| `spring.datasource.url`      | `application.properties` | PostgreSQL connection string         |
| `spring.datasource.username` | `application.properties` | DB username                          |
| `spring.datasource.password` | `application.properties` | DB password                          |
| `jwt.secret`                 | `application.properties` | Base64-encoded 256-bit secret        |
| Google Maps API key          | `index.html`             | Maps JS API key (restrict by origin) |

> ⚠️ Do not commit real credentials. Add `application.properties` to `.gitignore` and restrict the Maps API key by allowed origin in Google Cloud Console.

---

## Design Decisions

### Spherical law of cosines over Haversine

Both formulas compute great-circle distance. The spherical law of cosines expresses as a single `ACOS` call, making it more readable and maintainable inside a JPA `@Query` annotation. At the distances relevant to a local meetup platform (< 200 km), the two approaches are numerically identical.

### Lazy expiry over `@Scheduled`

No background thread, no polling, no scheduler configuration. Expiry is checked at query time. This is correct for a prototype: zero overhead when idle, trivially understandable, and easy to explain. A scheduled job becomes necessary only if exact-time side effects (locking chat rooms, sending notifications) are required.

### Stateless JWT over sessions

REST APIs benefit from stateless auth — no session store, no sticky sessions, horizontally scalable by default. JWT also threads naturally into WebSocket STOMP headers (planned).

### Spring simple broker over RabbitMQ/Redis

Correct for single-server deployment. The upgrade path to an external broker is well-defined and documented in the roadmap.

---

## Roadmap

- [ ] Thread JWT through STOMP `CONNECT` headers (participant auth for chat)
- [ ] Load chat history on room open (endpoint + UI)
- [ ] Replace hardcoded meetup ID in chat page with dynamic selection
- [ ] Add JPA `@ManyToOne` relationships (meetup → user, message → meetup)
- [ ] Input validation on meetup creation endpoints
- [ ] Pagination on meetup list
- [ ] Rate limiting (Bucket4j)
- [ ] Unit + integration tests (service layer + distance boundary tests)
- [ ] Deploy: Railway (backend), Vercel (frontend), Supabase (PostgreSQL)
- [ ] External broker for WebSocket (RabbitMQ / Redis pub/sub) if scaling

---

## Built by

**Xenna** — CS student, COEP Technological University  
[GitHub: XennaSkywalker](https://github.com/XennaSkywalker)
