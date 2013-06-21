package net.sf.javascribe.engine;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationHelper {

	public static List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> an,File[] locations) throws ClassNotFoundException,IOException {
		List<Class<?>> ret = new ArrayList<Class<?>>();

		for(File loc : locations) {
			if (loc.isDirectory()) {
				scanDirectory("",loc,an,ret);
			} else {
				scanJar(loc,an,ret);
			}
		}


		return ret;
	}

	private static void scanJar(File file,Class<? extends Annotation> an,List<Class<?>> ret) throws IOException,ClassNotFoundException {
		JarFile jar = null;

		try {
			jar = new JarFile(file);

			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String name = entry.getName().substring(0, entry.getName().length()-6);
					name = name.replace('/', '.');
					try {
						Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(name);
						if ((cl.isAnnotationPresent(an)) && (!ret.contains(cl))) {
							ret.add(cl);
						}
					} catch(NoClassDefFoundError e) { 
						// No-op if couldn't find the class.
					}
				}
			}
		} finally {
			if (jar!=null) {
				try { jar.close(); } catch(Exception e) { }
			}
		}
	}

	private static void scanDirectory(String pkg,File dir,Class<? extends Annotation> an,List<Class<?>> entries) throws ClassNotFoundException {
		File contents[] = dir.listFiles();
		for(File f : contents) {
			if (f.isDirectory()) {
				String newPkg = "";
				if (pkg.length()>0) {
					newPkg = pkg+'.'+f.getName();
				} else {
					newPkg = f.getName();
				}
				scanDirectory(newPkg,f,an,entries);
			}
			else {
				if (f.getName().endsWith(".class")) {
					String clName = pkg+'.'+f.getName().substring(0, f.getName().length()-6);
					Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(clName);
					if ((cl.isAnnotationPresent(an)) && (!entries.contains(cl))) {
						entries.add(cl);
					}
				}
			}
		}
	}

}
