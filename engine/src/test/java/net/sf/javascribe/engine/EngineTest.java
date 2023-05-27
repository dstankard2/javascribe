package net.sf.javascribe.engine;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

public class EngineTest {

	@Test
	public void testEngine() {
		JavascribeAgent agent = null;
		
		File[] libs = new File[] { 
				new File("C:\\git\\javascribe\\api\\target\\classes"),
				new File("C:\\git\\javascribe\\java-support\\target\\classes"),
				new File("C:\\git\\javascribe\\engine\\target\\test-classes")
		};
		
		HashMap<String,String> props = new HashMap<>();
		/*
		props.put("debug", "true");
		props.put("applicationDir", "C:\\git\\javascribe\\engine\\src\\test\\resources\\test1");
		props.put("singleAppMode", "true");
		props.put("outputDir", "c:\\build\\jstest\\test1");
		//props.put("once", "false");
		*/

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
