export async function authFetch(resource, options = {}) {
  const token = localStorage.getItem("token");
  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
  };

  const response = await fetch(resource, { ...options, headers });
  const data = await response.json();

  if (!response.ok) {
    console.log("authFetch response data:", data);
    const message =
      data.error || data.message || response.statusText || "Request failed";
    throw new Error(message);
  }

  if (data.error) {
    console.log("authFetch response data:", data);
    throw new Error(data.error);
  }

  return data;
}

export async function getMeetups() {
  const response = await fetch("http://localhost:8080/backend/meetup", {
    method: "GET",
  });
  const data = await response.json();
  console.log(data);
  return data;
}

export async function postMeetup(meetupData) {
  const data = await authFetch("http://localhost:8080/backend/meetup", {
    method: "POST",
    body: JSON.stringify(meetupData),
  });
  console.log(data);
  return data;
}

export async function login(credentials) {
  const response = await fetch("http://localhost:8080/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  const data = await response.json();
  console.log("-----");
  console.log(data);

  // Check if response contains an error code
  if (data.errorCode) {
    throw new Error(data.message || data.errorCode);
  }

  // If successful, redirect to home page
  localStorage.setItem("token", data.token);
  window.location.href = "http://localhost:5500";
  return data;
}

export async function signup(signupData) {
  const response = await fetch("http://localhost:8080/auth/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(signupData),
  });

  const data = await response.json();
  console.log("here comes the daaataaa");
  console.log(data);

  // Check if response contains an error code
  if (data.errorCode) {
    throw new Error(data.message || data.errorCode);
  }

  // If successful, redirect to login page
  window.location.href = "http://localhost:5500/login.html";
  return data;
}
