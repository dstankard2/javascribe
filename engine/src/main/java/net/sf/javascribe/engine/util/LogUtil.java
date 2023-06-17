package net.sf.javascribe.engine.util;

import net.sf.javascribe.engine.ComponentDependency;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.service.EngineResources;

public class LogUtil {

	private EngineResources engineResources;
	@ComponentDependency
	public void setEngineResources(EngineResources engineResources) {
		this.engineResources = engineResources;
	}

	public void outputPendingLogMessages(ApplicationData application, boolean consume) {
		boolean debug = engineResources.getEngineProperties().getDebug();
		
		application.getMessages().forEach(m -> {
			StringBuilder output = new StringBuilder();
			output.append('[').append(m.getLogName()).append("] ")
			.append(""+m.getLevel().name()+" ")
			.append(m.getMessage());

			System.out.println(output.toString());
			if (m.getE()!=null) {
				m.getE().printStackTrace();
			}
		});
		
		if (consume) {
			application.getMessages().clear();
		}
	}

}
