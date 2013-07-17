package net.sf.javascribe.patterns.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;

public class RuleResolver {
	private String searchType = null;
	private HashMap<String,String> dependencyRefs = new HashMap<String,String>();
	private int level = 1;
	private CodeExecutionContext execCtx = null;
	private ProcessorContext ctx = null;
	
	public static RuleResolver getRuleResolver(String searchType,CodeExecutionContext execCtx,HashMap<String,String> dependencyRefs,ProcessorContext ctx) {
		RuleResolver ret = new RuleResolver();
		
		ret.dependencyRefs = dependencyRefs;
		ret.execCtx = execCtx;
		ret.searchType = searchType;
		ret.ctx = ctx;
		
		return ret;
	}
	
	protected static RuleResolver getNestedRuleResolver(String searchType,CodeExecutionContext execCtx,HashMap<String,String> dependencyRefs,int level,ProcessorContext ctx) {
		RuleResolver ret = getRuleResolver(searchType,execCtx,dependencyRefs,ctx);
		ret.level = level+1;
		
		return ret;
	}
	
	public Java5CodeSnippet resolve() throws JavascribeException,CodeGenerationException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		if (this.level==5) {
			return null;
		}

		Java5CodeSnippet prepend = resolve(searchType);
		if (prepend==null) return null;
		prepend.merge(ret);
		ret = prepend;

		return ret;
	}

	// At this point, we should break the dependency check to look for rules that return single 
	// element, full list.
	protected Java5CodeSnippet resolve(String searchType) throws JavascribeException,CodeGenerationException {
		Java5CodeSnippet ret = null;
		
		for(String dependencyName : dependencyRefs.keySet()) {
			ret = checkDependency(dependencyName,searchType,dependencyRefs.get(dependencyName));
			if (ret!=null) return ret;
		}
		
		// If a list type, resolve for the element type.  If found, then wrap a for loop around
		// the resolved rule.
		/*
		if (searchType.indexOf("list/")==0) {
			String atomType = searchType.substring(5);
			Java5CodeSnippet code = resolve(atomType);
			if (code!=null) {
				// Found something that returns the atom type.
				System.out.println("hi");
			}
		}
		*/
		
		return null;
	}
	
	protected Java5CodeSnippet checkDependency(String typeName,String searchType,String instance) throws CodeGenerationException,JavascribeException {
		Java5CodeSnippet ret = null;
		JavaServiceObjectType type = (JavaServiceObjectType)execCtx.getType(typeName);
		List<String> methods = type.getOperationNames();
		
		for(String m : methods) {
			JavaOperation op = type.getMethod(m);
			ret = checkMethod(instance,searchType,op);
			if (ret!=null) return ret;
		}
		
		return null;
	}

	protected Java5CodeSnippet checkMethod(String objectRef,String searchType,JavaOperation op) throws JavascribeException,CodeGenerationException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		if (op.getReturnType()==null) return null;
		
		if (!op.getReturnType().equals(searchType)) {
			return null;
		}
		
		// This operation returns the correct type.  Invoke it.
		// For each of the operation's parameters, if it is not in the current execCtx 
		// then resolve for it.
		
		List<String> params = op.getParameterNames();
		Map<String,String> paramTypes = op.getParameterTypes();
		
		String newVarName = JavascribeUtils.getLowerCamelName(searchType);
		JavaVariableType returnType = (JavaVariableType)execCtx.getType(searchType);
		
		Java5CodeSnippet methodCallBuild = new Java5CodeSnippet();
		JsomUtils.merge(methodCallBuild, (JavaCode)returnType.declare(newVarName, execCtx));
		methodCallBuild.append(newVarName+" = ");
		methodCallBuild.append(objectRef);
		methodCallBuild.append('.');
		methodCallBuild.append(op.getName());
		methodCallBuild.append('(');
		
		// Check to see if params are in current execCtx.  Resolve for them if not.
		boolean first = true;
		for(String s : params) {
			if (first) first = false;
			else methodCallBuild.append(',');
			String ref = findVariableRef(s,execCtx,paramTypes.get(s));
			if (ref==null) {
				// Resolve for this parameter (only if it is an attribute holder?)
				RuleResolver res = RuleResolver.getNestedRuleResolver(paramTypes.get(s), execCtx, dependencyRefs, level, ctx);
				Java5CodeSnippet c = res.resolve();
				if (c==null) return null;
				this.execCtx = res.execCtx;
				c.merge(ret);
				ret = c;
				methodCallBuild.append(s);
			} else {
				// Found it!
				methodCallBuild.append(ref);
			}
		}
		methodCallBuild.append(");\n");
		ret.merge(methodCallBuild);
		this.execCtx.addVariable(newVarName, searchType);

		return ret;
	}
	
	// Returns the string that accesses the given param in the given execCtx.  Null if not found.
	// TODO: This maybe should check attributes of attribute holders in the execCtx.
	protected String findVariableRef(String param,CodeExecutionContext execCtx,String type) throws JavascribeException {

		String t = execCtx.getVariableType(param);
		if (t==null) return null;
		if (!t.equals(type)) {
			throw new JavascribeException("System Attribute Fault - Found multiple types for a variable called '"+param+"'");
		}
		
		return param;
	}

}

