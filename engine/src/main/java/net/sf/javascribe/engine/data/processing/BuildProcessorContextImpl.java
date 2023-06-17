package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.ApplicationContext;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.BuildProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.ProcessingContextOperations;

public class BuildProcessorContextImpl implements BuildProcessorContext {
	private ApplicationFolderImpl folder;
	private Map<String,String> configs;
	private ProcessorLog log;
	private ProcessingContextOperations ops;
	private ApplicationData application;
	private int itemId;
	
	public BuildProcessorContextImpl(int itemId, ApplicationFolderImpl folder, Map<String,String> configs,
			ProcessorLog log, ApplicationData application) {
		this.folder = folder;
		this.configs = configs;
		this.log = log;
		this.application = application;
		this.itemId = itemId;
		this.ops = ComponentContainer.get().getComponent(ProcessingContextOperations.class);
	}

	@Override
	public String getProperty(String name) {
		// TODO Auto-generated method stub
		return configs.get(name);
	}

	@Override
	public void addSourceFile(SourceFile file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SourceFile getSourceFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObject(String name, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getObject(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationFolder getFolder() {
		return folder;
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public void addComponent(Component component) {
		ops.addComponent(itemId, component, configs, folder, application);
	}

	@Override
	public BuildContext getParentBuildContext() {
		if (folder.getParent()!=null) {
			return folder.getParent().getBuildContext();
		}
		return null;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFolderWatcher(String path, FolderWatcher folderWatcher) {
		ops.addFolderWatcher(itemId, path, folderWatcher, configs, folder, application);
	}

}
