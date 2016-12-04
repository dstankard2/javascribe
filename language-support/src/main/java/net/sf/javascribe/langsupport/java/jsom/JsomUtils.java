package net.sf.javascribe.langsupport.java.jsom;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5ClassDefinition;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5MethodSignature;
import net.sf.jsom.java5.Java5SourceFile;
import net.sf.jsom.java5.Java5Type;

public class JsomUtils {

	public static void addJavaFile(Java5SourceFile file,ProcessorContext ctx) throws JavascribeException {
		JavaUtils.addJavaFile(new JsomJavaSourceFile(file), ctx);
	}
	
	public static Java5SourceFile getJavaFile(String className,ProcessorContext ctx) throws JavascribeException {
		JsomJavaSourceFile file = null;
		
		file = (JsomJavaSourceFile)JavaUtils.getJavaFile(className, ctx);
		if (file==null) return null;
		return file.getSrc();
	}

	public static JavaOperation createJavaOperation(Java5MethodSignature method) {
		JavaOperation ret = new JavaOperation();
		
		ret.setName(method.getName());
		ret.setReturnType(method.getType());
		List<String> args = method.getArgNames();
		
		for(String s : args) {
			ret.addParameter(s, method.getArg(s).getType());
		}
		
		return ret;
	}
	
	public static Java5DeclaredMethod createMethod(ProcessorContext ctx) {
		return new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
	}
	
	public static Java5ClassConstructor createConstructor(Java5SourceFile src,ProcessorContext ctx) {
		Java5ClassConstructor ret = new Java5ClassConstructor(new JavascribeVariableTypeResolver(ctx.getTypes()),src.getPublicClass().getClassName());
		return ret;
	}
	
	public static Java5SourceFile createJavaSourceFile(ProcessorContext ctx) {
		Java5SourceFile file = null;
		JavascribeVariableTypeResolver res = new JavascribeVariableTypeResolver(ctx);

		file = new Java5SourceFile(res);
		
		return file;
	}
	
	public static Java5CodeSnippet declareAndInstantiateObject(JavaServiceObjectType type,String name,CodeExecutionContext execCtx) throws CodeGenerationException,JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		if ((execCtx!=null) && (execCtx.getTypeForVariable(name)!=null)) {
			throw new CodeGenerationException("JSOMUtil cannot declare and instantiate an object that is already in the current execution context");
		}
		JavaCode code = type.declare(name);
		ret.merge(JsomUtils.toJsomCode(code));
		if (execCtx!=null) execCtx.addVariable(name, type.getName());
		if (type instanceof LocatedJavaServiceObjectType) {
			LocatedJavaServiceObjectType loc = (LocatedJavaServiceObjectType)type;
			code = loc.getInstance(name, execCtx);
			ret.merge(toJsomCode(code));
		} else {
			code = type.instantiate(name, null);
			ret.merge(toJsomCode(code));
		}
		if (execCtx!=null)
			execCtx.addVariable(name, type.getName());
		
		return ret;
	}
	
	/**
	 * Converts the given Javascribe code to a JSOM-compatible code snippet.
	 * @param code
	 * @return
	 * @throws JavascribeException
	 */
	public static Java5CodeSnippet toJsomCode(JavaCode code) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		for(String s : code.getImports()) {
			ret.addImport(s);
		}
		ret.append(code.getCodeText());
		
		return ret;
	}
	
	/**
	 * Returns the code required to declare the specified variable of 
	 * the specified type.  Adds that variable to the code execution 
	 * context.
	 * @param execCtx
	 * @param variable
	 * @param type
	 * @return
	 * @throws CodeGenerationException
	 */
	public static Java5CompatibleCodeSnippet declare(CodeExecutionContext execCtx,String variable,String type) throws CodeGenerationException {
		Java5CompatibleCodeSnippet ret = null;
		
		Java5Type t = (Java5Type)execCtx.getType(type);
		if (t==null) {
			throw new CodeGenerationException("Couldn't find Java type '"+type+"'");
		}
		if (t instanceof ListType){
			String eltType = type.substring(5);
			ListType listType = (ListType)t;
			try {
				ret = (Java5CompatibleCodeSnippet)listType.declare(variable, eltType, execCtx);
			} catch(JavascribeException e){
				throw new CodeGenerationException("Couldn't instanceiate list: "+e.getMessage());
			}
		} else {
			ret = (Java5CompatibleCodeSnippet)t.declare(variable);
		}
		execCtx.addVariable(variable, type);
		
		return ret;
	}
	
	public static void merge(Java5CompatibleCodeSnippet code,JavaCode merge) {
		try {
			Java5CodeSnippet m = toJsomCode(merge);
			code.merge(m);
		} catch(CodeGenerationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the default constructor for the specified class.  Creates it if 
	 * the class does not have one.
	 * @param cl JSOM Class to create a default constructor for.
	 * @return
	 */
	public static Java5ClassConstructor getDefaultConstructor(Java5ClassDefinition cl) {
		Java5ClassConstructor ret = null;
		
		List<String> methodNames = cl.getMethodNames();
		for(String s : methodNames) {
			Java5MethodSignature method = cl.getDeclaredMethod(s);
			if (!(method instanceof Java5ClassConstructor)) continue;
			if (method.getArgNames().size()==0) {
				return (Java5ClassConstructor)method;
			}
		}
		
		if (ret==null) {
			ret = new Java5ClassConstructor(cl.getTypes(),cl.getClassName());
			cl.addMethod(ret);
		}
		
		return ret;
	}

}

