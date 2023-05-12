package net.sf.javascribe.engine.data.engine;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.config.Component;

public class ComponentPattern {

	private Class<? extends Component> componentClass = null;
	
	public ComponentPattern(Class<? extends Component> componentClass) {
		this.componentClass = componentClass;
	}

	private List<RegisteredProcessor> processors = new ArrayList<>();

	public List<RegisteredProcessor> getProcessors() {
		return processors;
	}
	
	public Class<? extends Component> getPatternClass() {
		return componentClass;
	}

}
