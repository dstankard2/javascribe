package net.sf.javascribe.patterns.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;

public class JpaDaoFactoryType extends JavaServiceObjectType implements ServiceLocator {
	String pu = null;
	String className = null;
	String pkg = null;
	List<String> entityNames = null;
	String ref = null;

	public JpaDaoFactoryType(String ref,String pu,String pkg,String className,List<String> entityNames) {
		super(ModelUtils.getDaoFactoryTypeName(pu),pkg,ModelUtils.getDaoFactoryTypeName(pu));
		this.pu = pu;
		this.pkg = pkg;
		this.className = className;
		this.entityNames = entityNames;
		this.ref = ref;
	}
	
	@Override
	public String getName() {
		return ModelUtils.getDaoFactoryTypeName(pu);
	}

	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.addImport(getImport());
		code.appendCodeText(varName+" = new "+getClassName()+"();\n");

		return code;
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.addImport(getImport());
		ret.appendCodeText(className+" "+varName+" = null;\n");
		return ret;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getImport() {
		return pkg+'.'+className;
	}

	public JavaCode getDaoFactory(String varName,CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(varName+" = new "+className+"();\n");
		
		return ret;
	}

	/** Implementation of ServiceLocator **/
	@Override
	public List<String> getAvailableServices() {
		List<String> ret = new ArrayList<String>();
		
		for(String e : entityNames) {
			ret.add(e+"Dao");
		}
		
		return ret;
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

		ret.appendCodeText(locatorInstanceName+".get"+serviceName+"()");

		return ret;
	}
	/** End of ServiceLocator implementation **/

}

