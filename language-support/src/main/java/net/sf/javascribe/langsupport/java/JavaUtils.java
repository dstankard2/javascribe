package net.sf.javascribe.langsupport.java;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;

public class JavaUtils {

	private static final String JAVA_ROOT = "net.sf.javascribe.langsupport.java.root";

	public static void setJavaPath(SourceFile src,GeneratorContext ctx,String pkg,String className) throws JavascribeException {
		String path = ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT);

		path = pkg+'.'+className;
		path = path.replace('.', File.separatorChar)+".java";
		path = ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT)+File.separatorChar+path;
		
		src.setPath(path);
	}

	public static void addJavaFile(JavaSourceFile file,GeneratorContext ctx) throws JavascribeException {
		file.setSourceRootPath(ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT));
		ctx.addSourceFile(file);
	}
	
	public static JavaSourceFile getJavaFile(String className,GeneratorContext ctx) throws JavascribeException {
		JavaSourceFile ret = null;
		
		String path = ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT)+File.separatorChar;
		path = path + className.replace('.', File.separatorChar)+".java";
		ret = (JavaSourceFile)ctx.getSourceFile(path);
		
		return ret;
	}
	
	public static String findJavaPath(GeneratorContext ctx,String pkg,String className) throws JavascribeException {
		return ctx.getRequiredProperty(JAVA_ROOT)+File.separatorChar+pkg.replace('.', File.separatorChar)+
				File.separatorChar+className+".java";
	}

	public static String findWebRootPath(GeneratorContext ctx) throws JavascribeException {
		return ctx.getRequiredProperty("web.root");
	}
	
	public static String findPackageName(GeneratorContext ctx,String subpkg) throws JavascribeException {
		return ctx.getRequiredProperty("net.sf.javascribe.langsupport.java.pkg")+'.'+subpkg;
	}
	
	public static String getJavaFilePath(GeneratorContext ctx,String className) throws JavascribeException {
		String ret = ctx.getRequiredProperty("build.root")+File.separatorChar+ctx.getRequiredProperty("java.root")+File.separatorChar+className;
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

