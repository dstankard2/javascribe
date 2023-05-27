package net.sf.javascribe.engine.data.processing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.ApplicationContext;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.ProcessingContextOperations;

public class ProcessorContextImpl implements ProcessorContext {
	private ApplicationData application;
	private String name;
	private ProcessorLog log;
	private String lang = null;
	private int id;
	private Map<String,String> configs;
	private BuildContext buildCtx;
	private ApplicationFolderImpl folder;
	private EngineResources engineResources;
	private ProcessingContextOperations ops;
	private DependencyData dependencyData;
	
	public ProcessorContextImpl(String name, ApplicationData application, int id,
			Map<String,String> configs, BuildContext buildCtx, 
			ApplicationFolderImpl folder, EngineResources engineResources,
			ProcessingContextOperations ops, ProcessorLog log) {
		this.name = name;
		this.application = application;
		this.id = id;
		this.configs = configs;
		this.buildCtx = buildCtx;
		this.folder = folder;
		this.engineResources = engineResources;
		this.ops = ops;
		this.log = log;
		this.dependencyData = application.getDependencyData();
	}

	@Override
	public void setLanguageSupport(String language) throws JavascribeException {
		this.lang = language;
	}

	@Override
	public void addSystemAttribute(String name, String type) throws JavascribeException {
		application.getSystemAttributes().put(name, type);
		originateSystemAttribute(name);
	}

	@Override
	public void originateSystemAttribute(String name) {
		originateAttribute(name);
	}

	@Override
	public String getSystemAttribute(String name) {
		dependOnAttribute(name);
		return application.getSystemAttributes().get(name);
	}

	
	@Override
	public void addVariableType(VariableType variableType) {
		Map<String,VariableType> langTypes = application.getTypes().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			application.getTypes().put(lang, langTypes);
		}
		langTypes.put(variableType.getName(), variableType);
		typeDependency(lang, variableType.getName());
	}

	@Override
	public VariableType getVariableType(String name) {
		typeDependency(lang, name);
		Map<String,VariableType> langTypes = application.getTypes().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			application.getTypes().put(lang, langTypes);
		}
		return langTypes.get(name);
	}

	@Override
	public void setObject(String name, Object obj) {
		application.getObjects().put(name, obj);
		objectDependency(name);
	}

	@Override
	public Object getObject(String name) {
		objectDependency(name);
		return application.getObjects().get(name);
	}

	@Override
	public void addSourceFile(SourceFile file) {
		application.getSourceFiles().put(file.getPath(), file);
		originateSourceFile(file);
	}

	@Override
	public SourceFile getSourceFile(String path) {
		SourceFile ret = application.getSourceFiles().get(path);
		if (ret!=null) {
			originateSourceFile(ret);
		}
		return ret;
	}

	@Override
	public String getProperty(String name) {
		return this.configs.get(name);
	}

	@Override
	public BuildContext getBuildContext() {
		return buildCtx;
	}

	@Override
	public ApplicationResource getResource(String path) {
		return folder.getResource(path);
	}

	@Override
	public void addComponent(Component component) {
		originateComponent(component);
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public void addFolderWatcher(String folderPath, FolderWatcher folderWatcher) {
		originateFolderWatcher(folderPath, folderWatcher);
	}

	@Override
	public void addFileProcessor(String filePath, FileProcessor fileProcessor) {
		originateFileProcessor(filePath, fileProcessor);
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return engineResources;
	}

	private void objectDependency(String name) {
		List<Integer> ids = dependencyData.getObjectDependencies().get(name);
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void typeDependency(String lang, String name) {
		
	}
	
	private void dependOnAttribute(String name) {
	}
	
	private void originateAttribute(String name) {

	}
	
	private void originateComponent(Component component) {
		ops.addComponent(id, component);
	}

	private void originateFolderWatcher(String folderPath, FolderWatcher folderWatcher) {
		ops.addFolderWatcher(id, folderPath, folderWatcher);
	}
	
	private void originateFileProcessor(String filePath, FileProcessor fileProcessor) {
		
	}
	
	private void originateSourceFile(SourceFile sourceFile) {
		
	}

}

