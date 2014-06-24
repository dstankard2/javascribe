package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * This type represents a business service with business operations that can be 
 * called.
 * This type is instantiated via the instantiate method (default constructor).
 * @author Dave
 */
public class JavaServiceObjectType implements JavaVariableType,Injectable {
	private String className = null;
	private String pkg = null;
	private String name = null;
	private List<JavaOperation> methods = new ArrayList<JavaOperation>();
	
	public JavaServiceObjectType(String name,String pkg,String className) {
		this.className = className;
		this.pkg = pkg;
		this.name = name;
	}
	
	public void addMethod(JavaOperation op) {
		methods.add(op);
	}
	
	public List<JavaOperation> getMethods(String name) {
		List<JavaOperation> ret = new ArrayList<JavaOperation>();
		
		for(JavaOperation op : methods) {
			if (op.getName().equals(name)) ret.add(op);
		}
		
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(getClassName()+' '+name+" = null;\n");
		
		return ret;
	}

	public JavaCode declare(String varName) throws JavascribeException {
		return declare(varName,null);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	@Override
	public JavaCode instantiate(String name, String value,CodeExecutionContext execCtx) throws JavascribeException {
		// TODO Auto-generated method stub
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.appendCodeText(name+" = new "+getClassName()+"();\n");
		
		return ret;
	}

	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCodeImpl();

		ret = declare(instanceName,execCtx);
		JavaUtils.append(ret, instantiate(instanceName, null, execCtx));
		
		return ret;
	}

	public JavaCode instantiate(String varName, String value)
			throws JavascribeException {
		return instantiate(varName,value,null);
	}
	
	public List<JavaOperation> getMethods() {
		return methods;
	}

}

