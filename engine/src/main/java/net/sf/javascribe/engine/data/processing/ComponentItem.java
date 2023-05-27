package net.sf.javascribe.engine.data.processing;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.RegisteredComponentPattern;

public class ComponentItem extends ProcessableBase implements Item {
	int id;
	Component component;
	Map<String,String> configs;
	RegisteredComponentPattern pattern;
	EngineResources engineResources;
	int originatorId;
	ApplicationFolderImpl folder;
	ProcessorLog log = null;
	private String name;
	ProcessingState state = ProcessingState.CREATED;
	
	public ComponentItem(int id, Component component, Map<String,String> configs, 
			RegisteredComponentPattern pattern,EngineResources engineResources, 
			int originatorId, ApplicationFolderImpl folder) {
		this.id = id;
		this.component = component;
		this.configs = configs;
		this.pattern = pattern;
		this.engineResources = engineResources;
		this.originatorId = originatorId;
		this.folder = folder;
		this.name = component.getComponentName();
		log = new ProcessorLog(name);
	}

	@Override
	public void setState(ProcessingState state) {
		this.state = state;
	}

	@Override
	public int getItemId() {
		return id;
	}

	@Override
	public int getOriginatorId() {
		return originatorId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPriority() {
		return component.getPriority();
	}

	@Override
	public List<ProcessorLogMessage> getMessages() {
		return log.getMessages(false);
	}

	@Override
	public boolean process() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearLogMessages() {
		log.getMessages(true);
	}

	@Override
	public ProcessorLog getLog() {
		return log;
	}

	@Override
	public Map<String, String> getConfigs() {
		return configs;
	}

	@Override
	public ProcessingState getState() {
		return state;
	}

}
