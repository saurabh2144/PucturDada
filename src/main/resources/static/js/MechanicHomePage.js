var stompClient = null;
let currentStatus = "Busy";

 
function connect() {
  const socket = new SockJS("/google");
  stompClient = Stomp.over(socket);

  stompClient.connect({}, (frame) => {
    console.log("Connected: " + frame);

    stompClient.subscribe("/topic/response", (message) => {
      if (currentStatus === "Available") {
        const response = JSON.parse(message.body);
        const displayMessage = response.message || "No message received";
        $("#messages").append(`<p>${displayMessage}</p>`);
        $("#popupOverlay").show();
        $("#pp").fadeIn();
      } else {
        console.log("Mechanic is busy, so request ignored.");
      }
    });
  }, (error) => {
    console.error("Connection error: ", error);
    alert("Failed to connect to the WebSocket. Please try again later.");
  });
}


  function sendFriendRequest() {
    if (stompClient && stompClient.connected) {
      const message = {
		
        name: mechanicName ,
		message: mechanicName + " accepted your request"
      };
      stompClient.send("/app/notification", {}, JSON.stringify(message));
      
    } else {
      alert("Unable to send request. Please check the connection.");
    }
  }
  
  
  
  function updateStatus(checkbox, mechanicName) {
      const status = checkbox.checked ? 'Available' : 'Busy';
	  currentStatus = status;

	  fetch('http://localhost:8085/toggleStatus?name=' + encodeURIComponent(mechanicName) + '&status=' + encodeURIComponent(status), {
	      method: 'POST'
	  })

      .then(response => response.text())
      .then(data => {
          console.log(data);
      });
  }
  
  
  
  
  
  
  

  function initMap() {
    const map = L.map('map').setView([0, 0], 14);
    fetch('/getNearestMechanic')
      .then(response => response.json())
      .then(mechanic => {
        if (mechanic) {
          const mechanicLat = mechanic.latitude;
          const mechanicLon = mechanic.longitude;

          L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap contributors'
          }).addTo(map);

          L.marker([mechanicLat, mechanicLon], {
            title: mechanic.name,
            icon: L.icon({
              iconUrl: 'https://cdn-icons-png.flaticon.com/512/2972/2972185.png',
              iconSize: [50, 50]
            })
          }).addTo(map).bindPopup(`<strong>${mechanic.name}</strong>`);

          if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
              const userLat = position.coords.latitude;
              const userLon = position.coords.longitude;

              L.marker([userLat, userLon], {
                title: "Your Location",
                icon: L.icon({
                  iconUrl: 'https://cdn-icons-png.flaticon.com/512/7124/7124723.png',
                  iconSize: [40, 40]
                })
              }).addTo(map);

              map.fitBounds([
                [userLat, userLon],
                [mechanicLat, mechanicLon]
              ]);

              L.Routing.control({
                waypoints: [
                  L.latLng(mechanicLat, mechanicLon),
                  L.latLng(userLat, userLon)
                ],
                routeWhileDragging: false,
                addWaypoints: false
              }).addTo(map);
            });
          } else {
            alert("Geolocation is not supported by this browser.");
          }
        } else {
          alert("No mechanic found.");
        }
      })
      .catch(error => console.error('Error fetching mechanic:', error));
  }

  $(document).ready(() => {
    connect();

    $("#acceptBtn").click(() => {
      $(".map-container").show();
      $("#popupOverlay").hide();
      $("#pp").hide();
      sendFriendRequest();
      initMap();
    });

    $("#rejectBtn").click(() => {
      $("#popupOverlay").hide();
      $("#pp").hide();
      alert("Request Rejected");
    });
  });

  window.onbeforeunload = () => {
    if (stompClient && stompClient.connected) {
      stompClient.disconnect();
      console.log("Disconnected");
    }
  };