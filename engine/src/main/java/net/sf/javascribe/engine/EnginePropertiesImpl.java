package net.sf.javascribe.engine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import net.sf.javascribe.api.EngineProperties;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.LanguageSupport;

public class EnginePropertiesImpl implements EngineProperties {
	private List<Class<?>> scannedClasses = null;
	private JAXBContext componentContext = null;
	private Map<String,List<ProcessorEntry>> processors = null;
	private Map<String,LanguageSupport> languageSupport = null;
	
	public EnginePropertiesImpl(List<Class<?>> scannedClasses,JAXBContext componentContext,
			Map<String,List<ProcessorEntry>> processors,
			Map<String,LanguageSupport> languageSupport) {
		this.scannedClasses = scannedClasses;
		this.componentContext = componentContext;
		this.languageSupport = languageSupport;
		this.processors = processors;
	}

	public List<Class<?>> getScannedClasses() {
		return scannedClasses;
	}

	public JAXBContext getComponentContext() {
		return componentContext;
	}

	public Map<String, List<ProcessorEntry>> getProcessors() {
		return processors;
	}

	public Map<String, LanguageSupport> getLanguageSupport() {
		return languageSupport;
	}
	
	public EnginePropertiesImpl copy() {
		return new EnginePropertiesImpl(scannedClasses,componentContext,processors,languageSupport);
	}
	
	public static List<Class<?>> getScannedClassesThatImplement(List<Class<?>> scannedClasses,Class<?> _interface) {
		return JavascribeEngine.getScannedClassesThatImplement(scannedClasses, _interface);
	}

	public List<Class<?>> getScannedClassesOfInterface(Class<?> cl) {
		return getScannedClassesThatImplement(scannedClasses,cl);
	}

	public List<Class<?>> getScannedClassesOfAnnotation(Class<? extends Annotation> cl) {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		
		for(Class<?> sc : scannedClasses) {
			if (sc.isAnnotationPresent(cl)) {
				ret.add(sc);
			}
		}
		
		return ret;
	}

	/**
	 * Returns an input stream to the requested file.  The caller is responsible 
	 * for closing the input stream.
	 */
	public InputStream getClasspathResource(String path) throws JavascribeException,IOException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(path);

		if (url==null) {
			throw new JavascribeException("Couldn't load resource '"+path+"'");
		}
		return url.openStream();
	}

}

