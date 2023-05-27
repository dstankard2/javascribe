package net.sf.javascribe.langsupport.java.types;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface ServiceLocator extends JavaVariableType {

	public String instantiate();
	
	/**
	 * Returns a list of variable type names of services available.
	 * @return Variable type names
	 */
	public List<String> getAvailableServices();
	/**
	 * 
	 * @param factoryInstanceName
	 * @param serviceName
	 * @param serviceInstanceName
	 * @param execCtx
	 * @return
	 * @throws JavascribeException
	 */
	public String getService(String factoryInstanceRef,String serviceName,CodeExecutionContext execCtx) throws JavascribeException;

	public JavaCode instantiate(String varName);

}

