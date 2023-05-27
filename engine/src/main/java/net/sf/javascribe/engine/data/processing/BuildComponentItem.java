package net.sf.javascribe.engine.data.processing;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.config.BuildComponent;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;

public class BuildComponentItem implements Item {

	private int itemId;
	protected BuildComponent buildComp = null;
	private ApplicationFolderImpl folder;
	//protected BuildComponentPattern pattern = null;
	private BuildProcessorContextImpl buildProcessorCtx = null;
	private ProcessingState state;
	
	public BuildComponentItem(int itemId, BuildComponent buildComp, ApplicationFolderImpl folder) {
		this.itemId = itemId;
		this.buildComp = buildComp;
		this.folder = folder;
	}
	
	public String getName() {
		return null;
	}
	
	public void setState(ProcessingState state) {
		this.state = state;
	}

	public ProcessingState getState() {
		return this.state;
	}

	public void init() {
		
	}

	public void process() {
		
	}

	public BuildContext getBuildContext() {
		return null;
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

