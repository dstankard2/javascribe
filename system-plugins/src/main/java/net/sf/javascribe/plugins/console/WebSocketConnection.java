package net.sf.javascribe.plugins.console;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketConnection extends Endpoint {

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		ConsolePlugin plugin = ConsolePlugin.get();
		plugin.removeClient(session);
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		super.onError(session, throwable);
	}

	@Override
	public void onOpen(Session session, EndpointConfig cfg) {
		ConsolePlugin plugin = ConsolePlugin.get();
		plugin.addClient(session);
	}

}
