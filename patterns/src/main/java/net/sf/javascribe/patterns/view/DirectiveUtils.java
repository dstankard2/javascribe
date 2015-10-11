package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.view.impl.JaEval2;
import net.sf.javascribe.patterns.view.impl.JaEvalResult;

public class DirectiveUtils {

	public static final String PAGE_VAR = "_page";

	public static final String EVENT_DISPATCHER_VAR = "_dis";

	public static final String LOCAL_MODEL_VAR = "_model";
	
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
	
	public static String parsePartialExpression(String s,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		s = s.trim();
		int i = s.indexOf("{{");
		int previousEnd = 0;
		build.append('\'');

		while(i>=0) {
			String append = s.substring(previousEnd, i);
			int end = s.indexOf("}}", i+2);
			build.append(append.replace("'", "\\'"));
			String add = s.substring(i+2, end).trim();
			JaEval2 eval = new JaEval2(add,execCtx);
			populateImpliedVariables(eval);
			JaEvalResult result = eval.parseExpression();
			if (result.getErrorMessage()!=null) {
				throw new JavascribeException(result.getErrorMessage());
			}

			String ref = result.getResult().toString();
			build.append("'+((function(){try{return "+ref+"!=undefined?"+ref+":'';}catch(_e){return '';}})())+'");
			previousEnd = end + 2;
			i = s.indexOf("{{", previousEnd);
		}
		if (previousEnd < s.length()) {
			build.append(s.substring(previousEnd).replace("'","\\'"));
		}
		build.append('\'');
		
		return build.toString();
	}

	public static void populateImpliedVariables(JaEval2 eval) {
		eval.addImpliedVariable(DirectiveUtils.LOCAL_MODEL_VAR)
				.addImpliedVariable(DirectiveUtils.PAGE_VAR)
				.addImpliedVariable(DirectiveUtils.PAGE_VAR+".model");
	}

	// Converts a HTML identifier with - for word separator, into a lower camcel string.
	public static String getLowerCamelFromHtml(String html) {
		String ret = html;
		int index = ret.indexOf('-');
		while(index>0) {
			if (index>=ret.length()-1) throw new IllegalArgumentException("Tried to convert an invalid string '"+html+"' from HTML to lower camel case");
			char c = ret.charAt(index+1);
			c = Character.toUpperCase(c);
			ret = ret.substring(0, index)+c+ret.substring(index+2);
			index = ret.indexOf('-');
		}
		if (index==0) throw new IllegalArgumentException("Tried to convert an invalid string '"+html+"' from HTML to lower camel case");
		
		return ret;
	}
	
}

