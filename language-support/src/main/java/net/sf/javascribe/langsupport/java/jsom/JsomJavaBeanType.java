package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.CodeFragmentExpressionAtom;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class JsomJavaBeanType extends JavaBeanType implements Java5Type {

	public JsomJavaBeanType(String name, String pkg, String cl) {
		super(name, pkg, cl);
	}

	public JsomJavaCode declare(String varName, CodeExecutionContext execCtx) {
		Java5CodeSnippet ret = null;

		ret = new Java5CodeSnippet();
		ret.addImport(getImport());
		ret.append(className);
		ret.append(' ');
		ret.append(varName);
		ret.append(" = null;\n");

		return new JsomJavaCode(ret);
	}

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
	public Java5CompatibleCodeSnippet instantiate(String varName, String value)
			throws CodeGenerationException {
		try {
			return new JavascribeJavaCodeSnippet(instantiate(varName, value,null));
		} catch (JavascribeException e) {
			throw new CodeGenerationException("Javascribe exception while instantiating bean type", e);
		}
	}

	@Override
	public Java5CompatibleCodeSnippet declare(String varName)
			throws CodeGenerationException {
		return new JavascribeJavaCodeSnippet(declare(varName, null));
	}

}
