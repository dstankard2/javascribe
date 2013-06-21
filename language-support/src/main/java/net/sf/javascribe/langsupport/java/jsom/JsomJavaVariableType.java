package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5CompatibleType;

public class JsomJavaVariableType implements JavaVariableType {
	Java5CompatibleType type = null;
	
	public JsomJavaVariableType(Java5CompatibleType type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type.getName();
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		try {
			Java5CompatibleCodeSnippet code = (Java5CompatibleCodeSnippet)type.instantiate(name, value);
			return new JsomJavaCode(code);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception when instantiating a type",e);
		}
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
