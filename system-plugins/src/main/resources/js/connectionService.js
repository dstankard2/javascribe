
var _socket;

function _start() {
	_socket = new WebSocket("ws://localhost:5010/connect");
	_socket.onopen = function() {
		console.log('got a socket connection opened');
	}

	_socket.onmessage = function(evt) {
		let data = evt.data;
		let snapshot = JSON.parse(data);
		window.addApplication(snapshot);
	}
	
}

export const connectionService = {
	start: _start
};

