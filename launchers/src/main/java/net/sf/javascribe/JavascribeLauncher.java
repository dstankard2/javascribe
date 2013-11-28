package net.sf.javascribe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class JavascribeLauncher {
	File[] libFiles = null;
	String javascribeHome = null;
	
	public JavascribeLauncher(String javascribeHome) {
		File home = new File(javascribeHome);
		
		if ((!home.exists()) || (!home.isDirectory())) {
			throw new RuntimeException("Invalid Javascribe home specified.");
		}
		this.javascribeHome = javascribeHome;
		File libDir = new File(home,"lib");
		if ((!libDir.exists()) || (!libDir.isDirectory())) {
			throw new RuntimeException("Invalid Javascribe Home - Could not find lib directory.");
		}
		File[] contents = libDir.listFiles();
		
		ArrayList<File> l = new ArrayList<File>();
		for(File f : contents) {
			if ((!f.isDirectory()) && (f.getName().endsWith(".jar"))) {
				l.add(f);
			}
		}
		libFiles = l.toArray(new File[l.size()]);
	}
	
	public void invokeJavascribe(File zipFile) throws MalformedURLException,ClassNotFoundException,
			InvocationTargetException,IllegalAccessException,NoSuchMethodException,
			InstantiationException {
		URL[] libs = new URL[libFiles.length+1];
		URLClassLoader loader = null;
		
		for(int i=0;i<libFiles.length;i++) {
			libs[i] = libFiles[i].toURI().toURL();
		}
		libs[libFiles.length] = new URL("file:conf");

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			loader = new URLClassLoader(libs,JavascribeLauncher.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(loader);

			Class<?> cl = loader.loadClass("net.sf.javascribe.engine.JavascribeEngine");
			Constructor<?> cons = cl.getConstructor(File[].class,String.class);
			Object obj = cons.newInstance((Object)libFiles,javascribeHome);
			Method invoke = cl.getMethod("invoke", File.class);
			invoke.invoke(obj, (Object)zipFile);
			
		} finally {
			if (original!=null) {
				Thread.currentThread().setContextClassLoader(original);
			}
			if (loader!=null) {
				try { loader.close(); } catch(Exception e) { }
			}
		}
	}
	
}

