package net.sf.javascribe.engine.data.processing;

import java.util.Map;

import net.sf.javascribe.api.ApplicationContext;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.BuildProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.EngineResources;

public class BuildProcessorContextImpl implements BuildProcessorContext {
	private ApplicationFolderImpl folder;
	private Map<String,String> configs;
	private EngineResources engineResources;
	private ProcessorLog log;
	
	public BuildProcessorContextImpl(ApplicationFolderImpl folder, Map<String,String> configs,
			EngineResources engineResources, ProcessorLog log) {
		this.folder = folder;
		this.configs = configs;
		this.engineResources = engineResources;
		this.log = log;
	}

	@Override
	public String getProperty(String name) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Log getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addComponent(Component component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BuildContext getParentBuildContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
