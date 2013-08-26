package net.sf.javascribe.langsupport.javascript;

import java.io.File;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;

public class JavascriptUtils {

	public static final String JAVASCRIPT_FILE = "net.sf.javascribe.langsupport.javascript.file";

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
	
	public static void addJavaScriptTypes(ProcessorContext ctx) throws JavascribeException {
		if (ctx.getTypes().getType("var")==null) {
			ctx.getTypes().addType(new VarVariableType());
		}
	}

}

