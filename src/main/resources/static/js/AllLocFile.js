function initMap() {
		navigator.geolocation.getCurrentPosition(
			function (position) {
				var userLat = position.coords.latitude;
				var userLon = position.coords.longitude;

				var map = L.map('map').setView([userLat, userLon], 13);

				L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
					maxZoom: 19,
					attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
				}).addTo(map);

				L.marker([userLat, userLon], {
					title: "Your Location",
					icon: L.icon({
						iconUrl: 'https://cdn-icons-png.flaticon.com/512/7124/7124723.png',
						iconSize: [40, 30]
					})
				}).addTo(map);

				// Fetch nearby mechanics
				fetch('/getMechanics')
					.then(response => response.json())
					.then(mechanics => {
						mechanics.forEach(mechanic => {
							if (mechanic.latitude && mechanic.longitude) {
								L.marker([mechanic.latitude, mechanic.longitude], {
									title: mechanic.name,
									icon: L.icon({
										iconUrl: 'https://png.pngtree.com/png-vector/20240405/ourmid/pngtree-car-mechanic-vector-icon-png-image_12264316.png',
										iconSize: [50, 50]
									})
								}).addTo(map)
									.bindPopup(`<strong>${mechanic.name}</strong>`);
							}
						});
					})

			}
		);
	}

	var stompClient = null;

	function connect() {
		const socket = new SockJS("/google");
		stompClient = Stomp.over(socket);

		stompClient.connect(
			{},
			(frame) => {
				console.log("Connected: ");

				
				// Subscribe to driver notifications
				stompClient.subscribe("/topic/getnoti", (message) => {
					const response = JSON.parse(message.body);
					alert(response.message || "No message received.");
					document.querySelector('.loader-overlay').style.display = 'none';
					const displayMessage = response.message || "No message received";
					                    console.log(response);
										$("#messages").append(`<p>${displayMessage}</p>`);
					
					 
					setTimeout(() => {
					       window.location.href = "/booked";
					   }, 5000); 
					
				});
			}
		);
	}

	function sendFriendRequest() {
		if (stompClient && stompClient.connected) {
			const message = {
				name: "User123",
				message: "You have a new customer request!"
			};
			stompClient.send("/app/message", {}, JSON.stringify(message));
		
			
			document.querySelector('.loader-overlay').style.display = 'flex';
							document.body.classList.add('blur-effect');

			
		} else {
			console.error("WebSocket not connected.");
		}
	}

	$(document).ready(() => {
		connect();

		
		$("#Btn").click(() => {
			sendFriendRequest();
		});
	});

	window.onbeforeunload = () => {
		if (stompClient && stompClient.connected) {
			stompClient.disconnect();
			console.log("Disconnected from WebSocket");
		}
	};