package net.sf.javascribe.api.plugin;

import net.sf.javascribe.api.exception.JasperException;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;

public interface ApplicationPlugin {

	String getPluginName();
	
	void setApplicationName(String applicationName);

	void setPluginContext(PluginContext ctx);

	void scanStart() throws JasperException;
	
	void scanComplete(ApplicationSnapshot snapshot) throws JasperException;

}

