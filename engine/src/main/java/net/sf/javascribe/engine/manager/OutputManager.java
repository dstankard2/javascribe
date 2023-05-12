package net.sf.javascribe.engine.manager;

public class OutputManager {
	private String outputDir = null;
	private boolean singleApp = false;
	
	public void setOutputDir(String dir) {
		outputDir = dir;
	}
	
	public void setSingleApp(boolean singleApp) {
		this.singleApp = singleApp;
	}
	
}
