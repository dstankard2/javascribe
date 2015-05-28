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
import net.sf.javascribe.api.types.IntegerType;

public class IntegerVariableType extends JavascriptBaseObjectType implements IntegerType {

	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException {
	    StringBuilder build = new StringBuilder();
		ExpressionAtom atom = expr.getAtom(0);
		
	    if (atom instanceof VarReferenceExpressionAtom) {
	        VarReferenceExpressionAtom var = (VarReferenceExpressionAtom)atom;
            build.append(ExpressionUtil.evaluateVarRefExpressionAtom(var, "integer", execCtx));
	    } else if (atom instanceof ConstantReferenceExpressionAtom) {
	        ConstantReferenceExpressionAtom con = (ConstantReferenceExpressionAtom)atom;
	        if (con.getText().equals("null")) {
	        	build.append("null");
	        } else {
	        	build.append(con.getText());
	        }
	    } else if (atom instanceof CodeFragmentExpressionAtom) {
	    	CodeFragmentExpressionAtom a = (CodeFragmentExpressionAtom)atom;
	    	build.append(a.getText());
	    } else {
	    	throw new JavascribeException("Javascript integer type encountered an expression atom it couldn't process");
	    }

		return build.toString();
	}

	@Override
	public boolean isValidIntegerAtom(String atom) {
		return true;
	}

	@Override
	public String getName() {
		return "integer";
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
