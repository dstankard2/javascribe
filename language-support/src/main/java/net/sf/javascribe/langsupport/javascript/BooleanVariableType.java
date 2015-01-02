package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.Code;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.CodeFragmentExpressionAtom;
import net.sf.javascribe.api.expressions.ConstantReferenceExpressionAtom;
import net.sf.javascribe.api.expressions.ExpressionAtom;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;
import net.sf.javascribe.api.expressions.VarReferenceExpressionAtom;
import net.sf.javascribe.api.types.BooleanType;

public class BooleanVariableType implements JavascriptVariableType,BooleanType {

	@Override
	public String evaluateExpression(ValueExpression expr,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("Not supported");
	}

	@Override
	public boolean isValidBooleanAtom(String atom) {
		return false;
	}

	@Override
	public String getName() {
		return "boolean";
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append(name+" = "+value+";\n");
		return ret;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append("var "+name+";\n");
		return ret;
	}

}
