package net.sf.javascribe.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.EngineProperties;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.LanguageSupport;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.TypeResolver;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.config.ComponentBase;

public class ProcessorContextImpl implements ProcessorContext {
	EnginePropertiesImpl engineProps = null;
	Map<String,LanguageSupport> languageSupport = null;
	Map<String,TypeResolverImpl> typeMap = null;
	TypeResolverImpl currentLanguageSupport = null;
	Map<String,String> systemAttributes = null;
	List<SourceFile> sourceFiles = null;
	String buildRoot = null;
	Map<String,String> properties = null;
	List<ComponentBase> addedComponents = new ArrayList<ComponentBase>();
	Map<String,Object> objects = null;
	
	public ProcessorContextImpl(String buildRoot,EnginePropertiesImpl props,Map<String,LanguageSupport> languageSupport,Map<String,String> systemAttributes,Map<String,TypeResolverImpl> typeMap,List<SourceFile> sourceFiles,Map<String,String> properties,Map<String,Object> objects) {
		engineProps = props;
		this.languageSupport = languageSupport;
		this.systemAttributes = systemAttributes;
		this.typeMap = typeMap;
		this.sourceFiles = sourceFiles;
		this.buildRoot = buildRoot;
		this.objects = objects;
		this.properties = properties;
	}
	
	public EngineProperties getEngineProperties() {
		return engineProps;
	}
	
	public void setLanguageSupport(String languageName) throws JavascribeException {
		if (languageSupport.get(languageName)==null) {
			throw new JavascribeException("Found no language support for language '"+languageName+"'");
		}
		if (typeMap.get(languageName)==null) {
			currentLanguageSupport = new TypeResolverImpl();
			LanguageSupport supp = languageSupport.get(languageName);
			List<VariableType> additions = supp.getBaseVariableTypes();
			for(VariableType v : additions) {
				currentLanguageSupport.addType(v);
			}
			typeMap.put(languageName, currentLanguageSupport);
		} else {
			currentLanguageSupport = typeMap.get(languageName);
		}
	}

	public Map<String,String> getAllProperties() {
		return properties;
	}
	public String getProperty(String name) {
		return properties.get(name);
	}

	public String getRequiredProperty(String name) throws JavascribeException {
		if (properties.get(name)==null) {
			throw new JavascribeException("Could not find required property '"+name+"'");
		}
		return properties.get(name);
	}

	public String getAttributeType(String attributeName) {
		return systemAttributes.get(attributeName);
	}

	public VariableType getType(String typeName) {
		if (currentLanguageSupport==null) return null;
		return currentLanguageSupport.getType(typeName);
	}

	public String getBuildRoot() {
		return buildRoot;
	}

	public SourceFile getSourceFile(String path) {
		SourceFile ret = null;
		
		for(SourceFile src : sourceFiles) {
			if (src.getPath().equals(path)) {
				ret = src;
				break;
			}
		}
		
		return ret;
	}

	public void addSourceFile(SourceFile sourceFile) throws JavascribeException {
		for(SourceFile src : sourceFiles) {
			if (src.getPath().equals(sourceFile.getPath())) {
				throw new JavascribeException("Added duplicate source files as path '"+src.getPath()+"'");
			}
		}
		sourceFiles.add(sourceFile);
	}

	public TypeResolver getTypes() {
		return currentLanguageSupport;
	}

	public void addAttribute(String name, String type) throws JavascribeException {
		if (systemAttributes.get(name)!=null) {
			if (!systemAttributes.get(name).equals(type)) {
				throw new JavascribeException("Cannot add attribute '"+name+"' - it already exists as another type");
			}
		}
		systemAttributes.put(name, type);
	}

	public void addComponent(ComponentBase component) {
		addedComponents.add(component);
	}
	
	public List<ComponentBase> getAddedComponents() {
		return addedComponents;
	}

	public void putObject(String name, Object object) {
		objects.put(name, object);
	}

	public Object getObject(String name) {
		return objects.get(name);
	}
	
}
