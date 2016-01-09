
function JSEvent(name,data) {
	this.name = name;
	this.data = data;
}

function EventDispatcher() {
	var _listeners = { };

	var _debug = false;
	
	var _domWatch = { };
	
	var _obs;
	function _initObserver() {
		if (_obs) return;

		if (window.MutationObserver) {
		    _obs = new MutationObserver(function (e) {
		      if ((e[0].removedNodes) && (e[0].removedNodes.length)) {
		        var l = e[0].removedNodes.length;
		        for(var i=0;i<l;i++) {
		          var node = e[0].removedNodes[i];
		          if (_domWatch[node]) {
		        	  if (_debug) console.log('A watched node has been removed - TODO: remove '+_domWatch[node].length+' listeners');
		          }
		        }
		      }
		    });
		    _obs.observe(document.body, { childList: true });
		}
	}
	
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
			_initObserver();
			if (typeof(_listeners[name]) =='undefined') {
				_listeners[name] = [];
			}
			var list = _listeners[name];
			list.push(callback);
			if (domElement) {
				if (!_domWatch.hasOwnProperty(domElement)) {
					_domWatch[domElement] = [ ];
				}
				_domWatch[domElement].push(callback);
			}
		},
		removeEventListener: function(callback) {
			removeListener(fn);
		},
		clearBindings: function() {
			_listeners = { };
			_domWatch = { };
		},
		// Dispatches the given event name with the given data
		dispatch: function(name,data) {
			if (typeof(_listeners[name]) =='undefined') return;
			var list = _listeners[name];
			var i;

			if (data==null) data = { };
			var event = new JSEvent(name,data);
			for(i=0;i<list.length;i++) {
				list[i](event);
			}
		},
		event: function(name,fn,elt) {
			if (fn) {
				this.addEventListener(name,fn,elt);
			} else {
				this.dispatch(name,{});
			}
		},
		debug: function(value) {
			_debug = value;
		}
	}
		
}

