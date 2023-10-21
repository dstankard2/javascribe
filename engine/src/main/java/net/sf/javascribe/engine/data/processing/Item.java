package net.sf.javascribe.engine.data.processing;

import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;

// Represents an entry tracked by the engine
// An item may be a processable or may be used to create processables (folder/file watcher)
public interface Item {

	int getItemId();
	int getOriginatorId();
	String getName();
	public void setState(ProcessingState state);
	public ProcessingState getState();
	public ApplicationFolderImpl getFolder();

}

