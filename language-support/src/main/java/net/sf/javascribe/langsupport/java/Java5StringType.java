package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

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

public class Java5StringType implements JavaVariableType,Java5Type,StringType {

	@Override
	public String getClassName() {
		return "String";
	}

	@Override
	public String getImport() {
		return null;
	}

	@Override
	public String getName() {
		return "string";
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

	public List<String> getAttributeNames() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(STRING_LENGTH);
		ret.add(TRIMMED_VALUE);
		return ret;
	}

	@Override
	public String getAttributeType(String attrib) {
		if (attrib.equals(STRING_LENGTH)) return "integer";
		else if (attrib.equals(TRIMMED_VALUE)) return "string";
		return null;
	}

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		if (attribName.equals(STRING_LENGTH)) {
			return varName+".length()";
		}
		else if (attribName.equals(TRIMMED_VALUE)) {
			return varName+".trim()";
		}
		return null;
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("You cannot set the attributes of a string");
	}

	@Override
	public JavaCode instantiate(String name, String value,CodeExecutionContext execCtx) {
		return new JsomJavaCode(instantiate(name,value));
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {

		return new JsomJavaCode(declare(name));
	}


}
