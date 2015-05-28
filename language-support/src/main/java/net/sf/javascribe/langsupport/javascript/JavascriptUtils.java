package net.sf.javascribe.langsupport.javascript;

import java.io.File;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.java.JavaBeanType;

import org.apache.log4j.Logger;

public class JavascriptUtils {

	public static final String JAVASCRIPT_FILE = "net.sf.javascribe.langsupport.javascript.file";
	
	private static final Logger log = Logger.getLogger(JavascriptUtils.class);

	public static JavascriptSourceFile getSourceFile(ProcessorContext ctx) throws JavascribeException {
		JavascriptSourceFile ret = null;
		
		String jsFilePath = ctx.getBuildRoot() + File.separatorChar + ctx.getRequiredProperty(JAVASCRIPT_FILE);
		ret = (JavascriptSourceFile)ctx.getSourceFile(jsFilePath);
		if (ret==null) {
			ret = new JavascriptSourceFile(false);
			ret.setPath(jsFilePath);
			ctx.addSourceFile(ret);
		}
		
		return ret;
	}
	
	public static boolean isMin(ProcessorContext ctx) {
		boolean ret = false;
		String prop = ctx.getProperty("javascript.min");

		if ((prop!=null) && (prop.toLowerCase().equals("true"))) {
			ret = true;
		}
		
		return ret;
	}
	
	public String getListType(String typeName) {
		String ret = null;
		
		if (!typeName.startsWith("list/")) return null;
		ret = typeName.substring(5);
		
		return ret;
	}
	
	public static boolean isArray(String typeName) {
		if (typeName.indexOf("list")==0) return true;
		return false;
	}
	
	public static boolean isDataObject(String typeName) {
		if (isVar(typeName)) return false;
		if (typeName.indexOf("list")==0) return false;
		return true;
	}
	
	// Returns true if the type name is an atomic type or a list (JS array).
	// typeName is the name of a type as it would be defined by Java component processor
	public static boolean isVar(String typeName) {
		boolean ret = false;

		if (typeName.indexOf("list")==0) ret = false;
		else if (typeName.equals("string")) ret = true;
		else if (typeName.equals("integer")) ret = true;
		else if (typeName.equals("longint")) ret = true;
		else if (typeName.equals("date")) ret = true;
		else if (typeName.equals("timestamp")) ret = true;
		else if (typeName.equals("boolean")) ret = true;
		else if (typeName.equals("var")) ret = true;
		else if (typeName.equals("object")) ret = true;

		return ret;
	}
	
	public static JavascriptCode invokeFunction(String resultVar,String objName,JavascriptFunctionType fn,CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		
		if (resultVar!=null) {
			ret.append(resultVar+" = ");
		}
		if (objName!=null) {
			ret.append(objName+'.');
		}
		ret.append(fn.getName()+'(');
		boolean first = true;
		for(String p : fn.getParamNames()) {
			if (!first) ret.append(",");
			else first = false;
			if (execCtx.getVariableNames().contains(p)) {
				ret.append(p);
				continue;
			}
			for(String v : execCtx.getVariableNames()) {
				JavascriptBaseObjectType t = (JavascriptBaseObjectType)execCtx.getTypeForVariable(v);
				String s = findAttributeInDataObject(p, v, t);
				if (s!=null) {
					ret.append(s);
					continue;
				}
			}
			log.warn("Could not find parameter '"+p+"' to invoke a Javascript function.");
			return null;
		}
		ret.append(");\n");

		return ret;
	}
	
	protected static String findAttributeInDataObject(String attrib,String objVar,JavascriptBaseObjectType type) throws JavascribeException {
		String ret = null;
		if (type.getAttributeType(attrib)!=null) {
			return type.getCodeToRetrieveAttribute(objVar, attrib, "object", null);
		}
		return ret;
	}
	
	public static JavascriptObjectType makeDataObject(JavaBeanType javaType) {
		JavascriptObjectType ret = null;
		
		ret = new JavascriptObjectType(javaType.getName());
		for(String name : javaType.getAttributeNames()) {
			ret.addAttribute(name, javaType.getAttributeType(name));
		}
		return ret;
	}
	
}

