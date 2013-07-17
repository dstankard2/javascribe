package net.sf.javascribe.langsupport.java;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;

public class JavaUtils {

	private static final String JAVA_ROOT_PKG = "net.sf.javascribe.langsupport.java.rootPkg";
	private static final String JAVA_ROOT_DIR = "net.sf.javascribe.langsupport.java.rootDir";

	public static void addJavaFile(JavaSourceFile file,ProcessorContext ctx) throws JavascribeException {
		file.setSourceRootPath(ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT_DIR));
		ctx.addSourceFile(file);
	}
	
	public static JavaSourceFile getJavaFile(String className,ProcessorContext ctx) throws JavascribeException {
		JavaSourceFile ret = null;
		
		String path = ctx.getBuildRoot()+File.separatorChar
				+ctx.getRequiredProperty(JAVA_ROOT_DIR)+File.separatorChar+className;
		path = path.replace('.',File.separatorChar)+".java";
		ret = (JavaSourceFile)ctx.getSourceFile(path);
		
		return ret;
	}
	
	public static String findPackageName(ProcessorContext ctx,String subpkg) throws JavascribeException {
		return ctx.getRequiredProperty(JAVA_ROOT_PKG)+'.'+subpkg;
	}
	
	public static String getJavaFilePath(ProcessorContext ctx,String className) throws JavascribeException {
		String ret = ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT_DIR)+File.separatorChar+className;
		ret = ret.replace('.', File.separatorChar)+".java";
		return ret;
	}
	
	public static String findTypeName(String attribute) {
		return Character.toUpperCase(attribute.charAt(0))+attribute.substring(1);
	}

	public static String findAttributeName(String type) {
		return Character.toLowerCase(type.charAt(0))+type.substring(1);
	}

	public static String callJavaOperation(String resultName,String objName,JavaOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		
		build.append(objName).append('.');
		if (explicitParams==null) {
			explicitParams = new HashMap<String,String>();
		}

		build.append(op.getName()+"(");
		List<String> paramNames = op.getParameterNames();
		boolean first = true;
		for(String p : paramNames) {
			if (first) first = false;
			else build.append(',');
			ValueExpression expr = JavascribeUtils.findParameterValue(p,op.getParameterTypes().get(p),execCtx,explicitParams);
			if (expr==null) {
				throw new JavascribeException("Error calling operation "+objName+"."+op.getName()+": Could not find parameter '"+p+"'");
			}
			build.append(ExpressionUtil.getEvaluatedExpression(expr, execCtx));
		}
		build.append(")");
		if ((resultName!=null) && (resultName.trim().length()>0)) {
			ValueExpression expr = ExpressionUtil.buildExpressionFromCode(build.toString(),op.getReturnType());
			build = new StringBuilder();
			build.append(ExpressionUtil.evaluateSetExpression(resultName, expr, execCtx));
			build.append(";\n");
		} else {
			build.append(";\n");
		}

		return build.toString();
	}
	
}

