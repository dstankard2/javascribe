package net.sf.javascribe.patterns.view;

import java.util.Arrays;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;
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
	public static String getModelSetterCode(String modelRef,String value,DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		int i = modelRef.lastIndexOf('.');
		PageModelType modelType = getPageModelType(ctx);

		if (i>0) {
			String attr = modelRef.substring(0, i);
			String v = modelRef.substring(i+1);
			String ref = ExpressionUtil.evaluateValueExpression("${"+PAGE_VAR+".model."+attr+"}", "object", ctx.getExecCtx());
			b.append(ref+"."+v+" = "+value+";\n");
			b.append(PAGE_VAR+".controller.dispatch('"+attr+"Changed');\n");
		} else {
			b.append(modelType.getCodeToSetAttribute(PAGE_VAR+".model", modelRef, value, ctx.getExecCtx()));
		}
		
		return b.toString();
	}
	
	// Attempts to invoke the function on the objRef.  If the model type is 
	// not null, will look for parameters in the model first.
	// If execCtx is null, then execCtc i not checked for parameters.
	// Returns null if not all parameters are found
	public static String attemptInvoke(String resultVar,String objRef,JavascriptFunction fn,PageModelType modelType,CodeExecutionContext execCtx) throws JavascribeException {
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
				return ExpressionUtil.evaluateValueExpression("${"+ref+"}", "object", execCtx);
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
	
	private static final List<String> recognizedBooleanAtoms = Arrays.asList(new String[] {
			"!==", "!=", "!",
			"===", "==",
			"&&", "||", ">=", "<=", ">", "<", "(", ")", "null"
	});

	public static String evaluateIf(String cond,CodeExecutionContext execCtx) throws JavascribeException {
		try {
			return internalEvaluateIf(cond,execCtx);
		} catch(Exception e) {
			throw new JavascribeException("Couldn't evaluate conditional expression '"+cond+"'");
		}
	}
	
	protected static String internalEvaluateIf(String cond,CodeExecutionContext execCtx) throws JavascribeException {
		cond = cond.trim();
		if (cond.length()==0) return "";
		String a = findRecognizedAtom(cond);
		if (a!=null) {
			String rem = cond.substring(a.length());
			return a+internalEvaluateIf(rem,execCtx);
		}
		a = findFullReference(cond);
		if (a!=null) {
			String ref = DirectiveUtils.getValidReference(a, execCtx);
			return ref+internalEvaluateIf(cond.substring(a.length()),execCtx);
		}
		a = findNumber(cond);
		if (a!=null) {
			return a+internalEvaluateIf(cond.substring(a.length()),execCtx);
		}
		a = findString(cond);
		throw new JavascribeException("Couldn't evaluate boolean condition");
	}
	
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

	private static String findFullReference(String cond) {
		StringBuilder b = new StringBuilder();
		for(int i=0;i<cond.length();i++) {
			char c = cond.charAt(i);
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

	private static String findRecognizedAtom(String cond) {
		for(String s : recognizedBooleanAtoms) {
			if (cond.startsWith(s)) return s;
		}
		return null;
	}
	
}

