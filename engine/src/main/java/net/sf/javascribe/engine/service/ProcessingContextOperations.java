package net.sf.javascribe.engine.service;

import java.util.Map;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.StaleDependencyException;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;

public interface ProcessingContextOperations {

	void addFolderWatcher(int originatorId, String path, FolderWatcher watcher, Map<String,String> configs, ApplicationFolderImpl folder, ApplicationData application);
	void addFileProcessor(int originatorId, UserFile userFile, FileProcessor processor, Map<String,String> configs, ApplicationFolderImpl folder, ApplicationData application);
	void addComponent(int originatorId, Component component, Map<String,String> configs, ApplicationFolderImpl folder, ApplicationData application);
	void addSourceFile(SourceFile sourceFile, ApplicationData application);
	void checkVariableTypeStale(String lang, String name, ApplicationData application) throws StaleDependencyException;
	
}
