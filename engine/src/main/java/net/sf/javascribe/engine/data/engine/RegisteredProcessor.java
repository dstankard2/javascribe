package net.sf.javascribe.engine.data.engine;

import net.sf.javascribe.api.ComponentProcessor;

public class RegisteredProcessor {

	private Class<? extends ComponentProcessor> processorClass = null;
	
	public RegisteredProcessor(Class<? extends ComponentProcessor> processorClass) {
		super();
		this.processorClass = processorClass;
	}

	public Class<? extends ComponentProcessor> getProcessorClass() {
		return processorClass;
	}
	public void setProcessorClass(Class<? extends ComponentProcessor> processorClass) {
		this.processorClass = processorClass;
	}

}

