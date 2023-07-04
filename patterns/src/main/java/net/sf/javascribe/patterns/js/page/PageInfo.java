package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.types.ServiceOperation;

public class PageInfo {

	private String name = null;
	private String pageRendererObj = null;
	private ServiceOperation pageRendererRule = null;
	// TODO: Consider replacing page functions with a more specific web service client object
	private List<PageFnDef> functions = new ArrayList<>();
	private String modelTypeName = null;
	private String pageTypeName = null;

	public String getPageRendererObj() {
		return pageRendererObj;
	}
	public void setPageRendererObj(String pageRendererObj) {
		this.pageRendererObj = pageRendererObj;
	}
	public ServiceOperation getPageRendererRule() {
		return pageRendererRule;
	}
	public void setPageRendererRule(ServiceOperation pageRendererRule) {
		this.pageRendererRule = pageRendererRule;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PageFnDef> getFunctions() {
		return functions;
	}
	public void setFunctions(List<PageFnDef> functions) {
		this.functions = functions;
	}
	public String getModelTypeName() {
		return modelTypeName;
	}
	public void setModelTypeName(String modelTypeName) {
		this.modelTypeName = modelTypeName;
	}
	public String getPageTypeName() {
		return pageTypeName;
	}
	public void setPageTypeName(String pageTypeName) {
		this.pageTypeName = pageTypeName;
	}

}
