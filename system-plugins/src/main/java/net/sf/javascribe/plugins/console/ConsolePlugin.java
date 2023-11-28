package net.sf.javascribe.plugins.console;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.plugin.EnginePlugin;
import net.sf.javascribe.api.plugin.PluginContext;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;
import net.sf.javascribe.plugins.console.servlet.ConsoleServlet;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

@Plugin
// Starts an embedded Tomcat server which serves up a Javascribe console
public class ConsolePlugin implements EnginePlugin {
	PluginContext ctx = null;
	
	private static ConsolePlugin instance = null;
	private List<Session> clients = new ArrayList<>();
	private Map<String,ApplicationSnapshot> lastSnapshots = new HashMap<>();
	
	public static ConsolePlugin get() { return instance; }
	public synchronized void addClient(Session session) {
		clients.add(session);
		lastSnapshots.values().forEach(snapshot -> {
			try {
				this.send(session, snapshot);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		
	}
	public synchronized void removeClient(Session session) {
		try {
			session.close();
		} catch(Exception e) {
			System.err.println("Couldn't close session");
		}
		clients.remove(session);
	}

	@Override
	public void setPluginContext(PluginContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public String getPluginName() {
		return "Console";
	}

	@Override
	public String getPluginConfigName() {
		return "engine.plugin.console";
	}

	Tomcat tomcat = null;
	
	// Setting up embed tomcat (maybe update pattern?)
	// https://www.baeldung.com/tomcat-programmatic-setup
	@Override
	public void engineStart() {
		File tempDir = null;
		
		try {
			//tempDir = new File("C:\\git\\javascribe\\system-plugins\\src\\main\\webapp");
			tempDir = Files.createTempDirectory("javascribe_console_tomcat").toFile();
			String tempPath = tempDir.getAbsolutePath();
			tempDir.deleteOnExit();
			this.ctx.getLog().info("Starting console plugin");
	
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(5010);
			tomcat.enableNaming();
			StandardContext context = (StandardContext) tomcat.addWebapp("/",
					tempPath);
			WebResourceRoot resources = new StandardRoot(context);
			context.setResources(resources);
			Tomcat.addServlet(context, "ConsoleServlet",
					new ConsoleServlet());
			context.addServletMappingDecoded("/*", "ConsoleServlet");
	
			//context.addServletContainerInitializer(new WsSci(), null);

			tomcat.start();
			tomcat.getConnector().start();

			ServerContainer serverContainer = (ServerContainer) context.getServletContext().getAttribute("javax.websocket.server.ServerContainer");			
			ServerEndpointConfig cfg = ServerEndpointConfig.Builder.create(WebSocketConnection.class, "/connect").build();
			serverContainer.addEndpoint(cfg);

			instance = this;
			
			if (Desktop.isDesktopSupported()) {
				try {
				Desktop.getDesktop().browse(new URI("http://localhost:5010/console"));
				} catch(URISyntaxException e) {
					e.printStackTrace();
				}
			}

		} catch(IOException | LifecycleException | ServletException | DeploymentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void scanFinish(ApplicationSnapshot applicationSnapshot) {
		lastSnapshots.put(applicationSnapshot.getName(), applicationSnapshot);
			this.clients.forEach(session -> {
				try {
					send(session, applicationSnapshot);
				} catch(IOException e) {
					e.printStackTrace();
					removeClient(session);
				} catch(RuntimeException e) {
					e.printStackTrace();
					removeClient(session);
				}
			});
	}

	private void send(Session session, ApplicationSnapshot snapshot) throws JsonProcessingException,IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(snapshot);
		
		session.getBasicRemote().sendText(json);
	}
	
}
