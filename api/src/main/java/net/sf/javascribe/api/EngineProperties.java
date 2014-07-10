package net.sf.javascribe.api;

import java.io.IOException;
import java.io.InputStream;
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
	
	/**
	 * Returns a resource from a JAR file in the Javascribe lib directory.
	 * @param path Path to resource, such as ("META-INF/myfile.template").
	 * @return Input stream to resource.  Client is responsible for managing it.
	 * @throws IOException
	 * @throws JavascribeException
	 */
	public InputStream getClasspathResource(String path) throws IOException,JavascribeException;

}

