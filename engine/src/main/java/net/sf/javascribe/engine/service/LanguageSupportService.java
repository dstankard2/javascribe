package net.sf.javascribe.engine.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.langsupport.LanguageSupport;

public class LanguageSupportService {
	private File[] libs = null;
	
	private Map<String,LanguageSupport> languages = new HashMap<>();
	
	public void setLibs(File[] libs) {
		this.libs = libs;
	}
	
	public void loadLanguageSupport() {
		
	}

}

