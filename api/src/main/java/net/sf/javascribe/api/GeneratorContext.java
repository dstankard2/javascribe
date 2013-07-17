package net.sf.javascribe.api;

import net.sf.javascribe.api.config.ComponentBase;

public interface GeneratorContext {

	public void setLanguageSupport(String languageName) throws JavascribeException;
	public String getProperty(String name);
	public String getRequiredProperty(String name) throws JavascribeException;
	public String getAttributeType(String attribute);
	public VariableType getType(String typeName);
	public String getBuildRoot();
	public SourceFile getSourceFile(String path);
	public void addSourceFile(SourceFile sourceFile) throws JavascribeException;
	public TypeResolver getTypes();
	public void addAttribute(String name,String type) throws JavascribeException;
	public EngineProperties getEngineProperties();

	public void putObject(String name,Object object);
	public Object getObject(String name);
	public void addComponent(ComponentBase component);

}

