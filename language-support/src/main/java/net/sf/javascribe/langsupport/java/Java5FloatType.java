package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5FloatType implements JavaVariableType,Java5Type {

	@Override
	public String getClassName() {
		return "Float";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getName() {
		return "float";
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
		
		ret.append("Float "+varName+" = null;\n");
		return ret;
	}

	/*
	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException {
	    StringBuilder build = new StringBuilder();
		ExpressionAtom atom = expr.getAtom(0);
		
	    if (atom instanceof VarReferenceExpressionAtom) {
	        VarReferenceExpressionAtom var = (VarReferenceExpressionAtom)atom;
            build.append(ExpressionUtil.evaluateVarRefExpressionAtom(var, "float", execCtx));
	    } else if (atom instanceof ConstantReferenceExpressionAtom) {
	        ConstantReferenceExpressionAtom con = (ConstantReferenceExpressionAtom)atom;
	        if (con.getText().equals("null")) {
	        	build.append("null");
	        } else {
	        	build.append("new Float(").append(con.getText()).append(')');
	        }
	    } else if (atom instanceof CodeFragmentExpressionAtom) {
	    	CodeFragmentExpressionAtom a = (CodeFragmentExpressionAtom)atom;
	    	build.append(a.getText());
	    } else {
	    	throw new JavascribeException("Java integer type encountered an expression atom it couldn't process");
	    }

		return build.toString();
	}
	*/

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

	/*
	public boolean isValidIntegerAtom(String atom) {
		try {
			Integer.parseInt(atom);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	*/

}

