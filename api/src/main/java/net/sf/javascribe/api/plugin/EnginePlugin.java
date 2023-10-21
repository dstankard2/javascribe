package net.sf.javascribe.api.plugin;

import net.sf.javascribe.api.snapshot.ApplicationSnapshot;

public interface EnginePlugin {

	void setPluginContext(PluginContext ctx);
	
	String getPluginName();

	String getPluginConfigName();

	void engineStart();

	void scanFinish(ApplicationSnapshot applicationData);

}

