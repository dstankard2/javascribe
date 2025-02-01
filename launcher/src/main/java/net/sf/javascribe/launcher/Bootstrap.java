package net.sf.javascribe.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Bootstrap {

	private String home = null;
	private File homeDir = null;
	private Map<String,String> explicitProperties = new HashMap<>();
	private Map<String,String> engineProperties = new HashMap<>();
	File[] libs = null;
	
	public Bootstrap() {
		
	}
	
	public void setHome(String home) {
		this.home = home;
	}

	public boolean addParameter(String param) {
		int index = param.indexOf("=");
		if (index>=0) {
			String name = param.substring(0, index);
			String value = param.substring(index+1);
			boolean ret = handleParameter(name, value);
			if (!ret) {
				printUsage();
			}
			return ret;
		} else if (param.equals("-h")) {
			printUsage();
			return false;
		} 
		printUsage();
		return false;
	}
	
	public void start() {
		if (home==null) {
			System.err.println("Javascribe requires env variable JAVASCRIBE_HOME");
			return;
		}
		if (!findHome()) {
			return;
		}
		readEngineProperties();
		
		// Read explicit properties into engine properties
		explicitProperties.entrySet().forEach(e -> {
			engineProperties.put(e.getKey(), e.getValue());
		});
		
		// Find Javascribe classpath in lib directory
		findLibs();

		/*
		engineProperties.entrySet().forEach(e -> {
			System.out.println("Got engine property '"+e.getKey()+"' as '"+e.getValue()+"'");
		});
		*/
		
		startEngine();
	}
	
	private void startEngine() {
		URL[] urls = new URL[libs.length];
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			for(int i=0;i<libs.length;i++) {
				urls[i] = libs[i].toURL();
				// System.err.println("Using lib "+libs[i].getAbsolutePath());
			}
			
			URLClassLoader loader = new URLClassLoader(urls);
			Thread.currentThread().setContextClassLoader(loader);
			Class<?> cl = loader.loadClass("net.sf.javascribe.engine.JavascribeAgent");
			Constructor<?> con = cl.getConstructors()[0];
			Object instance = con.newInstance(libs, engineProperties);
			Object initResult = cl.getMethod("init").invoke(instance);
			if (Boolean.TRUE.equals(initResult)) {
				cl.getMethod("run").invoke(instance);
			}
		} catch(Throwable e) {
			e.printStackTrace();
			/*
		} catch(MalformedURLException e) {
			
		} catch(ClassNotFoundException e) {
			
		} catch(InvocationTargetException e) {
			
		} catch(IllegalAccessException e) {
			
		} catch(InstantiationException e) {
			
		} catch(NoSuchMethodException e) {
			*/
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	private void findLibs() {
		List<File> libList = new ArrayList<>();
		File libDir = new File(homeDir, "lib");
		
		if ((!libDir.exists()) || (!libDir.isDirectory())) {
			System.err.println("Couldn't find Javascribe lib directory");
			return;
		}
		Arrays.asList(libDir.listFiles()).forEach(f -> {
			if ((f.getName().endsWith(".jar")) && (!f.isDirectory())) {
				libList.add(f);
			}
		});
		// System.out.println("Scanned and found libs as "+libList);
		libs = new File[libList.size()];
		libs = libList.toArray(libs);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readEngineProperties() {
		File propFile = new File(homeDir, "engine.properties");
		
		if ((!propFile.exists()) || (propFile.isDirectory())) {
			System.err.println("WARN: Couldn't find file engine.properties");
		} else {
			Properties prop = new Properties();
			try (InputStream in = new FileInputStream(propFile)) {
				prop.load(in);
				engineProperties = (Map)prop;
			} catch(FileNotFoundException e) {
				System.err.println("WARN: Couldn't find file engine.properties");
			} catch(IOException e) {
				System.err.println("WARN: Couldn't find file engine.properties");
			}
		}
	}

	private boolean findHome() {
		homeDir = new File(home);
		
		if ((!homeDir.exists()) || (!homeDir.isDirectory())) {
			System.out.println("Couldn't find home directory "+homeDir);
			return false;
		}
		return true;
	}

	private boolean handleParameter(String name, String value) {
// 		System.out.println("Handling parameter '"+name+"' = '"+value+"'");
		if (name.equals("-workspaceDir")) {
			explicitProperties.put("workspaceDir", value);
			System.out.println("setting workspace dir as "+value);
			// workspace = value;
			return true;
		} else if (name.equals("-outputDir")) {
			explicitProperties.put("outputDir", value);
			System.out.println("setting output dir as "+value);
			// out = value;
			return true;
		}
		return false;
	}

	
	
	public void printUsage() {
		String helpText = """
Javascribe Usage: javascribe(.bat) <param> <param>
* Valid parameters:
 - "-h" - Print this message
 - "-workspaceDir=<workspaceDir>" - Set workspace directory
 - "-outputDir=<outputDirectory>" - Set Javascribe output directory
""";
		System.err.println(helpText);
	}

}

