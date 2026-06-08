import { signup } from "./api.js";

document.getElementById("signup-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorMessageDiv = document.getElementById("error-message");
  errorMessageDiv.textContent = "";

  const username = document.getElementById("username").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const signupData = { email, username, password };

  try {
    const response = await signup(signupData);
  } catch (error) {
    console.error("Signup failed:", error);
    errorMessageDiv.textContent = error.message;
  }
});
