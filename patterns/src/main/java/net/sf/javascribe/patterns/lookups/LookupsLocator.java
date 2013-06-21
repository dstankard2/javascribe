package net.sf.javascribe.patterns.lookups;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;

public class LookupsLocator implements ServiceLocator {
	private String pkg = null;
	private String className = null;
	List<String> lookups = new ArrayList<String>();
	
	public List<String> getLookups() {
		return lookups;
	}
	
	public LookupsLocator(String pkg,String className) {
		this.pkg = pkg;
		this.className = className;
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
		return className;
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.addImport(pkg+'.'+className);
		ret.append(name+" = new "+className+"();\n");
		
		return new JsomJavaCode(ret);
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.addImport(pkg+'.'+className);
		ret.append(className+' '+name+" = null;\n");
		
		return new JsomJavaCode(ret);
	}

	@Override
	public List<String> getAvailableServices() {
		return lookups;
	}

	@Override
	public JavaCode getService(String factoryInstanceName, String serviceName,
			String serviceInstanceName, CodeExecutionContext execCtx)
			throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append(serviceInstanceName+" = "+factoryInstanceName+".get"+serviceName+"();\n");

		return new JsomJavaCode(ret);
	}

	@Override
	public JavaCode getService(String factoryInstanceName, String serviceName,
			CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append(factoryInstanceName+".get"+serviceName+"();\n");

		return new JsomJavaCode(ret);
	}

}
