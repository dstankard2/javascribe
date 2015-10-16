
function JSEvent(name,data) {
	this.name = name;
	this.data = data;
}

function EventDispatcher() {
	var listeners = { };

	var domWatch = { };
	
	var obs;
	function _initObserver() {
		if (window.MutationObserver) {

		    obs = new MutationObserver(function (e) {
		      if ((e[0].removedNodes) && (e[0].removedNodes.length)) {
		        var l = e[0].removedNodes.length;
		        for(var i=0;i<l;i++) {
		          var node = e[0].removedNodes[i];
		          if (node._elt) {
		            console.log('A node was removed with mark as '+node._elt);
		          }
		        }
		      }
		    });
		    obs.observe(document.body, { childList: true });
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
			if (!obs) _initObserver();
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
		event: function(name,fn,elt) {
			if (fn) {
				this.addEventListener(name,fn,elt);
			} else {
				this.dispatch(name,{});
			}
		}
	}
		

}

