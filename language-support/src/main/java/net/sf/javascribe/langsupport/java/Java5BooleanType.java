package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.BooleanExpressionAtom;
import net.sf.javascribe.api.expressions.BooleanOperator;
import net.sf.javascribe.api.expressions.ConstantReferenceExpressionAtom;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;
import net.sf.javascribe.api.types.BooleanType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5BooleanType implements BooleanType,JavaVariableType,Java5Type {

	@Override
	public String getClassName() {
	    return "Boolean";
	}

	@Override
	public String getImport() { 
		return null;
	}
	
	@Override
	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder buf = new StringBuilder();
		
	    if (expr.getSize()!=1) {
	        throw new IllegalArgumentException("A boolean expression may have only 1 atom");
	    }

	    if (expr.getAtomType(0)==ValueExpression.CONST_REF_EXPR_ATOM) {
	        ConstantReferenceExpressionAtom con = expr.getConstantReferenceExpressionEntry(0);
	        if (con.getText().equalsIgnoreCase("true")) {
	            buf.append("Boolean.TRUE");
	        } else if (con.getText().equals("false")) {
	            buf.append("Boolean.FALSE");
	        } else {
	        	throw new JavascribeException("Found invalid boolean value '"+con.getText()+"'");
	        }
	    } else if (expr.getAtomType(0)==ValueExpression.VAR_REF_EXPR_ATOM) {
            buf.append(ExpressionUtil.evaluateVarRefExpressionAtom(expr.getVarReferenceExpressionEntry(0), "boolean", execCtx));
	    } else if (expr.getAtomType(0)==ValueExpression.CODE_FRAGMENT_EXPR_ATOM) {
	    	buf.append(expr.getCodeFragmentExpressionEntry(0).getText());
	    } else if (expr.getAtomType(0)==ValueExpression.BOOLEAN_EXPR_ATOM) {
	    	BooleanExpressionAtom bool = expr.getBooleanExpressionEntry(0);
	    	if (bool.getOperator()==BooleanOperator.EQUAL) {
	    		String temp = ExpressionUtil.getEvaluatedExpression(bool.getLeftOperand(),execCtx);
	    		buf.append(temp).append(".toString().equals(");
	    		temp = ExpressionUtil.getEvaluatedExpression(bool.getRightOperand(),execCtx);
	    		buf.append(temp).append(")");
	    	}
	    }

		return buf.toString();
	}

	@Override
	public String getName() {
		return "boolean";	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append(varName+" = "+value+";\n");
		
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append("Boolean "+varName+";\n");
		
		return ret;
	}

	@Override
	public JavaCode instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		return new JsomJavaCode(instantiate(name, value));
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		return new JsomJavaCode(declare(name));
	}

	public boolean isValidBooleanAtom(String atom) {
		if (atom.equalsIgnoreCase("true")) return true;
		if (atom.equalsIgnoreCase("false")) return true;
		return false;
	}
	
}

