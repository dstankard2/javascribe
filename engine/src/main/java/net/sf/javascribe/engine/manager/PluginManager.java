package net.sf.javascribe.engine.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.plugin.EnginePlugin;
import net.sf.javascribe.api.plugin.ProcessorLogMessage;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;
import net.sf.javascribe.api.snapshot.ItemSnapshot;
import net.sf.javascribe.api.snapshot.LogMessageSnapshot;
import net.sf.javascribe.api.snapshot.SourceFileSnapshot;
import net.sf.javascribe.api.snapshot.SystemAttributeSnapshot;
import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.EngineInitException;
import net.sf.javascribe.engine.EngineProperties;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
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
		ApplicationSnapshot ret = ApplicationSnapshot.builder().name(application.getName()).status(application.getState().name()).build();
		ProcessingData pd = application.getProcessingData();
		DependencyData dep = application.getDependencyData();

		Map<String,SourceFile> files = application.getSourceFiles();

		pd.getAllItems().forEach(item -> {
			ItemSnapshot is = ItemSnapshot.builder().id(item.getItemId())
					.originatorId(item.getOriginatorId()).state(item.getState().name()).name(item.getName()).build();
			ret.getAllItems().add(is);
			List<ProcessorLogMessage> msgs = application.getMessages().stream().filter(msg -> msg.getLogName().equals(item.getName())).collect(Collectors.toList());
			msgs.forEach(msg -> {
				is.getLogs().add(LogMessageSnapshot.builder().level(msg.getLevel().name()).message(msg.getMessage()).build());
			});
		});

		files.entrySet().forEach(e -> {
			String path = e.getKey();
			SourceFileSnapshot s = SourceFileSnapshot.builder().path(path).build();
			s.getOriginators().addAll(dep.getSrcDependencies().get(path));
			ret.getSourceFiles().put(path, s);
		});
		
		application.getSystemAttributes().entrySet().forEach(entry -> {
			String name = entry.getKey();
			String type = entry.getValue();
			SystemAttributeSnapshot s = SystemAttributeSnapshot.builder().name(name).type(type).build();
			Set<Integer> ids = dep.getAttributeDependencies().get(name);
			if (ids!=null) {
				s.getDependants().addAll(ids);
			}
			ids = dep.getAttributeOriginators().get(name);
			s.getOriginators().addAll(ids);
			ret.getAllSystemAttributes().put(name, s);
		});

		return ret;
	}

}

