window.onload = load();
var webSocket;

function load() {
    openSocket();
}
function openSocket() 
{
	var startLuna = document.getElementById('start');
	var stopLuna = document.getElementById('stop');
	var restartLuna = document.getElementById('restart');
	
	var saveSettings = document.getElementById('save');
	
	var statuselement = document.getElementById('status');
	var lastReport = document.getElementById('lastreport');

	var consolearea = document.getElementById('consolearea');

	lastReport.innerText = "";
	consolearea.innerText = "";
	
	webSocket = new WebSocket('ws://' + window.location.host + ':90');
	
	startLuna.onclick = start;
	stopLuna.onclick = stop;
	restartLuna.onclick = restart;	
	saveSettings.onclick = save;
	
	webSocket.addEventListener('message', function (event) {
			var data = event.data;
			
			if (data.startsWith("[s]")) {
				document.getElementById('settingswindow').innerHTML = (data.substring(3));
                return;
            }
            
            if (data == '[auth-failure]') {
                localStorage.removeItem("auth");
                window.location.replace("/login");
            }
            
            consolearea.innerHTML += data.endsWith("\r\n") ? data : data+"\r\n";
			consolearea.scrollTop = consolearea.scrollHeight;
			
	});

	webSocket.addEventListener('open', function (event) {
			report("Connected to Luna-Gateway");
                
            if (localStorage.hasOwnProperty("auth")) {
                webSocket.send("auth:"+localStorage.getItem("auth"));
            } else {
                window.location.replace("/login");
            }
	});
}
function save() {
	if (window.confirm('Are you sure you want to save? This will update immediately.')) {
        var settings = document.getElementById('settingswindow').value;
		webSocket.send('[settings]');
		webSocket.send(settings);
		webSocket.send('[end]');
        
        report("Updated settings");
	}
}
function stats(msg) 
{
	var statuselement = document.getElementById('status');
	statuselement.innerText = 'Status: '+msg;
}	
function report(msg) 
{
	var report = document.getElementById('status');
	report.innerText = "Last Report: "+msg;
}
function start() 
{
	stats('Starting Luna - '+webSocket.readyState);
	
	document.getElementById('start').setAttribute('disabled', '');
	webSocket.send('OPEN');
}
function restart() 
{	
	document.getElementById('start').setAttribute('disabled', '');
	document.getElementById('restart').setAttribute('disabled', '');
	
	stats('Restarting Luna - '+webSocket.readyState);
	webSocket.send('RESTART');
	
	document.getElementById('start').removeAttribute('disabled');
}
function stop() 
{
	stats('Stopping Luna - '+webSocket.readyState);

	document.getElementById('restart').setAttribute('disabled', '');
	webSocket.send('CLOSE');
}