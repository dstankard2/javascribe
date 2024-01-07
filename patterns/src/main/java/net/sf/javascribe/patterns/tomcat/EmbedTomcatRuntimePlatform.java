package net.sf.javascribe.patterns.tomcat;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.patterns.web.JavaWebappRuntimePlatform;

/**
 * Runtime platform implementation for an Embed Tomcat jar.  The jar should have a Main Class defined.
 * This runtime platform is deployed by invoking "java -jar <jarFile>"
 * @author DCS
 *
 */
public class EmbedTomcatRuntimePlatform extends JavaWebappRuntimePlatform {

	@Setter
	@Getter
	List<DataSourceInfo> dataSources = new ArrayList<>();

	public void addWebsocketEndpoint(String url,String className) {
		super.addWebsocketEndpoint(url, className);
		this.dependencies.add("javax.websocket-api");
		this.dependencies.add("tomcat-websocket");
		this.dependencies.add("tomcat-embed-websocket");
	}

	public void addDependency(String name) {
		dependencies.add(name);
	}
	
}
