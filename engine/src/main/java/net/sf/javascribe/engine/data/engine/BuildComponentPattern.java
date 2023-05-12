package net.sf.javascribe.engine.data.engine;

import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.BuildComponentProcessor;

public class BuildComponentPattern {

	private Class<? extends BuildComponent> componentClass;
	
	private Class<? extends BuildComponentProcessor> processorClass;

	/**
	 * Neither parameter is ever null.
	 * @param componentClass
	 * @param processorClass
	 */
	public BuildComponentPattern(Class<? extends BuildComponent> componentClass,Class<? extends BuildComponentProcessor> processorClass) {
		this.componentClass = componentClass;
		this.processorClass = processorClass;
	}

	public BuildComponentProcessor getProcessor(BuildComponent comp) {
		BuildComponentProcessor ret = null;
		
		try {
			ret = processorClass.getConstructor().newInstance();
		} catch(Exception e) {
			ret = null;
		}
		
		return ret;
	}

	public Class<? extends BuildComponent> getComponentClass() {
		return componentClass;
	}

	public Class<? extends BuildComponentProcessor> getProcessorClass() {
		return processorClass;
	}

}
