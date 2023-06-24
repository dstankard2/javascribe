package net.sf.javascribe.engine;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.manager.OutputManager;
import net.sf.javascribe.engine.manager.PluginManager;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.EngineResources;

/**
 * Acts as a component container for the Javascribe engine.  Initializes service objects and other 
 * dependencies.
 * @author dstan
 *
 */
public class JavascribeAgent {

	private EngineProperties properties;
	private File[] libs = null;
	private List<ApplicationData> applications;

	public JavascribeAgent(File[] libs, Map<String,String> engineProperties) {
		this.properties = new EngineProperties(engineProperties);
		this.libs = libs;
	}
	
	public void init() {
		boolean debug = properties.getDebug();
		
		ComponentContainer.get().registerServices();

		// Register engine data and resources with engine container
		ComponentContainer.get().setComponent("debug", debug);
		ComponentContainer.get().setComponent("jarFiles", libs);
		ComponentContainer.get().setComponent("EngineProperties", properties);
		ComponentContainer.get().registerComponent(new EngineResources());
		//ComponentContainer.get().setComponent("ProcessingContextOperations", processingService);

		WorkspaceManager workspaceManager = ComponentContainer.get().getComponent("WorkspaceManager", WorkspaceManager.class);
		PluginManager pluginManager = ComponentContainer.get().getComponent("PluginManager", PluginManager.class);
		OutputManager outputManager = ComponentContainer.get().getComponent("OutputManager", OutputManager.class);
		
		String appDir = properties.getApplicationDir();
		String outputDir = properties.getOutputDir();
		boolean singleApp = properties.getSingleApp();
		boolean runOnce = properties.getRunOnce();
		
		pluginManager.initializeAllPlugins(runOnce);
		applications = workspaceManager.initializeApplications(appDir, singleApp);
		
		// Initialize output folder(s)
		outputManager.initOutputDirectory(outputDir, applications, singleApp);

	}

	public void run() {
		boolean runOnce = properties.getRunOnce();
		PluginManager pluginManager = ComponentContainer.get().getComponent("PluginManager", PluginManager.class);
		boolean firstRun = true;
		boolean done = false;
		
		// If we're running in agent mode, initialize plugins
		if (!runOnce) {
			pluginManager.startPlugins();
		}
		
		while(!done) {
			scanApplications(firstRun, runOnce);
			firstRun = false;
			if (runOnce) {
				done = true;
			} else {
				try {
					Thread.sleep(1000);
				} catch(Exception e) { }
			}
		}
	}

	private void scanApplications(boolean firstRun, boolean onlyRun) {
		WorkspaceManager workspaceManager = ComponentContainer.get().getComponent("WorkspaceManager", WorkspaceManager.class);
		for(ApplicationData application : this.applications) {
			if (firstRun) {
				application.getApplicationLog().info("*** Scanned application '"+application.getName()+"' and found changes ***");
			}
			workspaceManager.scanApplicationDir(application, firstRun, onlyRun);
		}
	}

}

