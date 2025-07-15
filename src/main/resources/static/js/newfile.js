function initMap() {
    const map = L.map('map').setView([0, 0], 14);

    fetch('/getNearestMechanic')
        .then(response => response.json())
        .then(mechanic => {
            if (mechanic) {
                const { latitude, longitude, name } = mechanic;

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);

                L.marker([latitude, longitude], {
                    title: name,
                    icon: L.icon({
                        iconUrl: 'https://png.pngtree.com/png-vector/20240405/ourmid/pngtree-car-mechanic-vector-icon-png-image_12264316.png',
                        iconSize: [50, 50]
                    })
                }).addTo(map).bindPopup(`<strong>${name}</strong>`);

                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(position => {
                        const userLoc = [position.coords.latitude, position.coords.longitude];

                        L.marker(userLoc, {
                            title: "Your Location",
                            icon: L.icon({
                                iconUrl: 'https://cdn-icons-png.flaticon.com/512/7124/7124723.png',
                                iconSize: [40, 30]
                            })
                        }).addTo(map);

                        const routeControl = L.Routing.control({
                            waypoints: [
                                L.latLng(userLoc),
                                L.latLng([latitude, longitude])
                            ],
                            createMarker: () => null,
                            routeWhileDragging: false,
                            addWaypoints: false,
                            lineOptions: {
                                styles: [{ color: "#007BFF", weight: 6 }]
                            },
                            show: false,
                            fitSelectedRoutes: true,
                            draggableWaypoints: false
                        }).addTo(map);

                        routeControl.on('routesfound', e => {
                            const { totalDistance, totalTime } = e.routes[0].summary;
                            document.getElementById('routeInfo').innerText =
                                `Distance: ${(totalDistance / 1000).toFixed(2)} km, Time: ${(totalTime / 60).toFixed(0)} mins`;
								document.getElementById('minu').innerText =
								                                `${(totalTime / 60).toFixed(0)}`;
                        });

                        routeControl._container.style.display = "none";
                        map.setView(userLoc, 14);
                    });
                }
            }
        });
}

window.onload = initMap;
document.getElementById("okBtn").addEventListener("click", function () {
    document.getElementById("okBtn").innerText= "9598150940"// hide button written
  
});


fetch("/payments/getpin")
    .then(response => response.text())
    .then(pin => {
        document.getElementById("random").innerText = `Your PIN is: ${pin}`;
    });

document.getElementById("payNowBtn").addEventListener("click", function () {
	
    const options = {
        key: "rzp_test_dDNXpdEZbYVz7T", // Change this to your Razorpay Key ID
        amount: 10000,
        currency: "INR",
        name: "Puncture DADA",
        description: "Mechanic Booking Fee",
        image: "/vnewlogo.png",
        handler: function (response) {
            
            document.getElementById("ppp").style.display = "block";
            document.getElementById("payNowBtn").style.display = "none";
            document.getElementById("paymentId").innerText = response.razorpay_payment_id;

            fetch("/payments/save", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userName: "Saurabh Singh",
                    amount: 10000,
                    paymentId: response.razorpay_payment_id,
                    status: "Success"
                })
            }).catch(err => alert("Error saving payment: " + err));
        },
        prefill: {
            name: "Saurabh Singh",
            email: "itsmesaurabh214@gmail.com",
            contact: "9598150940"
        },
        theme: { color: "#007BFF" }
    };
    new Razorpay(options).open();
	
	
	 
	
});
