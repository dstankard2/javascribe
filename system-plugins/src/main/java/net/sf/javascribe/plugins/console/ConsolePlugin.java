package net.sf.javascribe.plugins.console;

import net.sf.javascribe.api.plugin.EnginePlugin;
import net.sf.javascribe.api.plugin.PluginContext;
import net.sf.javascribe.api.snapshot.ApplicationSnapshot;

// Starts an embedded Tomcat server which serves up a Javascribe console
public class ConsolePlugin implements EnginePlugin {
	PluginContext ctx = null;

	@Override
	public void setPluginContext(PluginContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return "Console";
	}

	@Override
	public String getPluginConfigName() {
		return "engine.plugin.console";
	}

	// Setting up embed tomcat (maybe update pattern?)
	// https://www.baeldung.com/tomcat-programmatic-setup
	@Override
	public void engineStart() {
		this.ctx.getLog().info("Not starting console plugin");
	}

	@Override
	public void scanFinish(ApplicationSnapshot applicationData) {
		// TODO Auto-generated method stub
		
	}

	
}
