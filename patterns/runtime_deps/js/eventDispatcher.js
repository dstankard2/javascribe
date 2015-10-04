
function JSEvent(name,data) {
	this.name = name;
	this.data = data;
}

function EventDispatcher() {
	var listeners = { };

	var domWatch = { };
	
	var observer = new MutationObserver(function(mutations) {
		mutations.forEach(function(m) {
			if (m.type!='childList') return;
			if (!m.removedNodes) return;
			for(var i=0;i<m.removedNodes.length;i++) {
				console.log('detected that a node is removed from DOM');
				if (domWatch[m.removedNodes[i]]) {
					var arr = domWatch[m.removedNodes[i]];
					for(var i2=0;i2<arr.length;i2++) {
						removeListener(arr[i2]);
					}
				}
			}
		});
	});
	
	var removeListener = function(fn) {
		for(var k in listeners) {
			var arr = listeners[k];
			var i = arr.indexOf(fn);
			if (i>=0) {
				arr.splice(i,1);
			}
		}
	}
	
	return {
		// Adds the specified callback function as a listener to the specified event.
		addEventListener: function(name,callback,domElement) {
			if (typeof(listeners[name]) =='undefined') {
				listeners[name] = new Array();
			}
			var list = listeners[name];
			list.push(callback);
			if (domElement) {
				if (!domWatch.hasOwnProperty(domElement)) {
					domWatch[domElement] = [ ];
				}
				domWatch[domElement].push(callback);
			}
		},
		removeEventListener: function(callback) {
			removeListener(fn);
		},
		clearBindings: function() {
			listeners = { };
			domWatch = { };
		},
		// Dispatches the given event name with the given data
		dispatch: function(name,data) {
			if (typeof(listeners[name]) =='undefined') return;
			var list = listeners[name];
			var i;

			if (data==null) data = { };
			var event = new JSEvent(name,data);
			for(i=0;i<list.length;i++) {
				list[i](event);
			}
		},
		event: function(name,fn) {
			if (fn) {
				addEventListener(name,fn);
			} else {
				dispatch(name,{});
			}
		}
	}
		

}

