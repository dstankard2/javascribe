package net.sf.javascribe.langsupport.java;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
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
	public static JavaCode callJavaOperation(String resultName,String objName,JavaOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams) throws JavascribeException {
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
	public static JavaCode callJavaOperation(String resultName,String objName,JavaOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams,boolean addSemicolon) throws JavascribeException {
		JavaCode ret = new JavaCodeImpl();
		JavaCode decl = new JavaCodeImpl();
		JavaCode invoke = new JavaCodeImpl();
		
		log.debug("Building Java operation call for "+objName+"."+op.getName());
		invoke.appendCodeText(objName+'.');
		if (explicitParams==null) {
			explicitParams = new HashMap<String,String>();
		}

		invoke.appendCodeText(op.getName()+"(");
		List<String> paramNames = op.getParameterNames();
		boolean first = true;
		for(String p : paramNames) {
			if (first) first = false;
			else invoke.appendCodeText(",");
			if (explicitParams.get(p)!=null) {
				invoke.appendCodeText(explicitParams.get(p));
			} else if (execCtx.getTypeForVariable(p)!=null) {
				invoke.appendCodeText(p);
			} else {
				// Check attribute holders
				List<String> vars = execCtx.getVariableNames();
				// Check variables
				boolean found = false;
				for(String v : vars) {
					VariableType type = execCtx.getTypeForVariable(v);
					if (type instanceof AttributeHolder) {
						AttributeHolder holder = (AttributeHolder)type;
						if (holder.getAttributeType(p)!=null) {
							log.debug("Found parameter "+p+" in attribute holder "+v);
							String paramTypeName = holder.getAttributeType(p);
							JavaVariableType paramType = (JavaVariableType)execCtx.getType(paramTypeName);
							if (execCtx.getTypeForVariable(p)==null) {
								JavaUtils.append(decl, (JavaCode)paramType.declare(p, execCtx));
								execCtx.addVariable(p, paramTypeName);
							}
							decl.appendCodeText("if ("+v+"!=null) {\n");
							decl.appendCodeText(p+" = ");
							decl.appendCodeText(holder.getCodeToRetrieveAttribute(v, p, paramTypeName, execCtx));
							decl.appendCodeText(";}\n");
							invoke.appendCodeText(p);
							found = true;
							break;
						}
					}
				}
				if (!found) {
					throw new JavascribeException("Couldn't find parameter '"+p+"' in current execution context");
				}
				
			}

					/*
			//JavaCode p = findParameter(p,execCtx,explicitParams);
			ValueExpression expr = JavascribeUtils.findParameterValue(p,op.getParameterTypes().get(p),execCtx,explicitParams);
			if (expr==null) {
				throw new JavascribeException("Error calling operation "+objName+"."+op.getName()+": Could not find parameter '"+p+"'");
			}
			build.append(ExpressionUtil.getEvaluatedExpression(expr, execCtx));
			*/
		}
		invoke.appendCodeText(")");
		if ((resultName!=null) && (resultName.trim().length()>0)) {
			ValueExpression expr = ExpressionUtil.buildExpressionFromCode(invoke.getCodeText(),op.getReturnType());
			invoke = new JavaCodeImpl();
			invoke.appendCodeText(ExpressionUtil.evaluateSetExpression(resultName, expr, execCtx));
			//build.append(ExpressionUtil.evaluateSetExpression(resultName, expr, execCtx));
		}
		if (addSemicolon) {
			invoke.appendCodeText(";");
		}
		invoke.appendCodeText("\n");
		JavaUtils.append(decl, invoke);
		JavaUtils.append(ret, decl);

		return ret;
	}
	
}

