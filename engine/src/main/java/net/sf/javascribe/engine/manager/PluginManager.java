package net.sf.javascribe.engine.manager;

import java.util.List;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.service.ComponentFileService;
import net.sf.javascribe.engine.service.LanguageSupportService;
import net.sf.javascribe.engine.service.PatternService;
import net.sf.javascribe.engine.service.PluginService;

public class PluginManager {

	private PatternService patternService;
	
	private PluginService pluginService;
	
	private ComponentFileService componentFileService;
	
	private LanguageSupportService languageSupportService;
	
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

	public PluginManager() {
	}

	/**
	 * Initializes all resources related to classes annotated with @Plugin
	 */
	public void initializeAllPlugins(boolean runOnce) {
		componentFileService.loadPatternDefinitions();
		languageSupportService.loadLanguageSupport();
		patternService.initializePatterns();
	}
	
	public void initEnginePlugins() {
		
	}
	
}

