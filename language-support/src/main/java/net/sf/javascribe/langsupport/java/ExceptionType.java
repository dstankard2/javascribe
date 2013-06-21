package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class ExceptionType implements JavaVariableType {
	private String name = null;
	private String pkg = null;
	private String className = null;
	
	public ExceptionType(String name,String pkg,String className) {
		this.name = name;
		this.pkg = pkg;
		this.className = className;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public JavaCode declare(String varName) throws JavascribeException {
		return (JavaCode)declare(varName,null);
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
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("You cannot instantiate an exception automatically.");
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(className+" "+name+" = null;\n");

		return ret;
	}

}
