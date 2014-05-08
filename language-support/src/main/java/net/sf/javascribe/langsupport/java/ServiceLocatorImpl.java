package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * A basic implementation of Service Locator
 * @author DCS
 *
 */
public class ServiceLocatorImpl implements ServiceLocator {
	String className = null;
	String pkg = null;
	String name = null;
	List<String> services = new ArrayList<String>();
	
	public ServiceLocatorImpl(String name,String pkg,String className) {
		this.pkg = pkg;
		this.className = className;
		this.name = name;
	}
	
	@Override
	public String getImport() {
		return pkg+'.'+className;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.appendCodeText(name+" = new "+className+"();\n");
		
		return code;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.addImport(pkg+'.'+className);
		code.appendCodeText(className+" "+name+" = null;\n");

		return code;
	}

	@Override
	public List<String> getAvailableServices() {
		// TODO Auto-generated method stub
		return services;
	}

	@Override
	public String getService(String factoryInstanceName, String serviceName,
			String serviceInstanceName, CodeExecutionContext execCtx)
			throws JavascribeException {
		StringBuilder build = new StringBuilder();
		
		build.append(serviceInstanceName+" = "+factoryInstanceName+".get"+serviceName+"();\n");
		
		return build.toString();
	}

	@Override
	public String getService(String factoryInstanceName, String serviceName,
			CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder build = new StringBuilder();

		build.append(factoryInstanceName+".get"+serviceName+"()");

		return build.toString();
	}

}
