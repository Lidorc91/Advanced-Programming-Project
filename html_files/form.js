// Import core functionality
import { getIframe } from "./core.js";

// Get the form element
const form = document.getElementById("messageForm");
console.log("form:", form);
// Listen for form submission
form.addEventListener("submit", (e) => {
  // e.preventDefault();
  console.log("button was pressed");
});
