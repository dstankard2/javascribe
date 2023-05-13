package net.sf.javascribe.engine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.engine.data.engine.BuildComponentPattern;
import net.sf.javascribe.engine.data.engine.ComponentPattern;

public class PatternService {

	private PluginService pluginService;
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}
	
	private Map<String,ComponentPattern> componentPatterns = new HashMap<>();
	private Map<String,BuildComponentPattern> buildComponentPatterns = new HashMap<>();

	@SuppressWarnings("unchecked")
	public void initializePatterns() {
		Set<Class<Component>> patterns = pluginService.findClassesThatExtend(Component.class);
		Set<Class<ComponentProcessor>> processors = pluginService.findClassesThatExtend(ComponentProcessor.class);
		Set<Class<BuildComponentProcessor>> buildProcessors = pluginService.findClassesThatExtend(BuildComponentProcessor.class);

		for(Class<?> pattern : patterns) {
			String name = pattern.getName();
			if (BuildComponent.class.isAssignableFrom(pattern)) {
				addBuildComponentPattern((Class<BuildComponent>)pattern, buildProcessors);
			} else {
				addComponentPattern((Class<Component>)pattern, processors);
			}
		}
	}
	
	private void addBuildComponentPattern(Class<BuildComponent> comp, Set<Class<BuildComponentProcessor>> buildProcessors) {
		System.out.println("hi");
	}

	private void addComponentPattern(Class<Component> comp, Set<Class<ComponentProcessor>> buildProcessors) {
	}

}
