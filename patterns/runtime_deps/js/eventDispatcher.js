
function EventDispatcher() {
	var _listeners = { };

	var _listenerEntries = EventDispatcher.$$listenerEntries;
	
	var _domListeners = {};

	var _debug = false;

	function _removeListeners(node) {
		var list = EventDispatcher.$$listenerEntries;
		for(var i=0;i<list.length;i++) {
			var entry = list[i];
			if (node==entry.element) {
				var dispatcher = entry.dispatcher;
				var event = entry.event;
				var callback = entry.callback;
				dispatcher.removeListener(event,callback);
			}
		}
	}
	
	function _removeListenersForAll(node) {
		if (node.$$hasListeners) {
			_removeListeners(node);
			node.$$hasListeners = undefined;
		}
		var children = node.childNodes;
		for(var i=0;i<children.length;i++) {
			_removeListenersForAll(children[i]);
		}
	}

	function _checkRemove(elt) {
		if (elt.$$remove) {
			try {
				elt.$$remove();
				elt.$$remove = undefined;
			} catch(err) {
				console.error(err);
			}
		}
		var children = elt.childNodes;
		for(var i=0;i<children.length;i++) {
			_checkRemove(children[i]);
		}
	}

	function _initObserver() {
		if (EventDispatcher.$$observer) return;
		if (EventDispatcher.$$domListener) return;
		var mutationObserver = window.MutationObserver ||
			window.WebKitMutationObserver || 
			window.MozMutationObserver;
		if (mutationObserver) {
			EventDispatcher.$$observer = new mutationObserver(function (e) {
				for(var i=0;i<e.length;i++) {
					var record = e[i];
					if ((record.removedNodes) && (record.removedNodes.length)) {
						var l = record.removedNodes;
						for(var j=0;j<l.length;j++) {
							var node = l[j];
							_removeListenersForAll(node);
							_checkRemove(node);
						}
					}
				}
				if (_debug) {
					console.log('Removed listeners '+EventDispatcher.$$listenerEntries.length);
				}
			});
			var config = {
				childList: true,
				subtree: true
			};
			EventDispatcher.$$observer.observe(document.body,config);
		}
	}

	return {
		addEventListener: function(name,callback,domElement) {
			var inst = this;

			_initObserver();

			if (_listeners[name]==undefined) {
				_listeners[name] = [];
			}
			var list = _listeners[name];
			list.push(callback);
			if (domElement) {
				var listeners = EventDispatcher.$$listenerEntries;
				var listenerEntry = {
					dispatcher: inst,
					event: name,
					callback: callback,
					element: domElement
				};
				listeners.push(listenerEntry);
				domElement.$$hasListeners = true;
			}
		},
		// Dispatches the given event name with the given data
		dispatch: function(name) {
			if (typeof(_listeners[name]) =='undefined') return;
			var list = _listeners[name];
			var i;

			for(i=0;i<list.length;i++) {
				list[i]();
			}
		},
		event: function(name,fn,elt) {
			if (fn) {
				this.addEventListener(name,fn,elt);
			} else {
				this.dispatch(name);
			}
		},
		debug: function(value) {
			_debug = value;
		},
		removeListener: function(event,callback) {
			var listeners = EventDispatcher.$$listenerEntries;
			for(var i=0;i<listeners.length;i++) {
				var entry = listeners[i];
				if (entry.dispatcher==this) {
					if ((entry.event==event) && (entry.callback==callback)) {
						listeners.splice(i,1);
						i--;
					}
				}
			}
		}
	}

}

EventDispatcher.$$listenerEntries = [];

