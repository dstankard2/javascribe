package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.engine.EngineException;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.DefaultBuildContext;
import net.sf.javascribe.engine.service.RegisteredBuildComponentPattern;

public class BuildComponentItem implements Item {

	private int itemId;
	protected BuildComponent buildComp = null;
	private ApplicationFolderImpl folder;
	protected RegisteredBuildComponentPattern pattern = null;
	private BuildProcessorContextImpl buildProcessorCtx = null;
	private ProcessingState state;
	private BuildContext buildContext;
	private ProcessorLog log;
	private Map<String,String> configs;
	private ApplicationData application;
	@SuppressWarnings("rawtypes")
	private BuildComponentProcessor processor = null;

	public BuildComponentItem(int itemId, BuildComponent buildComp, ApplicationFolderImpl folder, 
			RegisteredBuildComponentPattern pattern, 
			Map<String,String> configs, 
			ApplicationData application) {
		this.itemId = itemId;
		this.buildComp = buildComp;
		this.folder = folder;
		this.pattern = pattern;
		this.log = new ProcessorLog(buildComp.getComponentName(), application, folder.getLogLevel());
		this.configs = configs;
		this.application = application;
	}
	
	public String getName() {
		return buildComp.getComponentName();
	}
	
	public void setState(ProcessingState state) {
		this.state = state;
	}

	public ProcessingState getState() {
		return this.state;
	}
	
	public BuildComponent getBuildComponent() {
		return buildComp;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean init() {
		boolean success = true;
		this.buildProcessorCtx = new BuildProcessorContextImpl(
			itemId, folder, configs, log, application
		);
		
		if (pattern==null) {
			this.buildContext = new DefaultBuildContext(buildProcessorCtx);
		} else {
			Class<BuildComponentProcessor> cl = pattern.getProcessorClass();
			try {
				processor = cl.getConstructor().newInstance();
			} catch(Exception e) {
				throw new EngineException("Couldn't create build component processor", e);
			}
			try {
				processor.initialize(buildComp, buildProcessorCtx);
			} catch(JavascribeException e) {
				success = false;
				this.log.error("Exception in build initialization", e);
			}
			this.buildContext = processor.createBuildContext();
		}

		return success;
	}

	public void process() {
		
	}

	public BuildContext getBuildContext() {
		return buildContext;
	}

	@Override
	public int getItemId() {
		return itemId;
	}

	// A build never originates from another item
	@Override
	public int getOriginatorId() {
		return 0;
	}

	
}

