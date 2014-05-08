package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * A service represents a Java business logic service that is found via a service locator.
 * @author Dave
 */
public class LocatedJavaServiceObjectType extends JavaServiceObjectType {
	protected String locatorClass = null;
	
	public LocatedJavaServiceObjectType(String locatorClass,String serviceName,String pkg,String className) {
		super(serviceName,pkg,className);
		this.locatorClass = locatorClass;
	}

	public String getLocatorClass() {
		return locatorClass;
	}
	
	public JavaCode locateService(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();

		ret.addImport(getImport());
		ret.appendCodeText(varName+" = new "+locatorClass+"().get"+getName()+"();\n");

		return ret;
	}
	
	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		JavaUtils.append(ret, declare(instanceName));
		JavaUtils.append(ret, locateService(instanceName,execCtx));
		return ret;
	}
	
	
}
