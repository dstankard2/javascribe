package net.sf.javascribe.test.plugin;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.plugin.EnginePlugin;
import net.sf.javascribe.api.plugin.PluginContext;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;

@Plugin
public class TestEnginePlugin implements EnginePlugin {

	@Override
	public void setPluginContext(PluginContext ctx) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void engineStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scanFinish(ApplicationSnapshot applicationSnapshot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPluginConfigName() {
		return null;
	}

}
