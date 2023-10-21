package net.sf.javascribe.engine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.javascribe.api.ApplicationContext;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.data.processing.LogContext;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.util.LogUtil;

public class EngineResources implements ApplicationContext,LogContext {

	private PluginService pluginService;
	
	private EngineProperties engineProperties;

	private ProcessorLog log = null;

	public EngineResources() {
		this.log = new ProcessorLog("ENGINE", this, ProcessorLogLevel.DEBUG);
	}

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

	@Override
	public Log getEngineLog() {
		return log;
	}

	@Override
	public void appendMessage(ProcessorLogMessage message) {
		LogUtil logUtil = ComponentContainer.get().getComponent(LogUtil.class);
		logUtil.outputLogMessage(message);
	}
	
	@Override
	public List<ProcessorLogMessage> getMessages() {
		return new ArrayList<>();
	}

}
