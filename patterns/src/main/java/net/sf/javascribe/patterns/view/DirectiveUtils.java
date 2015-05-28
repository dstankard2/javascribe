package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;

public class DirectiveUtils {

	public static final String PAGE_VAR = "_page";

	public static final String DOCUMENT_REF = "_d";

	public static String getEventForModelRef(String modelRef) {
		String ret = modelRef+"Changed";
		int i = modelRef.indexOf('.');
		if (i>0) {
			ret = modelRef.substring(0, i)+"Changed";
		}
		
		return ret;
	}

	public static String getPageName(DirectiveContext ctx) {
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		if (execCtx.getVariableType(PAGE_VAR)!=null) {
			return execCtx.getVariableType(PAGE_VAR);
		}
		
		return null;
	}
	
	public static PageModelType getPageModelType(DirectiveContext ctx) throws JavascribeException {
		PageModelType ret = null;

		PageType pageType = (PageType)ctx.getExecCtx().getTypeForVariable(PAGE_VAR);
		if (pageType!=null) {
			String typeName = pageType.getAttributeType("model");
			ret = (PageModelType)ctx.getProcessorContext().getType(typeName);
		}

		return ret;
	}
	
	// Returns a string that calls a setter on the given model ref and passes the supplied value 
	public static String getModelSetterCode(String modelRef,String value,DirectiveContext ctx,String changeEvent) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		int i = modelRef.lastIndexOf('.');
		PageModelType modelType = getPageModelType(ctx);

		if (i>0) {
			String attr = modelRef.substring(0, i);
			String v = modelRef.substring(i+1);
			String ref = ExpressionUtil.evaluateValueExpression("${"+PAGE_VAR+".model."+attr+"}", "object", ctx.getExecCtx());
			b.append(ref+"."+v+" = "+value+";\n");
		} else {
			b.append(modelType.getCodeToSetAttribute(PAGE_VAR+".model", modelRef, value, ctx.getExecCtx()));
			b.append(";\n");
		}

		if (changeEvent!=null) {
			b.append(PAGE_VAR+".controller.dispatch('"+changeEvent+"');\n");
		} else {
			b.append(PAGE_VAR+".controller.dispatch('"+getEventForModelRef(modelRef)+"');\n");
		}
		return b.toString();
	}
	
	// Attempts to invoke the function on the objRef.  If the model type is 
	// not null, will look for parameters in the model first.
	// If execCtx is null, then execCtc is not checked for parameters.
	// Returns null if not all parameters are found
	public static String attemptInvoke(String resultVar,String objRef,JavascriptFunctionType fn,PageModelType modelType,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		
		if (resultVar!=null) {
			b.append(resultVar+" = ");
		}
		b.append(objRef+'.'+fn.getName()+"(");
		boolean first = true;
		for(String s : fn.getParamNames()) {
			if (first) first = false;
			else b.append(',');
			if (modelType!=null) {
				if (modelType.getAttributeType(s)!=null) {
					b.append(modelType.getCodeToRetrieveAttribute(PAGE_VAR+".model", s, "object", execCtx));
					continue;
				}
			}
			if (execCtx!=null) {
				if (execCtx.getVariableType(s)!=null) {
					b.append(s);
					continue;
				}
			}
			return null;
		}
		b.append(");\n");
		return b.toString();
	}
	
	// Returns a value expression for the variable.  Will first try to assume that 
	// the variable is a model attribute.  If that fails, it will try to find the 
	// variable in the code execution context.
	public static String getValidReference(String variable,CodeExecutionContext execCtx) {
		if (execCtx.getVariableType(PAGE_VAR)!=null) {
			String ref = PAGE_VAR+".model."+variable;
			try {
				String val = ExpressionUtil.evaluateValueExpression("${"+ref+"}", "object", execCtx);
				if (val!=null) return val;
			} catch(Exception e) {
			}
		}
		try {
			return ExpressionUtil.evaluateValueExpression("${"+variable+"}", "object", execCtx);
		} catch(Exception e) {
		}
		
		return null;
	}
	public static String getReferenceType(String ref,CodeExecutionContext execCtx) {
		if (execCtx.getVariableType(PAGE_VAR)!=null) {
			String ref2 = PAGE_VAR+".model."+ref;
			try {
				return ExpressionUtil.buildValueExpression("${"+ref2+"}", null, execCtx).getAtom(0).getAtomType();
			} catch(Exception e) {
			}
		}
		
		try {
			return ExpressionUtil.buildValueExpression("${"+ref+"}", null, execCtx).getAtom(0).getAtomType();
		} catch(Exception e) {
		}
		
		
		return null;
	}
	
	/** For evaluating boolean statements **/
	/*
	private static final List<String> atomsForBoolean = Arrays.asList(new String[] {
			"!==", "!=", "!",
			"===", "==",
			"&&", "||", ">=", "<=", ">", "<", "(", ")", "null"
	});
	*/
	
	/* Try to remove this.
	public static String evaluateIf(String cond,CodeExecutionContext execCtx) throws JavascribeException {
		try {
			return internalEvaluateIf(cond.trim(),cond.trim(),execCtx);
		} catch(Exception e) {
			throw new JavascribeException("Couldn't evaluate conditional expression '"+cond+"'");
		}
	}
	
	protected static String internalEvaluateIf(String originalCondition,String cond,CodeExecutionContext execCtx) throws JavascribeException {
		cond = cond.trim();
		if (cond.length()==0) return "";
		String a = findRecognizedAtom(cond);
		if (a!=null) {
			String rem = cond.substring(a.length());
			return a+internalEvaluateIf(originalCondition,rem,execCtx);
		}
		a = findFullReference(cond);
		if (a!=null) {
			String ref = DirectiveUtils.getValidReference(a, execCtx);
			return ref+internalEvaluateIf(originalCondition,cond.substring(a.length()),execCtx);
		}
		a = findNumber(cond);
		if (a!=null) {
			return a+internalEvaluateIf(originalCondition,cond.substring(a.length()),execCtx);
		}
		a = findString(cond);
		throw new JavascribeException("Couldn't evaluate boolean condition '"+originalCondition+"'");
	}
	*/

	/*
	private static String findString(String cond) {
		StringBuilder b = new StringBuilder();
		char starter = 0;
		
		if ((cond.startsWith("\"")) || (cond.startsWith("'"))) {
			starter = cond.charAt(0);
		} else return null;
		b.append('\'');
		for(int i=1;i<cond.length();i++) {
			if (cond.charAt(i)==starter) break;
			b.append(cond.charAt(i));
		}
		b.append('\'');
		
		return b.toString();
	}
	*/

	/*
	private static String findNumber(String cond) {
		StringBuilder b = new StringBuilder();
		for(int i=0;i<cond.length();i++) {
			if (Character.isDigit(cond.charAt(i))) {
				b.append(cond.charAt(i));
			} else break;
		}
		if (b.length()==0) return null;
		
		return b.toString();
	}
	*/

	/*
	private static String findFullReference(String cond) {
		StringBuilder b = new StringBuilder();
		
		char c = cond.charAt(0);
		if (!Character.isJavaIdentifierStart(c)) return null;
		b.append(c);
		for(int i=1;i<cond.length();i++) {
			c = cond.charAt(i);
			if ((!Character.isJavaIdentifierPart(c)) && 
					(c!='.')) break;
			b.append(c);
		}
		if (b.length()==0) return null;
		try {// Make sure it's not a number
			Integer.parseInt(b.toString());
			return null;
		} catch(Exception e) { }
		return b.toString();
	}
	*/

	/*
	private static String findRecognizedAtom(String cond) {
		for(String s : atomsForBoolean) {
			if (cond.startsWith(s)) return s;
		}
		return null;
	}
	*/
	
	// Returns a number (integer or decimal) if the expression starts with one
	/*
	private static String findNumberLiteral(String expr) {
		for(int i=0;i<expr.length();i++) {
			char c = expr.charAt(i);
			if ((Character.isDigit(c)) || (c=='.')) continue;
			if (i==0) return null;
			return expr.substring(0, i);
		}
		return null;
	}
	*/
	
	// For use when looking for an atom that might be a string expression
	// returns null if the expr doesn't start with a string ' "
	/*
	private static String findStringLiteral(String expr) {
		char start = expr.charAt(0);
		
		if ((start!='\'') && (start!='"')) return null;
		
		String ignore = "\\"+start;
		for(int i=1;i<expr.length();i++) {
			char c = expr.charAt(i);
			if (i<expr.length()-1) {
				if (expr.substring(i, i+2).equals(ignore)) {
					i++;
					continue;
				}
				if (c==start) {
					// This is the end of the string
					return expr.substring(0, i+1);
				}
			}
		}
		return null;
	}
	*/

	/*
	private static String findVariableReference(String expr,CodeExecutionContext execCtx) {
		String ref = null;

		if (!Character.isJavaIdentifierStart(expr.charAt(0))) return null;
		// It's a variable reference
		int i = 1;
		for(i=1;i<expr.length();i++) {
			if (Character.isJavaIdentifierPart(expr.charAt(i))) continue;
			if (expr.charAt(i)=='.') continue;
		}
		ref = expr.substring(0,i);
		try {
		if (ExpressionUtil.buildValueExpression(ref, null, execCtx)!=null) return ref;
		} catch(Exception e) { }
		String modelRef = PAGE_VAR+".model."+ref;
		try {
			if (ExpressionUtil.buildValueExpression(modelRef, null, execCtx)!=null) return ref;
		} catch(Exception e) { }

		return null;
	}
	*/
	
}

