package net.sf.javascribe.engine.manager;

import java.io.File;
import java.util.List;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.service.PluginService;

public class PluginManager {

	private PluginService pluginService;
	
	@ComponentDependency
	public void setPluginService(PluginService srv) {
		this.pluginService = srv;
	}

	public PluginManager() {
	}

	public void initEnginePlugins() {
		System.out.println("hi");
	}
	
	// Need to load patterns, plugins and language support from classpath
	public void initEngineResources() {
		List<Class<?>> classes = pluginService.findAllPlugins();
		System.out.println("hi");
	}

}

