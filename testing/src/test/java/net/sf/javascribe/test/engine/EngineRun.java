package net.sf.javascribe.test.engine;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import net.sf.javascribe.engine.JavascribeAgent;

public class EngineRun {

	private File[] libs = new File[] { 
			new File("C:\\git\\javascribe\\api\\target\\classes"),
			new File("C:\\git\\javascribe\\java-support\\target\\classes"),
			new File("C:\\git\\javascribe\\javascript-support\\target\\classes"),
			new File("C:\\git\\javascribe\\patterns\\target\\classes")
	};

	@Test
	public void runEngine() {
		JavascribeAgent agent = null;
		
		HashMap<String,String> props = new HashMap<>();
		props.put("debug", "true");
		props.put("applicationDir", "C:\\workspaces\\appDefs\\NewsList");
		props.put("singleAppMode", "true");
		props.put("outputDir", "c:\\build\\NewsList");
		props.put("once", "false");

		try {
			agent = new JavascribeAgent(libs, props);
			agent.init();
			agent.run();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
