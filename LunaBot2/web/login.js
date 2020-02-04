window.onload = load();
var webSocket;
var password;

function load() {
    webSocket = new WebSocket('ws://' + window.location.host + ':90');
    
    webSocket.addEventListener('message', function (event) {
        var data = event.data;

        if (data == '[auth-failure]') {
            alert("Wrong password. Please try again");
        }
        
        if (data == '[auth-success]') {
            localStorage.setItem("auth", password);
            window.location = "/";
        }
	});
}

function validate() {
    password = document.getElementById("password").value;
    
    webSocket.send("auth:"+password);
}