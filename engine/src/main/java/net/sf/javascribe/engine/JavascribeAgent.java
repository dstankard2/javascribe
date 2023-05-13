package net.sf.javascribe.engine;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.manager.OutputManager;
import net.sf.javascribe.engine.manager.PluginManager;
import net.sf.javascribe.engine.manager.ProcessingManager;
import net.sf.javascribe.engine.manager.WorkspaceManager;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.FolderScannerService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.PluginService;

/**
 * Acts as a component container for the Javascribe engine.  Initializes service objects and other 
 * dependencies.
 * @author dstan
 *
 */
public class JavascribeAgent {

	private EngineProperties properties;
	private File[] libs = null;
	
	public JavascribeAgent(File[] libs, Map<String,String> engineProperties) {
		this.properties = new EngineProperties(engineProperties);
		this.libs = libs;
	}
	
	public void init() {
		boolean debug = properties.getDebug();

		// Register services with engine container
		ComponentContainer.get().registerComponent(new FolderScannerService());
		ComponentContainer.get().registerComponent(new ComponentFileService());
		ComponentContainer.get().registerComponent(new PluginService());
		ComponentContainer.get().registerComponent(new LanguageSupportService());
		ComponentContainer.get().registerComponent(new PatternService());

		// Register manager classes with engine container
		ComponentContainer.get().registerComponent(new PluginManager());
		ComponentContainer.get().registerComponent(new FolderScannerService());
		ComponentContainer.get().registerComponent(new OutputManager());
		ComponentContainer.get().registerComponent(new WorkspaceManager());
		ComponentContainer.get().registerComponent(new ProcessingManager());

		// Register application data with engine container
		ComponentContainer.get().setComponent("debug", debug);
		ComponentContainer.get().setComponent("jarFiles", libs);

		ProcessingManager mgr = ComponentContainer.get().getComponent("ProcessingManager", ProcessingManager.class);
		WorkspaceManager workspaceManager = ComponentContainer.get().getComponent("WorkspaceManager", WorkspaceManager.class);
		OutputManager outputManager = ComponentContainer.get().getComponent("OutputManager", OutputManager.class);
		PluginManager pluginManager = ComponentContainer.get().getComponent("PluginManager", PluginManager.class);
		
		String appDir = properties.getApplicationDir();
		String outputDir = properties.getOutputDir();
		boolean singleApp = properties.getSingleApp();
		boolean runOnce = properties.getRunOnce();
		
		pluginManager.initializeAllPlugins(runOnce);
		List<ApplicationData> applications = workspaceManager.initializeApplications(appDir, singleApp);
		
	}

	public void run() {
		boolean runOnce = properties.getRunOnce();
		PluginManager pluginManager = ComponentContainer.get().getComponent("PluginManager", PluginManager.class);
		boolean firstRun = true;
		
		// If we're running in agent mode, initialize plugins
		if (!runOnce) {
			pluginManager.initEnginePlugins();
		}
	}

}

