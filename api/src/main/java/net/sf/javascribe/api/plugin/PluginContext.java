package net.sf.javascribe.api.plugin;

import java.util.Set;

import net.sf.javascribe.api.logging.Log;

public interface PluginContext {

	String getEngineProperty(String name,String defaultValue);

	boolean getBooleanEngineProperty(String name,boolean defaultValue);

	Log getLog();

	public <T> Set<Class<T>> getPlugins(Class<T> superClass);

}

