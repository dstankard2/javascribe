package net.sf.javascribe.engine.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.engine.ComponentDependency;

public class PluginService {

	private File[] libs = null;
	
	@ComponentDependency(name = "jarFiles")
	public void setLibs(File[] libs) {
		this.libs = libs;
	}

	public List<Class<?>> findAllPlugins() {
		List<Class<?>> ret = new ArrayList<>();
		
		for(File lib : libs) {
			if (lib.isDirectory()) {
				scanDirectory(lib, ret, "");
			} else {
				System.err.println("Can't scan jar files yet");
			}
		}
		
		return ret;
	}
	
	private void scanDirectory(File lib, List<Class<?>> results, String pkg) {
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
					// no-op
				}
			}
		}
	}

}

