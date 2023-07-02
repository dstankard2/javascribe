package net.sf.javascribe.test.engine;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.Test;

import net.sf.javascribe.engine.JavascribeAgent;

/**
 * This test is for running the full engine from a junit shell.
 * Usually used for debugging purposes.  The tests are disabled by default.
 * @author dstan
 */
public class EngineTest {

	private File[] libs = new File[] { 
			new File("C:\\git\\javascribe\\api\\target\\classes"),
			new File("C:\\git\\javascribe\\java-support\\target\\classes"),
			new File("C:\\git\\javascribe\\test-patterns\\target\\classes")
			//new File("C:\\git\\javascribe\\patterns\\target\\test-classes")
	};
	
	//@Test
	public void testEngine() {
		JavascribeAgent agent = null;
		
		HashMap<String,String> props = new HashMap<>();
		/*
		props.put("debug", "true");
		props.put("applicationDir", "C:\\git\\javascribe\\engine\\src\\test\\resources\\test1");
		props.put("singleAppMode", "true");
		props.put("outputDir", "c:\\build\\jstest\\test1");
		//props.put("once", "false");
		*/

		props.put("debug", "true");
		props.put("applicationDir", "C:\\git\\javascribe\\test-patterns\\src\\test\\resources\\test1");
		props.put("singleAppMode", "true");
		props.put("outputDir", "c:\\build\\jstest\\test1");
		props.put("once", "false");

		try {
		agent = new JavascribeAgent(libs, props);
		agent.init();
		agent.run();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void testAgentMode() {
		JavascribeAgent agent = null;
		
		File[] libs = new File[] { 
				new File("C:\\git\\javascribe\\api\\target\\classes"),
				new File("C:\\git\\javascribe\\java-support\\target\\classes"),
				new File("C:\\git\\javascribe\\engine\\target\\test-classes"),
				new File("C:\\git\\javascribe\\patterns\\target\\test-classes")
		};
		
		HashMap<String,String> props = new HashMap<>();

		props.put("debug", "true");
		props.put("applicationDir", "C:\\git\\javascribe\\engine\\src\\test\\resources\\test1");
		props.put("singleAppMode", "true");
		props.put("outputDir", "c:\\build\\jstest\\test1");
		props.put("once", "true");

		agent = new JavascribeAgent(libs, props);
		agent.init();
		agent.run();
	}

}
