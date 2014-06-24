package net.sf.javascribe.langsupport.java;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;

import org.apache.log4j.Logger;

/**
 * This utility class implements a number of methods that are useful for 
 * generating Java code and building Java-based variable types.
 * @author DCS
 */
public class JavaUtils {

	private static final String JAVA_ROOT_PKG = "net.sf.javascribe.langsupport.java.rootPkg";
	private static final String JAVA_ROOT_DIR = "net.sf.javascribe.langsupport.java.rootDir";

	private static final Logger log = Logger.getLogger(JavaUtils.class);
	
	public static void append(JavaCode code,JavaCode append) throws JavascribeException {
		code.appendCodeText(append.getCodeText());
		for(String im : append.getImports()) {
			if (!code.getImports().contains(im)) {
				code.addImport(im);
			}
		}
	}

	/**
	 * Adds the Java source file to the generated code distribution, taking into 
	 * account its package, class name and the root dir of the generated Java files.
	 * @param file
	 * @param ctx
	 * @throws JavascribeException
	 */
	public static void addJavaFile(JavaSourceFile file,ProcessorContext ctx) throws JavascribeException {
		file.setSourceRootPath(ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT_DIR));
		ctx.addSourceFile(file);
	}

	/**
	 * Gets the specified Java file, taking into account the package, class 
	 * name and root directory for generated Java files.
	 * @param className
	 * @param ctx
	 * @return
	 * @throws JavascribeException
	 */
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
	
	/**
	 * 
	 * @param ctx
	 * @param className
	 * @return
	 * @throws JavascribeException
	 */
	public static String getJavaFilePath(ProcessorContext ctx,String className) throws JavascribeException {
		String ret = ctx.getBuildRoot()+File.separatorChar+ctx.getRequiredProperty(JAVA_ROOT_DIR)+File.separatorChar+className;
		ret = ret.replace('.', File.separatorChar)+".java";
		return ret;
	}
	
	/**
	 * Returns the code to invoke the Java operation, using explicitly 
	 * defined parameters first and then the execution context.
	 * @param resultName Name of variable to put the result into, or null for operations that return void.
	 * @param objName
	 * @param op
	 * @param execCtx
	 * @param explicitParams
	 * @return
	 * @throws JavascribeException
	 */
	public static String callJavaOperation(String resultName,String objName,JavaOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams) throws JavascribeException {
		return callJavaOperation(resultName,objName,op,execCtx,explicitParams,true);
	}

	/**
	 * Returns the code to invoke the Java operation, using explicitly 
	 * defined parameters first and then the execution context.  Additionally, 
	 * you may invoke the operation without appending a semi-colon to the 
	 * end.
	 * @param resultName
	 * @param objName
	 * @param op
	 * @param execCtx
	 * @param explicitParams
	 * @param addSemicolon
	 * @return
	 * @throws JavascribeException
	 */
	public static String callJavaOperation(String resultName,String objName,JavaOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams,boolean addSemicolon) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		
		log.debug("Building Java operation call for "+objName+"."+op.getName());
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
		}
		if (addSemicolon) {
			build.append(';');
		}
		build.append('\n');

		return build.toString();
	}

}

