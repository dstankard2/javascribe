package net.sf.javascribe.engine.data.processing;

import java.util.ArrayList;
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
import net.sf.javascribe.engine.ComponentContainer;
import net.sf.javascribe.engine.data.ApplicationData;
import net.sf.javascribe.engine.data.DependencyData;
import net.sf.javascribe.engine.data.files.ApplicationFolderImpl;
import net.sf.javascribe.engine.data.files.UserFile;
import net.sf.javascribe.engine.data.files.WatchedResource;
import net.sf.javascribe.engine.service.EngineResources;
import net.sf.javascribe.engine.service.ProcessingContextOperations;

public class ProcessorContextImpl implements ProcessorContext {
	private ApplicationData application;
	private ProcessorLog log;
	private String lang = null;
	private int id;
	private Map<String,String> configs;
	private ApplicationFolderImpl folder;
	private EngineResources engineResources;
	private ProcessingContextOperations ops;
	private DependencyData dependencyData;
	
	public ProcessorContextImpl(ApplicationData application, int id,
			Map<String,String> configs, ApplicationFolderImpl folder, 
			ProcessorLog log) {
		this.application = application;
		this.id = id;
		this.configs = configs;
		this.folder = folder;
		this.log = log;
		this.dependencyData = application.getDependencyData();
		this.engineResources = ComponentContainer.get().getComponent("EngineResources", EngineResources.class);
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
	public String getSystemAttribute(String name) throws JavascribeException {
		if (application.getSystemAttribute(name)==null) {
			throw new JavascribeException("There is no system attribute '"+name+"'");
		}
		dependOnAttribute(name);
		return application.getSystemAttribute(name);
	}

	
	@Override
	public void addVariableType(VariableType variableType) throws JavascribeException {
		if (lang==null) {
			throw new JavascribeException("No language support selected");
		}
		Map<String,VariableType> langTypes = application.getTypes().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			application.getTypes().put(lang, langTypes);
		}
		langTypes.put(variableType.getName(), variableType);
		typeDependency(lang, variableType.getName());
	}

	@Override
	public VariableType getVariableType(String name) throws JavascribeException {
		if (lang==null) {
			throw new JavascribeException("No language support selected");
		}
		typeDependency(lang, name);
		Map<String,VariableType> langTypes = application.getTypes().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			application.getTypes().put(lang, langTypes);
		}
		return langTypes.get(name);
	}
	
	@Override
	public void modifyVariableType(VariableType variableType) throws JavascribeException {
		if (lang==null) {
			throw new JavascribeException("No language support selected");
		}
		String name = variableType.getName();
		ops.checkVariableTypeStale(lang, name, application);
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
		application.getAddedSourceFiles().put(file.getPath(), file);
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
		return folder.getBuildContext();
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
	public void addFolderWatcher(String folderPath, FolderWatcher folderWatcher) throws JavascribeException {
		originateFolderWatcher(folderPath, folderWatcher);
	}

	@Override
	public void addFileProcessor(String filePath, FileProcessor fileProcessor) {
		WatchedResource resource = folder.getResource(filePath);
		if ((resource!=null) && (resource instanceof UserFile)) {
			UserFile userFile = (UserFile)resource;
			originateFileProcessor(userFile, fileProcessor);
		}
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return engineResources;
	}

	private void objectDependency(String name) {
		List<Integer> ids = dependencyData.getObjectDependencies().get(name);
		if (ids==null) {
			ids = new ArrayList<>();
			dependencyData.getObjectDependencies().put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void typeDependency(String lang, String name) {
		Map<String,List<Integer>> langTypes = dependencyData.getTypeDependencies().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			dependencyData.getTypeDependencies().put(lang, langTypes);
		}
		List<Integer> ids = langTypes.get(name);
		if (ids==null) {
			ids = new ArrayList<>();
			langTypes.put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void dependOnAttribute(String name) {
		List<Integer> ids = dependencyData.getAttributeDependencies().get(name);
		if (ids==null) {
			ids = new ArrayList<>();
			dependencyData.getAttributeDependencies().put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void originateAttribute(String name) {
		List<Integer> ids = dependencyData.getAttributeOriginators().get(name);
		if (ids==null) {
			ids = new ArrayList<>();
			dependencyData.getAttributeOriginators().put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
		
		// This should also depend on the system attribute, or there will be an exception when removing the item
		ids = dependencyData.getAttributeDependencies().get(name);
		if (ids==null) {
			ids = new ArrayList<>();
			dependencyData.getAttributeDependencies().put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void originateComponent(Component component) {
		ops.addComponent(id,  component, configs, folder, application);
	}

	private void originateFolderWatcher(String folderPath, FolderWatcher folderWatcher) throws JavascribeException {
		ops.addFolderWatcher(id, folderPath, folderWatcher, configs, folder, application);
	}
	
	private void originateFileProcessor(UserFile file, FileProcessor fileProcessor) {
		ops.addFileProcessor(id, file, fileProcessor, configs, folder, application);
	}
	
	private void originateSourceFile(SourceFile sourceFile) {
		String path = sourceFile.getPath();
		List<Integer> ids = application.getDependencyData().getSrcDependencies().get(path);
		if (ids==null) {
			ids = new ArrayList<>();
			application.getDependencyData().getSrcDependencies().put(path, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}

}

