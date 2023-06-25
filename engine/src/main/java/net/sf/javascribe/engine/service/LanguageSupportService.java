package net.sf.javascribe.engine.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.javascribe.api.langsupport.LanguageSupport;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.engine.ComponentDependency;

public class LanguageSupportService {
	
	private Map<String,LanguageSupport> languages = new HashMap<>();

	private PluginService pluginService;
	@ComponentDependency
	public void setPluginService(PluginService s) {
		this.pluginService = s;
	}

	public void loadLanguageSupport() {
		Set<Class<LanguageSupport>> langs = pluginService.findClassesThatExtend(LanguageSupport.class);
		
		langs.forEach(lang -> {
			try {
				LanguageSupport supp = lang.getConstructor().newInstance();
				String name = supp.getLanguageName();
				languages.put(name, supp);
			} catch(Exception e) { }
		});
	}
	
	public Map<String,VariableType> getBaseTypes(String lang) {
		LanguageSupport supp = languages.get(lang);
		if (supp!=null) {
			Map<String,VariableType> ret = new HashMap<>();
			supp.getBaseVariableTypes().forEach(type -> {
				ret.put(type.getName(), type);
			});
			return ret;
		}
		return null;
	}

}

