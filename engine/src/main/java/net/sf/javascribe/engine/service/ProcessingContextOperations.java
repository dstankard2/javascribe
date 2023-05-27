package net.sf.javascribe.engine.service;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.data.files.UserFile;

public interface ProcessingContextOperations {

	void addFolderWatcher(int originatorId, String path, FolderWatcher watcher);
	void addFileProcessor(int originatorId, UserFile userFile, FileProcessor processor);
	void addComponent(int originatorId, Component component);
	void addSourceFile(SourceFile sourceFile);

}
