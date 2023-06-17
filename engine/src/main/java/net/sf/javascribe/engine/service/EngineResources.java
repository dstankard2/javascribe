package net.sf.javascribe.engine.service;

import java.util.Set;

import net.sf.javascribe.api.ApplicationContext;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineProperties;

public class EngineResources implements ApplicationContext {

	private PluginService pluginService;
	
	private EngineProperties engineProperties;

	@ComponentDependency
	public void setEngineProperties(EngineProperties engineProperties) {
		this.engineProperties = engineProperties;
	}

	@ComponentDependency
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}

	@Override
	public String getEngineProperty(String name) {
		return engineProperties.getProperty(name, null);
	}
	
	public EngineProperties getEngineProperties() {
		return engineProperties;
	}

	@Override
	public <T> Set<Class<T>> getPlugins(Class<T> superClass) {
		return pluginService.findClassesThatExtend(superClass);
	}

}
