package net.sf.javascribe.api.plugin;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;

public interface ApplicationPlugin {

	String getPluginName();
	
	void setApplicationName(String applicationName);

	void setPluginContext(PluginContext ctx);

	void scanStart() throws JavascribeException;
	
	void scanComplete(ApplicationSnapshot snapshot) throws JavascribeException;

}

