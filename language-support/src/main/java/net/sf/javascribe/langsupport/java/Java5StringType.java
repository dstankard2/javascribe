package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.CodeFragmentExpressionAtom;
import net.sf.javascribe.api.expressions.ConstantReferenceExpressionAtom;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;
import net.sf.javascribe.api.expressions.VarReferenceExpressionAtom;
import net.sf.javascribe.api.types.StringType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5StringType extends JavaVariableTypeBase implements Java5Type,StringType {

	public Java5StringType() {
		super("string",null,"String");
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
		
		ret.append("String "+varName+" = null;\n");
		
		return ret;
	}

	@Override
	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException {
	    StringBuffer buf = new StringBuffer();
	    
	    if (expr.getSize()==0) {
	    	buf.append(instantiate(null,null,execCtx).getCodeText());
	    } else {
	    	for(int i=0;i<expr.getSize();i++) {
	    		if (i>0) buf.append('+');
	    		if (expr.getAtomType(i)==ValueExpression.VAR_REF_EXPR_ATOM) {
	    			VarReferenceExpressionAtom var = null;
	    			var = expr.getVarReferenceExpressionEntry(i);
	    			buf.append(ExpressionUtil.evaluateVarRefExpressionAtom(var, "string", execCtx));
	    		} else if (expr.getAtomType(i)==ValueExpression.CONST_REF_EXPR_ATOM) {
	    			ConstantReferenceExpressionAtom con = null;
	    			con = expr.getConstantReferenceExpressionEntry(i);
	    			if (con.getText().equals("null")) {
	    				buf.append("null");
	    			} else {
	    				buf.append("\""+con.getText()+"\"");
	    			}
	    		} else if (expr.getAtomType(i)==ValueExpression.CODE_FRAGMENT_EXPR_ATOM) {
	    			CodeFragmentExpressionAtom code = expr.getCodeFragmentExpressionEntry(i);
	    			buf.append(code.getText());
	    		} else {
	    			throw new IllegalArgumentException("Illegal expression atom found while evaluating string expression");
	    		}
	    	}
	    }

	    return buf.toString();
	}

	@Override
	public JavaCode instantiate(String name, String value,CodeExecutionContext execCtx) {
		return new JsomJavaCode(instantiate(name,value));
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) {
		return new JsomJavaCode(declare(name));
	}


}
