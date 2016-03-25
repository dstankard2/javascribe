
function JSEvent(name,data) {
	this.name = name;
	this.data = data;
}

function EventDispatcher() {
	var _listeners = { };
	
	var _domListeners = {};

	var _debug = false;

	function _removeListeners(ele) {
		if (_domListeners[ele]) {
			var objs = _domListeners[ele];
			var num = 0;
			for(var i=0;i<objs.length;i++) {
				var obj = objs[i];
				_removeListener(obj.event,obj.fn);
				num++;
			}
			_domListeners[ele] = undefined;
			//console.log('I removed '+num+' listeners associated with element '+ele._elt);
		}
	}
	
	function _removeListener(event,fn) {
		var listeners = _listeners[event];
		var i = listeners.indexOf(fn);
		if (i>=0) {
			listeners.splice(i,1);
		}
	}
	
	var _obs;
	function _initObserver() {
		if (_obs) return;
		if (window.MutationObserver) {
			_obs = new MutationObserver(function (e) {
				for(var i=0;i<e.length;i++) {
					var record = e[i];
					if ((record.removedNodes) && (record.removedNodes.length)) {
						var l = e[i].removedNodes;
						for(var j=0;j<l.length;j++) {
							var node = l[j];
							if (node._elt) {
								_removeListeners(node);
							}
						}
					}
				}
			});
			var config = {
				attributes: true,
				childList: true,
				characterData: true,
				subtree: true
			};
			//_obs.observe(document.body,config);
		}
	}

	return {
		addEventListener: function(name,callback,domElement) {
			_initObserver();
			if (_listeners[name]==undefined) {
				_listeners[name] = [];
			}
			var list = _listeners[name];
			list.push(callback);
			if (domElement) {
				var obj = {
					fn: callback,
					event: name
				};
				if (!_domListeners[domElement]) {
					_domListeners[domElement] = [];
				}
				_domListeners[domElement].push(obj);
				//console.log('I added an event listener contingent on element '+domElement._elt);
			}
		},
		removeEventListener: function(callback) {
			removeListener(fn);
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

