package net.sf.javascribe.engine.data.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import net.sf.javascribe.engine.StaleDependencyException;
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
		this.engineResources = ComponentContainer.get().getComponent(EngineResources.class);
		this.ops = ComponentContainer.get().getComponent(ProcessingContextOperations.class);
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
		return application.getSystemAttribute(name);
	}

	@Override
	public void addVariableType(VariableType variableType) throws JavascribeException {
		String name = variableType.getName();

		if (lang==null) {
			throw new JavascribeException("No language support selected");
		}
		Map<String,VariableType> langTypes = application.getApplicationTypes().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			application.getApplicationTypes().put(lang, langTypes);
		}
		if (langTypes.get(name)!=null) {
			throw new JavascribeException("Already found a variable type called '"+name+"' for language '"+lang+"'");
		}
		langTypes.put(name, variableType);
		typeDependency(lang, name);
	}

	@Override
	public VariableType getVariableType(String name) throws JavascribeException {
		if (lang==null) {
			throw new JavascribeException("No language support selected");
		}
		typeDependency(lang, name);
		return application.getType(lang, name);
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
		objectDependency(name);
		if (application.getObjects().get(name)!=null) {
			throw new StaleDependencyException(id);
		}
		application.getAddedObjects().put(name, obj);
	}

	@Override
	public Object getObject(String name) {
		Object ret = application.getAddedObjects().get(name);
		if (ret==null) {
			ret = application.getObjects().get(name);
			objectDependency(name);
			if (ret!=null) {
				// The object is in the application but was not added this run.
				// In this case the item should go stale and be removed.
				throw new StaleDependencyException(id);
			}
		} else {
			objectDependency(name);
		}
		return ret;
	}

	@Override
	public void addSourceFile(SourceFile file) {
		application.getAddedSourceFiles().put(file.getPath(), file);
		originateSourceFile(file);
	}

	@Override
	public SourceFile getSourceFile(String path) {
		SourceFile ret = application.getSourceFiles().get(path);
		if (ret==null) {
			ret = application.getAddedSourceFiles().get(path);
		} else {
			this.originateSourceFile(ret);
			throw new StaleDependencyException(id);
		}
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
	public void addFolderWatcher(String folderPath, FolderWatcher folderWatcher) {
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
		Set<Integer> ids = dependencyData.getObjectDependencies().get(name);
		if (ids==null) {
			ids = new HashSet<>();
			dependencyData.getObjectDependencies().put(name, ids);
		}
		ids.add(id);
	}
	
	private void typeDependency(String lang, String name) {
		Map<String,Set<Integer>> langTypes = dependencyData.getTypeDependencies().get(lang);
		if (langTypes==null) {
			langTypes = new HashMap<>();
			dependencyData.getTypeDependencies().put(lang, langTypes);
		}
		Set<Integer> ids = langTypes.get(name);
		if (ids==null) {
			ids = new HashSet<>();
			langTypes.put(name, ids);
		}
		if (!ids.contains(id)) {
			ids.add(id);
		}
	}
	
	private void dependOnAttribute(String name) {
		Set<Integer> ids = dependencyData.getAttributeDependencies().get(name);
		if (ids==null) {
			ids = new HashSet<>();
			dependencyData.getAttributeDependencies().put(name, ids);
		}
		ids.add(id);
	}
	
	private void originateAttribute(String name) {
		Set<Integer> ids = dependencyData.getAttributeOriginators().get(name);
		if (ids==null) {
			ids = new HashSet<>();
			dependencyData.getAttributeOriginators().put(name, ids);
		}
		ids.add(id);
		
		// This should also depend on the system attribute, or there will be an exception when removing the item
		ids = dependencyData.getAttributeDependencies().get(name);
		if (ids==null) {
			ids = new HashSet<>();
			dependencyData.getAttributeDependencies().put(name, ids);
		}
		ids.add(id);
	}
	
	private void originateComponent(Component component) {
		ops.addComponent(id,  component, configs, folder, application);
	}

	private void originateFolderWatcher(String folderPath, FolderWatcher folderWatcher) {
		ops.addFolderWatcher(id, folderPath, folderWatcher, configs, folder, application);
	}
	
	private void originateFileProcessor(UserFile file, FileProcessor fileProcessor) {
		ops.addFileProcessor(id, file, fileProcessor, configs, folder, application);
	}
	
	private void originateSourceFile(SourceFile sourceFile) {
		String path = sourceFile.getPath();
		Set<Integer> ids = application.getDependencyData().getSrcDependencies().get(path);
		if (ids==null) {
			ids = new HashSet<>();
			application.getDependencyData().getSrcDependencies().put(path, ids);
		}
		ids.add(id);
	}

}

