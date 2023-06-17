package net.sf.javascribe.engine;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.manager.OutputManager;
import net.sf.javascribe.engine.manager.PluginManager;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.OutputService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.PluginService;
import net.sf.javascribe.engine.service.ProcessingService;
import net.sf.javascribe.engine.util.DependencyUtil;
import net.sf.javascribe.engine.util.FileUtil;
import net.sf.javascribe.engine.util.LogUtil;
import net.sf.javascribe.engine.util.OutputUtil;
import net.sf.javascribe.engine.util.ProcessingUtil;

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
		ProcessingService processingService = new ProcessingService();

		// Register utility classes with engine container
		ComponentContainer.get().registerComponent(new FileUtil());
		ComponentContainer.get().registerComponent(new ProcessingUtil());
		ComponentContainer.get().registerComponent(new DependencyUtil());
		ComponentContainer.get().registerComponent(new OutputUtil());
		ComponentContainer.get().registerComponent(new LogUtil());
		
		// Register services with engine container
		ComponentContainer.get().registerComponent(new FolderScannerService());
		ComponentContainer.get().registerComponent(new ComponentFileService());
		ComponentContainer.get().registerComponent(new PluginService());
		ComponentContainer.get().registerComponent(new LanguageSupportService());
		ComponentContainer.get().registerComponent(new PatternService());
		ComponentContainer.get().registerComponent(processingService);
		ComponentContainer.get().registerComponent(new OutputService());
		ComponentContainer.get().registerComponent(new FolderScannerService());

		// Register manager classes with engine container
		ComponentContainer.get().registerComponent(new PluginManager());
		ComponentContainer.get().registerComponent(new WorkspaceManager());
		ComponentContainer.get().registerComponent(new OutputManager());

		// Register engine data and resources with engine container
		ComponentContainer.get().setComponent("debug", debug);
		ComponentContainer.get().setComponent("jarFiles", libs);
		ComponentContainer.get().setComponent("EngineProperties", properties);
		ComponentContainer.get().registerComponent(new EngineResources());
		ComponentContainer.get().setComponent("ProcessingContextOperations", processingService);

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
			scanApplications();
			if (runOnce) {
				done = true;
			} else {
				try {
					Thread.sleep(1000);
				} catch(Exception e) { }
			}
		}
	}

	private void scanApplications() {
		WorkspaceManager workspaceManager = ComponentContainer.get().getComponent("WorkspaceManager", WorkspaceManager.class);
		for(ApplicationData app : this.applications) {
			workspaceManager.scanApplicationDir(app);
		}
	}

}

