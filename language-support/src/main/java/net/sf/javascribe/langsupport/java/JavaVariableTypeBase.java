package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;

public abstract class JavaVariableTypeBase implements JavaVariableType {

	protected String im = null;
	protected String className = null;
	protected String name = null;
	
	public JavaVariableTypeBase(String name,String pkg,String className) {
		this.name = name;
		if (pkg!=null) {
			this.im = pkg+'.'+className;
		}
		this.className = className;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String instantiate() {
		return "new "+im+"()";
	}

	@Override
	public JavaCode instantiate(String name, String value,
			CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		if (name!=null) {
			ret.appendCodeText(name+" = ");
		}
		ret.appendCodeText(instantiate());
		ret.appendCodeText(";\n");
		return ret;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(className+" "+name+" = null;\n");
		
		return ret;
	}

	@Override
	public String getImport() {
		return im;
	}

	@Override
	public String getClassName() {
		return className;
	}

}
