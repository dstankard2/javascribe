package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * A basic implementation of Service Locator Type
 * @author DCS
 *
 */
public class ServiceLocatorImpl extends JavaVariableTypeBase implements ServiceLocator {
	String className = null;
	String pkg = null;
	String name = null;
	List<String> services = new ArrayList<String>();
	
	public ServiceLocatorImpl(String name,String pkg,String className) {
		super(name,pkg,className);
	}
	
	@Override
	public JavaCode instantiate(String name, String value, CodeExecutionContext execCtx) {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.appendCodeText(name+" = new "+className+"();\n");
		
		return code;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.addImport(pkg+'.'+className);
		code.appendCodeText(className+" "+name+" = null;\n");

		return code;
	}

	@Override
	public List<String> getAvailableServices() {
		return services;
	}

	@Override
	public String getService(String factoryInstanceName, String serviceName,
			CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder build = new StringBuilder();

		build.append(factoryInstanceName+".get"+serviceName+"()");

		return build.toString();
	}

}
