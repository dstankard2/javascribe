package net.sf.javascribe.patterns.servlet;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;

public class UrlWebServiceType implements VariableType {
	String webService = null;
	Map<String,SingleUrlService> services = new HashMap<String,SingleUrlService>();
	
	@Override
	public String getName() {
		return webService;
	}

	@Override
	public Code instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("UrlWebService cannot be instantiated.");
	}

	@Override
	public Code declare(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("UrlWebService cannot be declared.");
	}

	public String getWebService() {
		return webService;
	}

	public void setWebService(String webService) {
		this.webService = webService;
	}

	public Map<String, SingleUrlService> getServices() {
		return services;
	}

}

