package net.sf.javascribe.engine;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.LanguageSupport;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentConfigElement;
import net.sf.javascribe.api.config.ComponentSet;

/**
 * <p>The Thread's current context class loader should have all required Javascribe libs 
 * including pattern definitions and processors.</p>
 * <p>When invoke is called, runtime libraries are scanned for the @Scannable annotation.  
 * For this to happen, the libFiles or javascribeHome instance variable or both must be 
 * set.  If both are set, libFiles overrides javascribeHome.  If libFiles is not 
 * initialized (==null) then <javascribeHome>/lib will be queried for *.jar and class file 
 * folders.  Once libFiles is initialized, all classes found are scanned for the @Scannable 
 * interface and interpretted as component pattern definitions or component processors.</p>
 * @author Dave
 */
public class JavascribeEngine {
	File[] libFiles = null;
	String javascribeHome = null;
	EnginePropertiesImpl engineProperties = null;

	public JavascribeEngine(File[] libFiles) {
		this.libFiles = libFiles;
	}

	public JavascribeEngine(File[] libFiles,String javascribeHome) {
		this.libFiles = libFiles;
		this.javascribeHome = javascribeHome;
	}

	public JavascribeEngine(String javascribeHome) {
		this.javascribeHome = javascribeHome;
		libFiles = null;
	}

	public void resetEngine(String javascribeHome) {
		this.javascribeHome = null;
		this.libFiles = null;
		resetEngine();
	}

	public void resetEngine(File[] libFiles) {
		this.libFiles = libFiles;
		javascribeHome = null;
		resetEngine();
	}

	// Forces the JavascribeEngine to re-scan the classpath.
	// If javascribeHome is specified, the lib directory will be re-read.
	// Does not update javascribeHome or lib locations.
	public void resetEngine() {
		engineProperties = null;
		if (javascribeHome!=null) {
			libFiles = null;
		}
	}
	
	private void initLog4j() {
		String filename = javascribeHome + File.separatorChar + "conf" + 
				File.separatorChar + "log4j.xml";
//		PropertyConfigurator.configure(filename);
		DOMConfigurator.configure(filename);
	}

	protected synchronized EnginePropertiesImpl getEngineProperties() throws JavascribeException {
		if (this.engineProperties==null) {
			if ((this.libFiles==null) && (this.javascribeHome==null)) {
				throw new JavascribeException("Javascribe Engine has insufficient information to execute");
			}

			initLog4j();

			if (libFiles==null) {
				readLibs();
			}
			List<Class<?>> classes = null;

			try {
				classes = AnnotationHelper.findAnnotatedClasses(Scannable.class, libFiles);
			} catch(Exception e) {
				throw new JavascribeException("Exception while locating scannable classes",e);
			}
			// Locate classes for the JAXBContext.
			ArrayList<Class<?>> jaxbClasses = (ArrayList<Class<?>>)getScannedClassesThatImplement(classes, ComponentConfigElement.class);
			jaxbClasses.add(ComponentSet.class);
			Class<?>[] jaxbClassArr = jaxbClasses.toArray(new Class<?>[jaxbClasses.size()]);
			JAXBContext ctx = null;

			try {
				ctx = JAXBContext.newInstance(jaxbClassArr);
			} catch(Exception e) {
				throw new JavascribeException("Error building Javascribe component JAXB context",e);
			}

			Map<String,List<ProcessorEntry>> processors = new HashMap<String,List<ProcessorEntry>>();

			List<Class<?>> processorClasses = getScannedClassesWithAnnotation(classes, Processor.class);
			for(Class<?> cl : processorClasses) {
				scanForProcessorMethods(processors,cl);
			}

			List<Class<?>> langSupportClasses = getScannedClassesThatImplement(classes, LanguageSupport.class);
			Map<String,LanguageSupport> languageSupport = new HashMap<String,LanguageSupport>();
			try {
				for(Class<?> cl : langSupportClasses) {
					LanguageSupport supp = (LanguageSupport)cl.newInstance();
					languageSupport.put(supp.languageName(), supp);
				}
			} catch(Exception e) {
				throw new JavascribeException("Exception while loading language support",e);
			}
			engineProperties = new EnginePropertiesImpl(
				classes,ctx,processors,languageSupport);
		}

		return engineProperties.copy();
	}

	private void scanForProcessorMethods(Map<String,List<ProcessorEntry>> processors,Class<?> cl) {
		Method methods[] = cl.getMethods();

		for(Method m : methods) {
			if (m.isAnnotationPresent(ProcessorMethod.class)) {
				ProcessorMethod pm = m.getAnnotation(ProcessorMethod.class);
				Class<?> comp = pm.componentClass();
				String name = comp.getName();
				List<ProcessorEntry> procs = processors.get(name);
				if (procs==null) {
					procs = new ArrayList<ProcessorEntry>();
					processors.put(name, procs);
				}
				procs.add(new ProcessorEntry(cl,m));
			}
		}
	}

	public static ArrayList<Class<?>> getScannedClassesThatImplement(List<Class<?>> scannedClasses,Class<?> _interface) {
		ArrayList<Class<?>> ret = new ArrayList<Class<?>>();

		if (!_interface.isInterface()) {
			return ret;
		}
		for(Class<?> cl : scannedClasses) {
			if (_interface.isAssignableFrom(cl)) {
				ret.add(cl);
			}
		}

		return ret;
	}

	public static ArrayList<Class<?>> getScannedClassesWithAnnotation(List<Class<?>> scannedClasses,Class<? extends Annotation> annotation) {
		ArrayList<Class<?>> ret = new ArrayList<Class<?>>();

		for(Class<?> cl : scannedClasses) {
			if (cl.isAnnotationPresent(annotation)) {
				ret.add(cl);
			}
		}

		return ret;
	}



	public void invoke(File zipFile) throws JavascribeException {
		EnginePropertiesImpl engineProps = null;
		ApplicationReader reader = null;
		ZipFile zip = null;
		ApplicationDefinition def = null;

		try {
			engineProps = getEngineProperties();
			reader = new ApplicationReader(engineProps.getComponentContext());
			zip = new ZipFile(zipFile);
			def = reader.readApplication(zip);
			CodeGenerator generator = new CodeGenerator(engineProps,def);
			generator.generate();
		} catch(IOException e) {
			throw new JavascribeException("Exception while running Javascribe",e);
		} catch(JAXBException e) {
			throw new JavascribeException("Exception while running Javascribe",e);
		}
	}

	private void readLibs() throws JavascribeException {
		File dir = new File(javascribeHome);

		if (!dir.exists()) {
			throw new JavascribeException("Invalid JAVASCRIBE_HOME specified.");
		}
		if (!dir.isDirectory()) {
			throw new JavascribeException("Invalid JAVASCRIBE_HOME specified.");
		}

		File libDir = new File(dir,"lib");
		if (!libDir.exists()) {
			throw new JavascribeException("Invalid JAVASCRIBE_HOME specified.");
		}
		if (!libDir.isDirectory()) {
			throw new JavascribeException("Invalid JAVASCRIBE_HOME specified.");
		}
		ArrayList<File> files = new ArrayList<File>();
		String names[] = libDir.list();
		for(String name : names) {
			if (name.endsWith(".jar")) {
				File f = new File(libDir,name);
				files.add(f);
			}
		}
		libFiles = new File[files.size()];
		files.toArray(libFiles);
	}

}

