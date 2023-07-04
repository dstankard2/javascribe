package net.sf.javascribe.engine.util;

import net.sf.javascribe.api.logging.ProcessorLogLevel;
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
		boolean engineDebug = engineResources.getEngineProperties().getDebug();
		
		application.getMessages().forEach(m -> {
			int target = engineDebug ? 0 : m.getTargetLevel().ordinal();
			if (m.getLevel().ordinal() >= target) {
				StringBuilder output = new StringBuilder();
				output.append('[').append(m.getLogName()).append("] ")
					.append(""+m.getLevel().name()+" ")
					.append(m.getMessage());
				if (m.getLevel()==ProcessorLogLevel.ERROR) {
					System.err.println(output.toString());
					System.err.flush();
				} else {
					System.out.println(output.toString());
					System.out.flush();
				}
				if ((m.getE()!=null) && (target==0)) {
					m.getE().printStackTrace();
				}
			}
		});
		
		if (consume) {
			application.getMessages().clear();
		}
	}

	public void outputMessageToLog(String message) {
		System.out.println(message);
	}

}

