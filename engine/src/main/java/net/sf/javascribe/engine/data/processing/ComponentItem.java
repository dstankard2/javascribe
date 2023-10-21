package net.sf.javascribe.engine.data.processing;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.RegisteredComponentPattern;
import net.sf.javascribe.engine.util.ConfigUtil;

public class ComponentItem extends ProcessableBase implements Item {
	int id;
	Component component;
	Map<String,String> configs;
	RegisteredComponentPattern pattern;
	//EngineResources engineResources;
	int originatorId;
	ApplicationFolderImpl folder;
	ProcessorLog log = null;
	private String name;
	ProcessingState state = ProcessingState.CREATED;
	private ApplicationData application;

	public ComponentItem(int id, Component component, Map<String,String> configs, 
			RegisteredComponentPattern pattern,
			int originatorId, ApplicationFolderImpl folder, ApplicationData application) {
		this.id = id;
		this.component = component;
		this.configs = configs;
		this.pattern = pattern;
		this.originatorId = originatorId;
		this.folder = folder;
		this.name = component.getComponentName();
		this.application = application;
		log = new ProcessorLog(name, application, folder.getLogLevel());
	}

	public Component getComponent() {
		return component;
	}

	@Override
	public ApplicationFolderImpl getFolder() {
		return folder;
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public boolean process() {
		boolean success = true;
		ConfigUtil configUtil = ComponentContainer.get().getComponent(ConfigUtil.class);

		success = configUtil.populateConfigurations(component, log, configs);

		if (success) {
			try {
				Set<Class<ComponentProcessor>> compClasses = pattern.getProcessorClasses();
				for(Class<ComponentProcessor> procClass : compClasses) {
					ProcessorContextImpl ctx = new ProcessorContextImpl(
						application, id, configs, folder, log
					);
					ComponentProcessor proc = procClass.getConstructor().newInstance();
					proc.process(component,  ctx);
				}
			} catch(JavascribeException e) {
				this.log.error(e.getMessage());
				success = false;
			} catch(InstantiationException e) {
				this.log.error("Couldn't invoke component processor - "+e.getMessage(), e);
				success = false;
			} catch(IllegalAccessException e) {
				this.log.error("Couldn't invoke component processor - "+e.getMessage(), e);
				success = false;
			} catch(NoSuchMethodException e) {
				this.log.error("Couldn't invoke component processor - "+e.getMessage(), e);
				success = false;
			} catch(InvocationTargetException e) {
				this.log.error("Couldn't invoke component processor - "+e.getMessage(), e);
				success = false;
			}
		}
		
		return success;
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
