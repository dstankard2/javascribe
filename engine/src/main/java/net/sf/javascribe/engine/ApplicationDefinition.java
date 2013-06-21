package net.sf.javascribe.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.config.ComponentSet;

public class ApplicationDefinition {
	private HashMap<String,String> attributes = new HashMap<String,String>();
	private List<ComponentSet> components = new ArrayList<ComponentSet>();
	private HashMap<String,String> globalProperties = new HashMap<String,String>();
	private String appName = null;
	private String buildRoot = null;
	
	public HashMap<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}
	public List<ComponentSet> getComponents() {
		return components;
	}
	public void setComponents(List<ComponentSet> components) {
		this.components = components;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public HashMap<String, String> getGlobalProperties() {
		return globalProperties;
	}
	public void setGlobalProperties(HashMap<String, String> globalProperties) {
		this.globalProperties = globalProperties;
	}
	public String getBuildRoot() {
		return buildRoot;
	}
	public void setBuildRoot(String buildRoot) {
		this.buildRoot = buildRoot;
	}

}
