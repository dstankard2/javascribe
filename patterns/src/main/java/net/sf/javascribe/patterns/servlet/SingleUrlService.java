package net.sf.javascribe.patterns.servlet;

import java.util.ArrayList;
import java.util.List;

public class SingleUrlService {
	String path = "";
	List<String> queryParams = new ArrayList<String>();
	String returnFormat = "";
	String returnType = "";
	String requestMethod = "";
	String requestBody = "";
	
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
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	
}
