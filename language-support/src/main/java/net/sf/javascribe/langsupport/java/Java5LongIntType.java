package net.sf.javascribe.langsupport.java;

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
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5LongIntType implements IntegerType,JavaVariableType,Java5Type {

	@Override
	public String getClassName() {
		return "Long";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getName() {
		return "longint";
	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append(varName+" = "+value+";\n");
		
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append("Long "+varName+" = null;\n");
		return ret;
	}

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
	        	build.append("new Long(").append(con.getText()).append(')');
	        }
	    } else if (atom instanceof CodeFragmentExpressionAtom) {
	    	CodeFragmentExpressionAtom a = (CodeFragmentExpressionAtom)atom;
	    	build.append(a.getText());
	    } else {
	    	throw new JavascribeException("Java longint type encountered an expression atom it couldn't process");
	    }

		return build.toString();
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		return new JsomJavaCode(instantiate(name,value));
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		return new JsomJavaCode(declare(name));
	}
	
	public boolean isValidIntegerAtom(String atom) {
		try {
			Long.parseLong(atom);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

}

