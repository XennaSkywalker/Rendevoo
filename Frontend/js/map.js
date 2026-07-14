import { getMeetups, postMeetup } from "./api.js";

const meetupFormContainer = document.getElementById("meetup-form-container");
const meetupForm = document.getElementById("meetup-form");
const closeButton = document.getElementById("close-button");
const errorMessageDiv = document.getElementById("error-message");
const setLocationButton = document.getElementById("set-location-button");
const locationStatusText = document.getElementById("location-status-text");
let userLng = null;
let userLat = null;
let selectedLatLng = null;
let mode = "CREATE";
console.log("google =", window.google);
console.log("API key test");

async function initMap() {
  const { Map } = await google.maps.importLibrary("maps");
  const { AdvancedMarkerElement, PinElement } =
    await google.maps.importLibrary("marker");

  const initialLocation = await getUserLocation();
  userLat = initialLocation.lat;
  userLng = initialLocation.lng;

  const map = new Map(document.getElementById("map"), {
    zoom: 4,
    center: { lat: userLat, lng: userLng },
    mapId: "176c391fbf3dd89d6444b1be",
    minZoom: 4,
  });

  placeUserLocationMarker(
    map,
    userLat,
    userLng,
    AdvancedMarkerElement,
    PinElement,
  );
  updateLocationStatus("Location set automatically");
  map.setCenter({ lat: userLat, lng: userLng });

  setupMeetupInteractions(map, AdvancedMarkerElement);
  await loadMeetups(map, AdvancedMarkerElement);
}

async function getUserLocation() {
  if (!navigator.geolocation) {
    return { lat: 18, lng: 73 };
  }

  return new Promise((resolve) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        });
      },
      () => resolve({ lat: 18, lng: 73 }),
      { enableHighAccuracy: true, timeout: 5000 },
    );
  });
}

function placeUserLocationMarker(
  map,
  lat,
  lng,
  AdvancedMarkerElement,
  PinElement,
) {
  const bluePin = new PinElement({
    background: "#4285F4",
    borderColor: "#1A73E8",
    glyphColor: "#FFFFFF",
  });

  new AdvancedMarkerElement({
    map,
    position: { lat, lng },
    content: bluePin.element,
  });
}

function setupMeetupInteractions(map, AdvancedMarkerElement) {
  map.addListener("click", (event) => {
    if (mode === "CREATE") {
      selectedLatLng = event.latLng;
      displayLatLng(event.latLng);
      showMeetupForm();
    } else if (mode === "SEARCH") {
      userLat = event.latLng.lat();
      userLng = event.latLng.lng();
      console.log("User location set:", userLat, userLng);
      selectedLatLng = event.latLng;
      setLocationConfirmed();
      map.setCenter({ lat: userLat, lng: userLng });
    }
  });

  closeButton?.addEventListener("click", hideMeetupForm);

  setLocationButton?.addEventListener("click", () => {
    mode = "SEARCH";
    setLocationButton.classList.remove(
      "bg-surface-container",
      "text-on-surface-variant",
    );
    setLocationButton.classList.add(
      "bg-primary/10",
      "text-primary",
      "ring-2",
      "ring-primary/30",
    );
    setLocationButton.textContent = "Set location manually";
    updateLocationStatus("Click anywhere on the map to set the location.");
  });

  meetupForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    await handleMeetupSubmit(map, AdvancedMarkerElement);
  });
}

async function loadMeetups(map, AdvancedMarkerElement) {
  const meetupList = await getMeetups(userLat, userLng, 100); // Example coordinates and radius
  meetupList.forEach((element) => {
    placeMarker(element.lat, element.lng, map, AdvancedMarkerElement);
  });
}

async function handleMeetupSubmit(map, AdvancedMarkerElement) {
  if (!selectedLatLng) {
    showErrorMessage("Please select a location on the map.");
    return;
  }

  const formData = new FormData(meetupForm);
  const meetupData = {
    lat: selectedLatLng.lat(),
    lng: selectedLatLng.lng(),
    meetupName: formData.get("meetup-name"),
    meetupDate: formData.get("meetup-date"),
    meetupTime: formData.get("meetup-time"),
  };

  clearErrorMessage();

  try {
    const meetupResponse = await postMeetup(meetupData);
    placeMarker(
      meetupResponse.lat,
      meetupResponse.lng,
      map,
      AdvancedMarkerElement,
    );
    hideMeetupForm();
    meetupForm?.reset();
    console.log("Meetup created:", meetupResponse);
  } catch (error) {
    console.error("Failed to create meetup:", error);
    showErrorMessage(error.message);
  }
}

function showMeetupForm() {
  meetupFormContainer?.classList.remove("hidden");
}

function hideMeetupForm() {
  meetupFormContainer?.classList.add("hidden");
  meetupForm?.reset();
  clearErrorMessage();
}

function setLocationConfirmed() {
  if (setLocationButton) {
    setLocationButton.classList.remove(
      "bg-primary/10",
      "text-primary",
      "ring-2",
      "ring-primary/30",
    );
    setLocationButton.classList.add(
      "bg-surface-container",
      "text-on-surface-variant",
    );
    mode = "CREATE";
  }

  updateLocationStatus("Location set. You can now submit the meetup.");
}

function updateLocationStatus(message) {
  if (locationStatusText) {
    locationStatusText.textContent = message;
  }
}

function clearErrorMessage() {
  if (errorMessageDiv) {
    errorMessageDiv.textContent = "";
  }
}

function showErrorMessage(message) {
  if (errorMessageDiv) {
    errorMessageDiv.textContent = message;
  }
}

function placeMarker(lat, lng, map, AdvancedMarkerElement) {
  const marker = new AdvancedMarkerElement({
    position: { lat, lng },
    map,
    title: "Meetup Location",
    gmpClickable: true,
  });

  marker.addEventListener("click", () => {
    console.log("marker clicked");
  });
}

function displayLatLng(latLng) {
  console.log(JSON.stringify(latLng));
}

initMap();
