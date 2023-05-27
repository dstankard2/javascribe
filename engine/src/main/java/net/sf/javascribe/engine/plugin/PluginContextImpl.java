package net.sf.javascribe.engine.plugin;

import java.util.Set;

import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.plugin.PluginContext;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.service.EngineResources;

public class PluginContextImpl implements PluginContext {
	private EngineProperties props = null;
	private EngineResources engineResources;

	public PluginContextImpl(EngineResources jasperResources, EngineProperties props) {
		super();
		this.engineResources = jasperResources;
		this.props = props;
	}

	@Override
	public String getEngineProperty(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}

	@Override
	public boolean getBooleanEngineProperty(String name, boolean defaultValue) {
		return props.getBoolean(name, defaultValue);
	}

	@Override
	public Log getLog() {
		return null;
	}

	@Override
	public <T> Set<Class<T>> getPlugins(Class<T> superClass) {
		return engineResources.getPlugins(superClass);
	}

}