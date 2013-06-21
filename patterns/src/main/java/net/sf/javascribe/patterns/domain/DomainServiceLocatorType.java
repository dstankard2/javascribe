package net.sf.javascribe.patterns.domain;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.Injectable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;

public class DomainServiceLocatorType extends JavaServiceObjectType implements Injectable,ServiceLocator {
	List<String> serviceNames = null;

	public List<String> getServiceNames() {
		return serviceNames;
	}
	
	public DomainServiceLocatorType(String name,String pkg,String className) {
		super(name,pkg,className);
	}
	
	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("JpaDaoFactory does not support instantiate");
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.addImport(getImport());
		ret.appendCodeText(getClassName()+" "+varName+" = null;\n");
		return ret;
	}

	/** Injectable **/
	@Override
	public JavaCode getInstance(String instanceName,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.addImport(getImport());
		code.appendCodeText(instanceName+" = new "+getClassName()+"();\n");

		return code;
	}
	
	/** Implementation of ServiceLocator **/
	@Override
	public List<String> getAvailableServices() {
		return serviceNames;
	}

	@Override
	public JavaCode getService(String locatorInstanceName,String serviceName,String serviceInstanceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.appendCodeText(serviceInstanceName+" = "+locatorInstanceName+".get"+serviceName+"();\n");
		
		return ret;
	}

	@Override
	public JavaCode getService(String locatorInstanceName,String serviceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.appendCodeText(locatorInstanceName+".get"+serviceName+"();\n");
		
		return ret;
	}
	/** End of ServiceLocator implementation **/

}

