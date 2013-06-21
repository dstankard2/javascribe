package net.sf.javascribe.patterns.custom;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;


public class CustomLogicLocatorType implements ServiceLocator {
	List<String> services = new ArrayList<String>();
	String pkg = null;
	String className;
	
	public CustomLogicLocatorType(String pkg,String className) {
		this.pkg = pkg;
		this.className = className;
	}
	
	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getImport() {
		return pkg+'.'+className;
	}

	@Override
	public String getName() {
		return className;
	}

	/*
	public Java5CodeSnippet getDomainService(String varName,String typeName,CodeExecutionContext execCtx) throws CodeGenerationException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JavaServiceObjectType type = null;
		
		if (typeName==null) {
			throw new CodeGenerationException("Invalid custom logic object specified");
		}
		else if (!services.contains(typeName)) {
			throw new CodeGenerationException("Custom Logic '"+typeName+"' not found in service set '"+className+"'");
		}

		type = (JavaServiceObjectType)execCtx.getType(typeName);
		if (type==null) {
			throw new CodeGenerationException("Could not find custom object type "+typeName);
		}
		ret.addImport(type.getImport());
		ret.addImport(getImport());
		ret.append(varName+" = "+className+".get"+typeName+"();\n");

		return ret;
	}
	*/

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		ret.addImport(getImport());
		ret.append(name+" = new "+className+"();\n");

		return new JsomJavaCode(ret);
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		ret.addImport(getImport());
		ret.append(className+" "+name+" = null;\n");

		return new JsomJavaCode(ret);
	}

	@Override
	public List<String> getAvailableServices() {
		return services;
	}

	@Override
	public JavaCode getService(String factoryInstanceName, String serviceName,
			String serviceInstanceName,
			net.sf.javascribe.api.CodeExecutionContext execCtx)
			throws net.sf.javascribe.api.JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		if (!services.contains(serviceName)) {
			throw new JavascribeException("Custom logic does not know of a service called '"+serviceName+"'");
		}
		
		ret.addImport(getImport());
		ret.append(serviceName+" "+serviceInstanceName+" = new "+className+"().get"+serviceName+"();\n");

		return new JsomJavaCode(ret);
	}

	@Override
	public JavaCode getService(String factoryInstanceName, String serviceName,
			net.sf.javascribe.api.CodeExecutionContext execCtx)
			throws net.sf.javascribe.api.JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		ret.addImport(getImport());
		ret.append(factoryInstanceName+".get"+serviceName+"();\n");
		return new JsomJavaCode(ret);
	}

}

