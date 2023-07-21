package net.sf.javascribe.engine;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.logging.ProcessorLogLevel;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.processing.BuildComponentItem;
import net.sf.javascribe.engine.data.processing.ComponentItem;
import net.sf.javascribe.engine.data.processing.ProcessingState;
import net.sf.javascribe.engine.data.processing.ProcessorLog;
import net.sf.javascribe.engine.service.RegisteredBuildComponentPattern;
import net.sf.javascribe.engine.service.RegisteredComponentPattern;

// Builder for application data
public class ApplicationBuilder {
	ApplicationData app;
	
	public static ApplicationBuilder create() {
		return new ApplicationBuilder();
	}

	public ApplicationBuilder rootFolder(ApplicationFolderImpl folder) {
		app.setRootFolder(folder);
		return this;
	}

	public ApplicationBuilder createLog() {
		app.setApplicationLog(new ProcessorLog("APP", app, ProcessorLogLevel.DEBUG));
		return this;
	}

	public ApplicationData build() {
		return app;
	}

	private ApplicationBuilder() {
		app = new ApplicationData();
		app.setName("Test");
		app.setState(ProcessingState.CREATED);
		//app.setRootFolder(folder);
	}
	
	public ApplicationBuilder addComponentItem(Component comp, boolean toProcess) {
		RegisteredComponentPattern pattern = null;
		Map<String,String> configs = new HashMap<>();

		ComponentItem item = new ComponentItem(5, comp, configs, pattern, 0, app.getRootFolder(), app);
		if (toProcess) {
			app.getProcessingData().getToProcess().add(item);
		} else {
			app.getProcessingData().getProcessed().add(item);
		}
		app.getProcessingData().getAllItems().add(item);

		return this;
	}

	public ApplicationBuilder addBuildComponentItem(BuildComponent comp, ProcessingState state) {
		RegisteredBuildComponentPattern pattern = null;
		Map<String,String> configs = new HashMap<>();

		BuildComponentItem item = new BuildComponentItem(0, comp, app.getRootFolder(), pattern, configs, app);
		if (state==ProcessingState.CREATED) {
			app.getProcessingData().getBuildsToInit().add(item);
		}
		else if (state==ProcessingState.INITIALIZED) {
			app.getProcessingData().getBuildsToProcess().add(item);
		}
		else if (state==ProcessingState.SUCCESS) {
			app.getProcessingData().getBuildsProcessed().add(item);
		}
		else if (state==ProcessingState.ERROR) {
			app.getProcessingData().getBuildsToInit().add(item);
		}
		app.getProcessingData().getAllItems().add(item);

		return this;
	}

}

