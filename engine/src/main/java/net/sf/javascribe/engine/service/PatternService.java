package net.sf.javascribe.engine.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.config.Property;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.ComponentFile;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;

// TODO: Fix circular dependency between pattern service and processing util
public class PatternService {

	private PluginService pluginService;
	@ComponentDependency
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}

	private Map<String,RegisteredComponentPattern> componentPatterns = new HashMap<>();
	private Map<String,RegisteredBuildComponentPattern> buildComponentPatterns = new HashMap<>();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void initializePatterns() {
		Set<Class<Component>> patterns = pluginService.findClassesThatExtend(Component.class);
		Set<Class<ComponentProcessor>> processors = pluginService.findClassesThatExtend(ComponentProcessor.class);
		Set<Class<BuildComponentProcessor>> buildProcessors = pluginService.findClassesThatExtend(BuildComponentProcessor.class);

		for(Class<?> pattern : patterns) {
			String name = pattern.getName();
			if ((pattern==BuildComponent.class) || (pattern==Component.class)) {
				continue;
			}
			if (BuildComponent.class.isAssignableFrom(pattern)) {
				addBuildComponentPattern(name, (Class<BuildComponent>)pattern, buildProcessors);
			} else {
				addComponentPattern(name, (Class<Component>)pattern, processors);
			}
		}
		
	}
	
	public RegisteredComponentPattern getPattern(Component component) {
		String name = component.getClass().getSimpleName();
		RegisteredComponentPattern pattern = componentPatterns.get(name);
		return pattern;
	}

	public ComponentItem createComponentItem(int originatorId, Component component, 
			ComponentFile file, ApplicationData application) {
		ComponentItem ret = null;
		int id = application.getProcessingData().nextId();
		ApplicationFolderImpl folder = file.getFolder();
		Map<String,String> configs = createConfigs(component, file);
		String patternName = component.getClass().getName();
		RegisteredComponentPattern pattern = componentPatterns.get(patternName);

		ret = new ComponentItem(id, component, configs, pattern, 
				originatorId, folder, application);
		
		return ret;
	}

	// TODO: Return null if pattern has no processor.  Handle appropriately.
	public BuildComponentItem createBuildComponentItem(BuildComponent buildComp, 
			ComponentFile compFile, ApplicationData application) {
		BuildComponentItem ret = null;
		int id = application.getProcessingData().nextId();
		Map<String,String> configs = createConfigs(buildComp, compFile);
		String patternName = buildComp.getClass().getName();
		RegisteredBuildComponentPattern pattern = buildComponentPatterns.get(patternName);
		
		ret = new BuildComponentItem(id, buildComp, compFile.getFolder(), pattern, configs, application);
		
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	private void addBuildComponentPattern(String name, Class<BuildComponent> comp, Set<Class<BuildComponentProcessor>> buildProcessors) {
		RegisteredBuildComponentPattern pattern = RegisteredBuildComponentPattern.builder()
				.componentClass(comp).build();
		for(Class<BuildComponentProcessor> cl : buildProcessors) {
			ParameterizedType processorType = findBuildProcessorType(cl);

			if (processorType.getActualTypeArguments().length!=1) {
				continue;
			}
			if (processorType.getActualTypeArguments()[0]==comp) {
				pattern.setProcessorClass(cl);
			}
		}
		this.buildComponentPatterns.put(name, pattern);
	}

	@SuppressWarnings("rawtypes")
	private ParameterizedType findBuildProcessorType(Class<BuildComponentProcessor> cl) {
		for(Type type : cl.getGenericInterfaces()) {
			if (type.getTypeName().contains("BuildComponentProcessor")) {
				return (ParameterizedType)type;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private void addComponentPattern(String name, Class<Component> comp, Set<Class<ComponentProcessor>> processorClasses) {
		RegisteredComponentPattern pattern = RegisteredComponentPattern.builder()
				.componentClass(comp)
				.build();
		
		for(Class<ComponentProcessor> cl : processorClasses) {
			ParameterizedType processorType = findProcessorType(cl);
			
			if (processorType.getActualTypeArguments().length!=1) {
				continue;
			}
			if (processorType.getActualTypeArguments()[0]==comp) {
				pattern.getProcessorClasses().add(cl);
			}
		}
		
		componentPatterns.put(name, pattern);
	}

	@SuppressWarnings("rawtypes")
	private ParameterizedType findProcessorType(Class<ComponentProcessor> cl) {
		for(Type type : cl.getGenericInterfaces()) {
			if (type.getTypeName().contains("ComponentProcessor")) {
				return (ParameterizedType)type;
			}
		}
		
		return null;
	}
	
	private Map<String,String> createConfigs(Component component, ComponentFile file) {
		ApplicationFolderImpl folder = file.getFolder();
		Map<String,String> configs = folder.getProperties();
		
		for(Property prop : file.getComponentSet().getProperty()) {
			configs.put(prop.getName(), prop.getValue());
		}
		for(Property prop : component.getProperty()) {
			configs.put(prop.getName(), prop.getValue());
		}
		
		return configs;
	}
}

