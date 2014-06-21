package net.sf.javascribe.api;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This class makes engine-level resources available to component processors.
 * @author DCS
 */
public interface EngineProperties {

	public List<Class<?>> getScannedClasses();
	
	public List<Class<?>> getScannedClassesOfInterface(Class<?> cl);

	public List<Class<?>> getScannedClassesOfAnnotation(Class<? extends Annotation> cl);

}

