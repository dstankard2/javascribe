package test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class JavascribeTest {

	public static void main(String args[]) {
		try {
			runTest();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void runTest() throws Exception {
		File zipFile = new File("c:\\build\\example1\\example1.zip");
//		File zipFile = new File("c:\\build\\codewiz\\codewiz.zip");

		File libs[] = new File[12];
		libs[0] = new File("..\\engine\\bin");
		libs[1] = new File("..\\api\\bin");
		libs[2] = new File("..\\language-support\\bin");
		libs[3] = new File("..\\patterns\\bin");
		libs[4] = new File("..\\dist\\lib\\mysql-connector-java-5.0.4-bin.jar");
		libs[5] = new File("..\\dist\\lib\\jsom.jar");
		libs[6] = new File("..\\dist\\lib\\other-xsd.jar");
		libs[7] = new File("..\\dist\\lib\\log4j-1.2.17.jar");
		libs[8] = new File("..\\dist\\lib\\javaparser-1.0.8.jar");
		libs[10] = new File("..\\dist\\lib\\jsoup-1.8.1.jar");
		libs[11] = new File("..\\dist\\lib\\jackson-all-1.9.6.jar");

		URL libUrls[] = new URL[libs.length];
		for(int i=0;i<libs.length;i++) {
			libUrls[i] = libs[i].toURI().toURL();
		}

		URLClassLoader loader = null;
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			loader = new URLClassLoader(libUrls,JavascribeTest.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(loader);
			Class<?> cl = loader.loadClass("net.sf.javascribe.engine.JavascribeEngine");
			Constructor<?> cons = cl.getConstructor(File[].class,String.class);
			Object obj = cons.newInstance((Object)libs,"c:\\dev_tools\\javascribe-0.1.8");
			Method invoke = cl.getMethod("invoke", File.class);
			invoke.invoke(obj, (Object)zipFile);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
			if (loader!=null) {
				try { loader.close(); } catch(Exception e) { }
			}
		}
	}

}
