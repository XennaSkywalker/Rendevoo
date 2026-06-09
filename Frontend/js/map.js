import { getMeetups, postMeetup } from "./api.js";
//this file is only for handling the UI of the website
const meetupFormContainer = document.getElementById("meetup-form-container");
const meetupForm = document.getElementById("meetup-form");
const closeButton = document.getElementById("close-button");
const submitButton = document.getElementById("submit-button");
let latLng;

console.log("google =", window.google);
console.log("API key test");

async function initMap() {
  const customStyle = [
    {
      featureType: "water",
      elementType: "geometry",
      stylers: [{ color: "#193341" }],
    },
    {
      featureType: "landscape",
      elementType: "geometry",
      stylers: [{ color: "#2c5b70" }],
    },
    {
      featureType: "poi",
      elementType: "labels.text.fill",
      stylers: [
        { visibility: "off" }, // Completely hides business/POI labels
      ],
    },
  ];
  const { Map } = await google.maps.importLibrary("maps");
  const { AdvancedMarkerElement, PinElement } =
    await google.maps.importLibrary("marker");

  const map = new google.maps.Map(document.getElementById("map"), {
    zoom: 10,
    center: { lat: 50, lng: 50 },
    // mapId: "DEMO_MAP_ID",
    minZoom: 3,
    styles: customStyle,
  });

  const meetupList = await getMeetups();
  meetupList.forEach((element) => {
    placeMarker(element.lat, element.lng, map);
  });

  //map on click
  map.addListener("click", (e) => {
    latLng = e.latLng;
    displayLatLng(e.latLng);
    meetupFormContainer.classList.remove("hidden");
  });

  //form buttons
  closeButton.addEventListener("click", () => {
    meetupFormContainer.classList.add("hidden");
  });

  submitButton.addEventListener("click", async (e) => {
    e.preventDefault();
    const meetupFormData = new FormData(meetupForm);
    const meetupData = {
      lat: latLng.lat(),
      lng: latLng.lng(),
      meetupName: meetupFormData.get("meetup-name"),
      meetupDate: meetupFormData.get("meetup-date"),
      meetupTime: meetupFormData.get("meetup-time"),
    };
    const meetupResponse = await postMeetup(meetupData);
    const lat = meetupResponse.lat;
    const lng = meetupResponse.lng;
    placeMarker(lat, lng, map);
  });
}

//helper functions
function placeMarker(lat, lng, map) {
  const marker = new google.maps.marker.AdvancedMarkerElement({
    position: { lat: lat, lng: lng },
    map: map,
  });
  marker.addEventListener("click", () => {
    console.log("marker clicked");
  });
}

function displayLatLng(latLng) {
  console.log(JSON.stringify(latLng));
}

initMap();
