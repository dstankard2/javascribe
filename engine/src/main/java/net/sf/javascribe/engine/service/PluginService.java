package net.sf.javascribe.engine.service;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.engine.ComponentDependency;

public class PluginService {

	private File[] libs = null;
	private Set<Class<?>> pluginClasses = null;
	
	@ComponentDependency(name = "jarFiles")
	public void setLibs(File[] libs) {
		this.libs = libs;
	}

	@SuppressWarnings("unchecked")
	public <T> Set<Class<T>> findClassesThatExtend(Class<T> superclass) {
		Set<Class<?>> pluginClasses = findAllPlugins();
		Set<Class<T>> ret = new HashSet<>();
		
		for(Class<?> cl : pluginClasses) {
			if (superclass.isAssignableFrom(cl)) {
				ret.add((Class<T>)cl);
			}
		}
		
		return ret;
	}

	public Set<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotation) {
		Set<Class<?>> pluginClasses = findAllPlugins();
		Set<Class<?>> ret = new HashSet<>();
		
		for(Class<?> cl : pluginClasses) {
			if (cl.isAnnotationPresent(annotation)) {
				ret.add(cl);
			}
		}
		
		return ret;
	}

	public Set<Class<?>> findAllPlugins() {
		if (pluginClasses==null) {
			pluginClasses = new HashSet<>();
			
			for(File lib : libs) {
				if (lib.isDirectory()) {
					scanDirectory(lib, pluginClasses, "");
				} else if (!lib.exists()) {
					System.err.println("Couldn't find lib location "+lib.getAbsolutePath());
				} else {
					scanJarFile(lib, pluginClasses);
				}
			}
		}
		
		return pluginClasses;
	}
	
	private void scanJarFile(File jarFile, Set<Class<?>> results) {
		JarFile jar = null;

		try {
			jar = new JarFile(jarFile);
			jar.entries().asIterator().forEachRemaining(e -> {
				String name = e.getName();
				if (name.endsWith(".class")) {
					scanClass(name, results);
				}
			});
		} catch(IOException e) {
			
		}
	}
	
	private void scanClass(String name, Set<Class<?>> results) {
		String className = name.substring(0, name.length() - 6);
		className = className.replace('/', '.');
		if (className.indexOf("module-info") >= 0) {
			// no-op
		} else if (className.indexOf("META-INF") >= 0) {
			// no-op
		} else {
			checkClass(className, results);
		}
	}

	private void scanDirectory(File lib, Set<Class<?>> results, String pkg) {
		File[] contents = lib.listFiles();
		for(File f : contents) {
			String name = f.getName();
			if (f.isDirectory()) {
				String newPkg = pkg.length()>0 ? pkg + "." + name : name;
				scanDirectory(f, results, newPkg);
			} else if (name.endsWith(".class")) {
				// It's a class file.  Load it and check if it has the Plugin annotation
				String className = pkg+'.'+name.substring(0, name.length() - 6);
				checkClass(className, results);
			}
		}
	}

	private void checkClass(String className, Set<Class<?>> results) {
		try {
			Class<?> cl = Class.forName(className);
			if (cl.getAnnotation(Plugin.class)!=null) {
				results.add(cl);
			}
		} catch(Throwable e) {
			// System.err.println("Couldn't scan a class named "+className);
		}
	}

}

