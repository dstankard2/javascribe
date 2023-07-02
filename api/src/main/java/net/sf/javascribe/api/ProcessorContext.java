package net.sf.javascribe.api;

import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.logging.Log;
import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.api.resources.FileProcessor;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.VariableType;

public interface ProcessorContext {

	public void setLanguageSupport(String language) throws JavascribeException;

	public void addSystemAttribute(String name,String type) throws JavascribeException;
	public void originateSystemAttribute(String name);
	public String getSystemAttribute(String name);

	public void addVariableType(VariableType variableType) throws JavascribeException;
	public VariableType getVariableType(String name) throws JavascribeException;
	public void modifyVariableType(VariableType variableType) throws JavascribeException;

	public void setObject(String name,Object obj);
	public Object getObject(String name);

	public void addSourceFile(SourceFile file);
	public SourceFile getSourceFile(String path);

	public String getProperty(String name);

	public BuildContext getBuildContext();
	
	public ApplicationResource getResource(String path);

	public void addComponent(Component component);

	Log getLog();

	void addFolderWatcher(String folderPath,FolderWatcher folderWatcher);
	
	void addFileProcessor(String filePath,FileProcessor fileProcessor);
	
	ApplicationContext getApplicationContext();
	
}

