package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * This type represents a business service that is found via a service 
 * locator.  It has dependencies which are injected via set methods, along 
 * with an impl class that the service locator actually instantiates.
 * @author Dave
 */
public class LocatedJavaServiceObjectType extends JavaServiceObjectType {
	protected String locatorClass = null;
	private List<String> dependancyNames = new ArrayList<String>();
	private String implClass = null;
	
	public LocatedJavaServiceObjectType(String locatorClass,String serviceName,String pkg,String className) {
		super(serviceName,pkg,className);
		this.locatorClass = locatorClass;
	}

	public String getLocatorClass() {
		return locatorClass;
	}
	
	public JavaCode locateService(String varName,CodeExecutionContext execCtx) {
		return locateService(varName,execCtx,true);
	}
	
	protected JavaCode locateService(String varName,CodeExecutionContext execCtx,boolean completeCodeLine) {
		JavaCodeImpl ret = new JavaCodeImpl();

		ret.addImport(getImport());
		if (varName!=null) {
			ret.appendCodeText(varName+" = ");
		}
		ret.appendCodeText("new "+locatorClass+"().get"+getName()+"()");
		if (completeCodeLine) {
			ret.appendCodeText(";\n");
		}

		return ret;
	}

	public String getAnonymousInstance() {
		JavaCode code = locateService(null,null,false);
		return code.getCodeText();
	}

	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		JavaUtils.append(ret, declare(instanceName));
		JavaUtils.append(ret, locateService(instanceName,execCtx));
		ret.appendCodeText(";\n");
		return ret;
	}
	
	public void addDependancy(String name) {
		dependancyNames.add(name); 
	}

	public List<String> getDependancyNames() {
		return dependancyNames;
	}
	
	public String getImplClass() {
		return implClass;
	}

	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}

}
