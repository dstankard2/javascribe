package net.sf.javascribe.langsupport.javascript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.modules.ModuleFunction;
import net.sf.javascribe.langsupport.javascript.modules.ModuleSourceFile;

public class JavascriptUtils {

	public static String getModuleRelativePath(String path,String modulePath) {
		if (!path.startsWith("/")) path = "/" + path;
		if (!modulePath.startsWith("/")) modulePath = "/" + modulePath;
		StringBuilder build = new StringBuilder();
		
		build.append(".");
		int i = modulePath.lastIndexOf('/');
		String moduleFileName = null;
		String moduleFileDir = null;
		String pathDir = null;
		
		moduleFileName = modulePath.substring(i+1);
		if (i>0) {
			moduleFileDir = modulePath.substring(1,i);
			pathDir = path.substring(1, path.lastIndexOf('/'));
		} else {
			moduleFileDir = "/";
			pathDir = "";
		}
		
		// TODO: Implement real solution
		if (moduleFileDir.startsWith(pathDir+'/')) {
			build.append('/').append(moduleFileDir.substring(pathDir.length()+1));
		} else if (moduleFileDir.equals(pathDir)) {
			// no-op
			build.append("");
		} else {
			int i2 = pathDir.indexOf('/');
			while(i2>=0) {
				build.append("/..");
				i2 = pathDir.indexOf('/',i2+1);
				//if (i2>=0) build.append('/');
			}
			//build.append("..");
			build.append("/../"+moduleFileDir);
		}
		
		build.append("/").append(moduleFileName);
		
		return build.toString();
	}
	
	public static String getModulePath(String srcPath,ProcessorContext ctx) throws JavascribeException {
		String ret = null;
		String base = ctx.getProperty("javascript.modules.srcRoot");
		
		if (base==null) {
			throw new JavascribeException("Javascript modules require config property 'javascript.modules.srcRoot'");
		}
		if (srcPath.startsWith(base)) {
			ret = srcPath.substring(base.length());
		} else {
			throw new JavascribeException("Couldn't get web path for file "+srcPath+" based on www root folder "+base);
		}
		
		return ret;
	}
	
	public static String getModulePath(ProcessorContext ctx) throws JavascribeException {
		String srcPath = getModuleSourceFilePath(ctx);
		return getModulePath(srcPath,ctx);
	}
	
	protected static String getModuleSourceFilePath(ProcessorContext ctx) throws JavascribeException {
		String base = ctx.getBuildContext().getOutputRootPath("js");
		String path = ctx.getProperty("javascript.module.source");
		String fullPath = null;
		
		if (path==null) {
			throw new JavascribeException("Couldn't find required property 'javascript.module.source'");
		}
		if ((base.endsWith("/")) || (path.startsWith("/"))) {
			fullPath = base+path;
		} else {
			fullPath = base+'/'+path;
		}
		return fullPath;
	}

	public static ModuleSourceFile getModuleSource(ProcessorContext ctx) throws JavascribeException {
		ModuleSourceFile ret = null;

		String fullPath = getModuleSourceFilePath(ctx);

		ctx.getLog().debug("Found Javascript module sourcefile as '"+fullPath+"'");
		ret = (ModuleSourceFile)ctx.getSourceFile(fullPath);
		if (ret==null) {
			ret = new ModuleSourceFile(getModulePath(ctx));
			ret.setPath(fullPath);
			ctx.addSourceFile(ret);
			ctx.getLog().debug("Creating module source file.");
		}

		return ret;
	}
	
	public static JavascriptCode callJavascriptOperation(String resultName,String objName,ServiceOperation op,CodeExecutionContext execCtx,Map<String,String> explicitParams,boolean addSemicolon, boolean allParamsRequired) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode();
		JavascriptCode invoke = new JavascriptCode();

		invoke.appendCodeText(objName+'.');
		if (explicitParams==null) {
			explicitParams = new HashMap<String,String>();
		}

		invoke.appendCodeText(op.getName()+"(");
		List<String> paramNames = op.getParamNames();
		boolean first = true;
		for(String p : paramNames) {
			if (first) first = false;
			else invoke.appendCodeText(",");
			if (explicitParams.get(p)!=null) {
				invoke.appendCodeText(explicitParams.get(p));
			} else if (execCtx.getTypeForVariable(p)!=null) {
				invoke.appendCodeText(p);
			} else {
				// Parameter was not found
				if (allParamsRequired) {
					throw new JavascribeException("Couldn't find parameter '"+p+"' in current code execution context");
				} else {
					invoke.appendCodeText("undefined");
				}
			}

		}
		invoke.appendCodeText(")");
		if ((resultName!=null) && (resultName.trim().length()>0)) {
			//JavascriptCode fullInvoke = new JavascriptCode();
			//fullInvoke.appendCodeText(s);
			//invoke = JavaUtils.set(resultName, invoke.getCodeText(), execCtx);
			invoke.appendCodeText(";");
			ret.appendCodeText(resultName + " = ");
		} else if (addSemicolon) {
			invoke.appendCodeText(";");
		}
		invoke.appendCodeText("\n");
		ret.append(invoke);
		
		return ret;
	}

	public static boolean isJavascriptDebug(ProcessorContext ctx) {
		boolean ret = false;
		String val = ctx.getProperty("javascript.debug");
		if ((val.equalsIgnoreCase("true")) || (val.equalsIgnoreCase("T")) || (val.equalsIgnoreCase("Y"))) {
			ret = true;
		}
		return ret;
	}

	// If localName is true, a _ is prepended to the name
	public static String fnSource(ModuleFunction fn, String namePrepend) {
		StringBuilder b = new StringBuilder();
		String name = fn.getName();

		b.append("function ");
		if (namePrepend!=null) b.append(namePrepend);
		b.append(name+"(");
		boolean firstParam = true;
		for(String p : fn.getParamNames()) {
			if (firstParam) firstParam = false;
			else b.append(',');
			b.append(p);
		}
		b.append(") {\n");
		// TODO: This shouldn't be necessary, right?
		if (fn.getCode()!=null) {
			b.append(fn.getCode().getCodeText());
		}
		b.append("\n}\n");

		return b.toString();
	}

}

