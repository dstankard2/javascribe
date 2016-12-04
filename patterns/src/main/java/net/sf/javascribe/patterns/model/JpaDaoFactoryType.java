package net.sf.javascribe.patterns.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.Injectable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableTypeBase;
import net.sf.javascribe.langsupport.java.ServiceLocator;

public class JpaDaoFactoryType extends JavaVariableTypeBase implements ServiceLocator,Injectable {
	String pu = null;
	List<String> entityNames = null;
	String ref = null;

	public JpaDaoFactoryType(String ref,String pu,String pkg,String className,List<String> entityNames) {
		super(ModelUtils.getDaoFactoryTypeName(pu),pkg,className);
		this.pu = pu;
		this.entityNames = entityNames;
		this.ref = ref;
	}
	
	@Override
	public String getName() {
		return ModelUtils.getDaoFactoryTypeName(pu);
	}

	/*
	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaCodeImpl code = new JavaCodeImpl();
		
		code.addImport(getImport());
		code.appendCodeText(varName+" = new "+getClassName()+"();\n");

		return code;
	}
	*/

	/*
	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		ret.addImport(getImport());
		ret.appendCodeText(className+" "+varName+" = null;\n");
		return ret;
	}
	*/

	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCodeImpl();

		ret = declare(instanceName,execCtx);
		JavaUtils.append(ret, instantiate(instanceName, null, execCtx));
		
		return ret;
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
	public String getService(String locatorInstanceRef,String serviceName,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder build = new StringBuilder();

		build.append(locatorInstanceRef+".get"+serviceName+"()");

		return build.toString();
	}
	/** End of ServiceLocator implementation **/

}

