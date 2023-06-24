package net.sf.javascribe.engine.service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
					System.err.println("Can't scan jar files yet");
				}
			}
		}
		
		return pluginClasses;
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
				try {
					Class<?> cl = Class.forName(className);
					if (cl.getAnnotation(Plugin.class)!=null) {
						results.add(cl);
					}
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
					// no-op
				}
			}
		}
	}

}

