package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class JsomJavaBeanType extends JavaBeanType implements Java5Type {

	public JsomJavaBeanType(String name, String pkg, String cl) {
		super(name, pkg, cl);
	}

	/*
	public JavaCode declare(String varName, CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(className);
		ret.appendCodeText(" ");
		ret.appendCodeText(varName);
		ret.appendCodeText(" = null;\n");

		return ret;
	}
	*/

	/*
	@Override
	public JsomJavaCode instantiate(String varName, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		if (varName.indexOf('.') > 0) {
			ValueExpression ex = new ValueExpression();
			String v = "new " + className + "()";
			CodeFragmentExpressionAtom a = new CodeFragmentExpressionAtom(v,
					name);
			ex.addAtom(a);
			ret.append(ExpressionUtil.evaluateSetExpression(varName, ex,
					execCtx));
			ret.addImport(getImport());
		} else {
			ret.append(varName + " = new " + className + "();\n");
		}
		return new JsomJavaCode(ret);
	}
	*/

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx) {
		String ret = null;

		ret = varName + ".get";
		ret = ret + Character.toUpperCase(attribName.charAt(0));
		ret = ret + attribName.substring(1) + "()";

		return ret;
	}

	public String getCodeToSetAttribute(String varName, String attribName,
			String value) {
		StringBuilder build = new StringBuilder();

		build.append(varName).append(".set")
				.append(Character.toUpperCase(attribName.charAt(0)));
		build.append(attribName.substring(1)).append('(' + value + ')');

		return build.toString();
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String value, CodeExecutionContext execCtx)
			throws JavascribeException {
		return getCodeToSetAttribute(varName, attribName, value);
	}

	@Override
	public Java5CompatibleCodeSnippet instantiate(String varName, String value) {
		return new JavascribeJavaCodeSnippet(instantiate(varName, value,null));
	}

	@Override
	public Java5CompatibleCodeSnippet declare(String varName)
			throws CodeGenerationException {
		return new JavascribeJavaCodeSnippet(declare(varName, null));
	}

}
