package net.sf.javascribe.engine;

import java.lang.reflect.Method;

public class ProcessorEntry {
	private Class<?> processorClass = null;
	private Method method = null;
	
	public ProcessorEntry(Class<?> processorClass, Method method) {
		this.processorClass = processorClass;
		this.method = method;
	}

	public Class<?> getProcessorClass() {
		return processorClass;
	}

	public void setProcessorClass(Class<?> processorClass) {
		this.processorClass = processorClass;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
}
