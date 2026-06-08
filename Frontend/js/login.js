import { login } from "./api.js";

document.getElementById("login-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorMessageDiv = document.getElementById("error-message");
  errorMessageDiv.textContent = "";

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  const credentials = { username, password };

  try {
    const data = await login(credentials);
  } catch (error) {
    console.error("Login failed:", error);
    errorMessageDiv.textContent = error.message;
  }
});
