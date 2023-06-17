package net.sf.javascribe.engine.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.javascribe.api.langsupport.LanguageSupport;
import net.sf.javascribe.engine.ComponentDependency;

public class LanguageSupportService {
	
	private Map<String,LanguageSupport> languages = new HashMap<>();
	
	private EngineResources engineResources;
	@ComponentDependency
	public void setEngineResources(EngineResources r) {
		this.engineResources = r;
	}
	
	public void loadLanguageSupport() {
		Set<Class<LanguageSupport>> langs = engineResources.getPlugins(LanguageSupport.class);
		
		langs.forEach(lang -> {
			try {
				LanguageSupport supp = lang.getConstructor().newInstance();
				String name = supp.getLanguageName();
				languages.put(name, supp);
			} catch(Exception e) { }
		});
	}

}

