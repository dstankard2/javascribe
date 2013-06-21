package net.sf.javascribe.patterns.servlet;

import java.util.ArrayList;
import java.util.List;

public class SingleUrlService {
	String path = null;
	List<String> queryParams = new ArrayList<String>();
	String returnFormat = null;
	String returnType = null;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<String> getQueryParams() {
		return queryParams;
	}
	public void setQueryParams(List<String> queryParams) {
		this.queryParams = queryParams;
	}
	public String getReturnFormat() {
		return returnFormat;
	}
	public void setReturnFormat(String returnFormat) {
		this.returnFormat = returnFormat;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
}
