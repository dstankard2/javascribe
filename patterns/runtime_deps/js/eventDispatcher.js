
function JSEvent(name,data) {
	this.name = name;
	this.data = data;
}

function JSController() {
	this.listeners = { },
	
	this.clearBindings = function() {
		this.listeners = { };
	}
	
	// Adds the specified callback function as a listener to the specified event.
	this.addEventListener = function(name,callback) {
		if (typeof(this.listeners[name]) =='undefined') {
			this.listeners[name] = new Array();
		}
		var list = this.listeners[name];
		list.push(callback);
	};
		
	// Dispatches the given event name with the given data
	this.dispatch = function(name,data) {
		if (typeof(this.listeners[name]) =='undefined') return;
		var list = this.listeners[name];
		var i;

		if (data==null) data = { };
		var event = new MVCEvent(name,data);
		for(i=0;i<list.length;i++) {
			list[i](event);
		}
	};

}

