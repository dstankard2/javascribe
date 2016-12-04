package net.sf.javascribe.patterns.custom;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableTypeBase;
import net.sf.javascribe.langsupport.java.ServiceLocator;

// TODO: Not sure this is used.
@Deprecated
public class CustomLogicLocatorType extends JavaVariableTypeBase implements ServiceLocator {
	List<String> services = new ArrayList<String>();
	String pkg = null;
	String className;
	
	public CustomLogicLocatorType(String pkg,String className) {
		super(className,pkg,className);
	}
	
	@Override
	public List<String> getAvailableServices() {
		return services;
	}

	@Override
	public String getService(String factoryInstanceName, String serviceName,CodeExecutionContext execCtx) 
			throws JavascribeException {
		StringBuilder build = new StringBuilder();

		if (!services.contains(serviceName)) {
			throw new JavascribeException("Custom logic does not know of a service called '"+serviceName+"'");
		}
		
		build.append("new "+pkg+"."+className+"().get"+serviceName+"();\n");

		return build.toString();
	}

}

