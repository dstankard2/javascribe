package net.sf.javascribe.engine.manager;

import java.util.HashSet;
import java.util.Set;

import net.sf.javascribe.api.plugin.EnginePlugin;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineInitException;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.ProcessingData;
import net.sf.javascribe.engine.plugin.PluginContextImpl;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.PluginService;
import net.sf.javascribe.engine.util.LogUtil;

public class PluginManager {

	private Set<EnginePlugin> plugins = new HashSet<>();

	private PatternService patternService;

	private PluginService pluginService;

	private ComponentFileService componentFileService;

	private LanguageSupportService languageSupportService;

	private EngineProperties props;

	private EngineResources engineResources;

	private LogUtil logUtil;
	
	@ComponentDependency
	public void setEngineResources(EngineResources engineResources) {
		this.engineResources = engineResources;
	}

	@ComponentDependency
	public void setEngineProperties(EngineProperties props) {
		this.props = props;
	}

	@ComponentDependency
	public void setLanguageSupportService(LanguageSupportService srv) {
		this.languageSupportService = srv;
	}

	@ComponentDependency
	public void setPatternService(PatternService srv) {
		this.patternService = srv;
	}

	@ComponentDependency
	public void setComponentFileService(ComponentFileService srv) {
		this.componentFileService = srv;
	}

	@ComponentDependency
	public void setPluginService(PluginService srv) {
		this.pluginService = srv;
	}

	@ComponentDependency
	public void setLogUtil(LogUtil u) {
		this.logUtil = u;
	}

	public PluginManager() {
	}

	/**
	 * Initializes all resources related to classes annotated with @Plugin
	 */
	public void initializeAllPlugins(boolean runOnce) {
		componentFileService.loadPatternDefinitions();
		languageSupportService.loadLanguageSupport();
		patternService.initializePatterns();

		if (!runOnce) {
			Set<Class<EnginePlugin>> pluginClasses = pluginService.findClassesThatExtend(EnginePlugin.class);
			for (Class<EnginePlugin> cl : pluginClasses) {
				try {
					PluginContextImpl pluginContext = new PluginContextImpl(engineResources, props);
					EnginePlugin plugin = cl.getConstructor().newInstance();
					String configName = plugin.getPluginConfigName();
					if ((configName != null) && (configName.startsWith("engine.plugin."))) {
						if (props.getProperty(configName, null) != null) {
							// TODO: Log to engine that this plugin is starting
							plugin.setPluginContext(pluginContext);
							this.plugins.add(plugin);
						}
					}
				} catch (Exception e) {
					throw new EngineInitException("Couldn't initialize engine plugin " + cl.getName()
							+ " - Check that it has a default constructor", e);
				}
			}
		}
	}

	public void startPlugins() {
		// System.out.println("Start plugins");
		this.plugins.forEach(plugin -> {
			plugin.engineStart();
			logUtil.outputPendingLogMessages(engineResources, true);
		});
	}

	public void postScan(ApplicationData application) {
		ApplicationSnapshot snapshot = getApplicationSnapshot(application);
		this.plugins.forEach(plugin -> {
			plugin.scanFinish(snapshot);
			logUtil.outputPendingLogMessages(engineResources, true);
		});
	}

	private ApplicationSnapshot getApplicationSnapshot(ApplicationData application) {
		ApplicationSnapshot ret = ApplicationSnapshot.builder().name(application.getName()).build();
		ProcessingData pd = application.getProcessingData();

		//pd.
		return ret;
	}
}
