package net.sf.javascribe.api;

public interface TypeResolver {

	public void addType(VariableType type) throws JavascribeException;
	public void addOrReplaceType(VariableType type);
	public VariableType getType(String name);
	public void setLanguageSupport(String language,String name);
	
}
