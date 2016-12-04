package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.view.impl.JavascriptEvalResult;
import net.sf.javascribe.patterns.view.impl.JavascriptEvaluator;

public class DirectiveUtils {

	public static final String PAGE_VAR = "_page";

	public static final String EVENT_DISPATCHER_VAR = "_dis";

	public static final String LOCAL_MODEL_VAR = "_model";
	
	public static final String DOCUMENT_REF = "_d";
	
	public static final String TEMPLATE_ROOT_ELEMENT_REF = "_r";

	public static List<AttributeDirective> getAttributeDirectives(ProcessorContext ctx) throws JavascribeException {
		List<AttributeDirective> ret = new ArrayList<AttributeDirective>();
		String objName = "net.sf.javascribe.view.templating.AttributeDirectives";
		
		List<AttributeDirectiveBase> systemDirectives = (List<AttributeDirectiveBase>)ctx.getObject(objName);
		if (systemDirectives==null) {
			systemDirectives = new ArrayList<AttributeDirectiveBase>();
			ctx.putObject(objName, systemDirectives);
			List<Class<?>> cls = ctx.getEngineProperties().getScannedClassesOfInterface(AttributeDirective.class);
			for(Class<?> cl : cls) {
				AttributeDirectiveBase dir;
				try {
					dir = (AttributeDirectiveBase)cl.newInstance();
					systemDirectives.add(dir);
				} catch(Exception e) {
					throw new JavascribeException("Couldn't find attribute directives",e);
				}
			}
			Collections.sort(systemDirectives);
		}
		for(AttributeDirective d : systemDirectives) {
			ret.add(d);
		}
		return ret;
	}
	public static Map<String,ElementDirective> getElementDirectives(ProcessorContext ctx) throws JavascribeException {
		Map<String,ElementDirective> ret = new HashMap<String,ElementDirective>();
		String objName = "net.sf.javascribe.view.templating.ElementDirectives";
		
		ret = (Map<String,ElementDirective>)ctx.getObject(objName);
		if (ret==null) {
			ret = new HashMap<String,ElementDirective>();
			ctx.putObject(objName, ret);
			List<Class<?>> cls = ctx.getEngineProperties().getScannedClassesOfInterface(ElementDirective.class);
			for(Class<?> cl : cls) {
				ElementDirective dir;
				try {
					dir = (ElementDirective)cl.newInstance();
					ret.put(dir.getElementName(), dir);
				} catch(Exception e) {
					throw new JavascribeException("Couldn't load attribute directives",e);
				}
			}
		}
		
		return ret;
	}

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
			b.append(PAGE_VAR+".event('"+changeEvent+"');\n");
		} else {
			b.append(PAGE_VAR+".event('"+getEventForModelRef(modelRef)+"');\n");
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
			JavascriptEvaluator eval = new JavascriptEvaluator(add,execCtx);
			populateImpliedVariables(eval);
			JavascriptEvalResult result = eval.evalExpression();
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

	public static void populateImpliedVariables(JavascriptEvaluator eval) {
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
	
	protected static String replace(String str,String replace,String replacement) {
		int i = str.indexOf(replace);
		while(i>=0) {
			str = str.substring(0, i) + replacement + str.substring(i+replace.length());
			i = str.indexOf(replace);
		}
		return str;
	}
	
	public static String unescapeXml(String str) {
		str = replace(str,"&quot;","\"");
		str = replace(str,"&lt;","<");
		str = replace(str,"&gt;",">");
		str = replace(str,"&amp;","&");
		str = replace(str,"&apos;","'");
		return str;
	}

	private static final String REM_CODE = 
	"if (!window._rem) {\n"+
	"/* Invoke remove functions on the element.  $$remove is an array of functions */\n"+
	"window._invokeRem = function(elt) {\n"+
	"if ((elt.$$remove) && (elt.$$remove.length)) {\n"+
	"for(var i=0;i<elt.$$remove.length;i++) {\n"+
	"elt.$$remove[i]();\n"+
	"}\n"+
	"delete elt.$$remove;\n"+
	"}\n"+
	"};\n"+
	"/* If only parent is passed, remove all its children recursively and call $remove on the parent */\n"+
	"/* If parent is defined, remove toRemove and remove all its children */\n"+
	"window._rem = function(parent,toRemove){\n"+
	"if (!toRemove) {\n"+
	"for(var i=0;i<parent.childNodes.length;i++) {\n"+
	"var node = parent.childNodes[i];\n"+
	"window._rem(node);\n"+
	"};\n"+
	"window._invokeRem(parent);\n"+
	"} else {\n"+
	"for(var _i=0;_i<parent.childNodes.length;_i++) {\n"+
	"if (parent.childNodes[_i]._elt==toRemove) {\n"+
	"var node = parent.childNodes[_i];\n"+
	"window._rem(node);\n"+
	"parent.removeChild(node);_i--;\n"+
	"}\n"+
	"}\n"+
	"}\n"+
	"};\n"+
	"/* Insert an element in its due place under parent, depending on prev */\n"+
	"window._ins = function(parent,elt,prev) {\n"+
	"for(var i=0;i<parent.childNodes.length;i++) {\n"+
	"var done = true;\n"+
	"var n = parent.childNodes[i];\n"+
	"for(var i2=0;i2<prev.length;i2++) {\n"+
	"if (n._elt==prev[i2]) {\n"+
	"done = false;\n"+
	"break;\n"+
	"}\n"+
	"}\n"+
	"if (done) {\n"+
	"parent.insertBefore(elt,n);\n"+
	"return;\n"+
	"}\n"+
	"}\n"+
	"parent.appendChild(elt);\n"+
	"};\n"+
	"}\n";

	/*
	private static final String REM_FUNC = 
			"if (!window._rem) {\n" + 
			"window._rem = function(parent,toRemove){\n" +
			"if (!parent)return;\n" +
			"for(var _i=0;_i<parent.childNodes.length;_i++){" +
			"if (parent.childNodes[_i]._elt==toRemove){" +
			"parent.removeChild(parent.childNodes[_i]);_i--;\n" +
			"}\n" +
			"}\n" +
			"}\n" +
			"}\n";
	private static final String INS_FUNC = 
			"if (!window._ins) {\n" +
			"window._ins = function(parent,elt,prev) {\n" +
			"for(var i=0;i<parent.childNodes.length;i++) {\n" +
			"var done = true;\n" +
			"var n = parent.childNodes[i];\n" +
			"for(var i2=0;i2<prev.length;i2++) {\n" +
			"if (n._elt==prev[i2]) {\n"+
			"done = false;\nbreak;\n" +
			"}\n" +
			"}\n" +
			"if (done) {\n" +
			"parent.insertBefore(elt,n);\n" +
			"return;\n"+
			"}\n" +
			"}\n" +
			"parent.appendChild(elt);\n"+
			"};\n" +
			"}\n";
	*/
	
	public static void ensureJavascriptTemplatingUtilities(ProcessorContext ctx) throws JavascribeException {
		JavascriptSourceFile ret = JavascriptUtils.getSourceFile(ctx);
		String propName = "net.sf.javascribe.patterns.view.DirectiveUtils_Fn_"+ret.getPath();
		
		Object val = ctx.getObject(propName);
		if (val==null) {
			ctx.putObject(propName, Boolean.TRUE);
			//ret.getSource().append(INS_FUNC);
			ret.getSource().append(REM_CODE);
		}
	}

	/*
	public static JavascriptSourceFile getJavascriptFileWithTemplatingUtilities(ProcessorContext ctx) throws JavascribeException {
		JavascriptSourceFile ret = JavascriptUtils.getSourceFile(ctx);
		String propName = "net.sf.javascribe.patterns.view.DirectiveUtils_Fn_"+ret.getPath();
		
		Object val = ctx.getObject(propName);
		if (val==null) {
			ctx.putObject(propName, Boolean.TRUE);
			ret.getSource().append(INS_FUNC);
			ret.getSource().append(REM_FUNC);
		}
		
		return ret;
	}
	*/
	
	public static String newVarName(String baseName,String type,CodeExecutionContext execCtx) {
		for(int i=0;i<100;i++) {
			if (execCtx.getVariableType(baseName+i)==null) {
				execCtx.addVariable(baseName+i, type);
				return baseName+i;
			}
		}
		return null;
	}

}

