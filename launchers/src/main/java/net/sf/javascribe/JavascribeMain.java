package net.sf.javascribe;

import java.io.File;

public class JavascribeMain {

	/**
	 * The JAVASCRIBE_HOME environment variable must be set.  The arguments should 
	 * contain the ZIP file to run Javascribe for.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			runProgram(args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void runProgram(String args[]) throws Exception {
		String javascribeHome = "C:\\dev_tools\\javascribe-0.1.3";
		File zipFile = new File("C:\\build\\appdesigner\\appdesigner.zip");
		new JavascribeLauncher(javascribeHome).invokeJavascribe(zipFile);
	}
	
}
