package net.sf.javascribe.langsupport.java;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public interface ServiceLocator extends JavaVariableType {

	public List<String> getAvailableServices();
	public JavaCode getService(String factoryInstanceName,String serviceName,String serviceInstanceName,CodeExecutionContext execCtx) throws JavascribeException;
	public JavaCode getService(String factoryInstanceName,String serviceName,CodeExecutionContext execCtx) throws JavascribeException;

}

