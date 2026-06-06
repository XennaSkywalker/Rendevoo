export async function getMeetups() {
  const response = await fetch("http://localhost:8080/backend/meetup", {
    method: "GET",
  });
  const data = await response.json();
  console.log(data);
  return data;
}

export async function postMeetup(meetupData) {
  const response = await fetch("http://localhost:8080/backend/meetup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(meetupData),
  });
  const data = await response.json();
  console.log(data);
  return data;
}

export async function login(credentials) {
  const response = await fetch("http://localhost:8080/backend/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  const data = await response.json();
  console.log("-----");
  console.log(data);
  return data;
}
