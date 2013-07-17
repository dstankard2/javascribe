package net.sf.javascribe.patterns.js.page;

import java.util.HashMap;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.langsupport.javascript.JavascriptConstants;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;

public class PageUtils {

	public static JavascriptVariableType getPageType(ProcessorContext ctx,String pageName) throws JavascribeException {
		VariableType t = ctx.getType(JavascriptConstants.JS_TYPE+pageName);
		JavascriptVariableType ret = null;
		
		if (t!=null) {
			if (!(t instanceof JavascriptVariableType)) {
				throw new JavascribeException("Type '"+pageName+"' is not a Javascript type");
			}
			ret = (JavascriptVariableType)t;
		}

		return ret;
	}
	
	public static void ensureModel(ProcessorContext ctx,JavascriptVariableType pageType) throws JavascribeException {
		if (pageType.getAttributeType("model")==null) {
			String modelName = pageType.getName()+"_Model";
			JavascriptVariableType type = new JavascriptVariableType(modelName);
			ctx.getTypes().addType(type);
			pageType.addVariableAttribute("model", modelName);
		}
	}
	
	public static JavascriptVariableType getModelType(ProcessorContext ctx,String pageName) throws JavascribeException {
		JavascriptVariableType ret = null;
		JavascriptVariableType pageType = getPageType(ctx, pageName);
		
		if (pageType==null) {
			throw new JavascribeException("Found no page called '"+pageName+"'");
		}
		if (pageType.getAttributeType("model")==null) return null;
		ret = (JavascriptVariableType)ctx.getType(pageType.getAttributeType("model"));
		
		return ret;
	}
	
	public static StringBuilder getInitFunction(ProcessorContext ctx,String pageName) throws JavascribeException {
		StringBuilder ret = null;
//		String jsFile = ctx.getRequiredProperty("javascript.file");
		String objectName = "initFunc_"+pageName;

		ret = (StringBuilder)ctx.getObject(objectName);
		if (ret==null) {
			ret = new StringBuilder();
			ctx.putObject(objectName, ret);
			ret.append(pageName+".init = function() {\n");
		}
		
		return ret;
	}
	
	/*
	public static HashMap<String,ElementBinder> getElementBinders(GeneratorContext ctx) throws JavascribeException {
		HashMap<String,ElementBinder> ret = null;
		final String ELEMENT_BINDERS = "com.dave.components.js.page.ViewElements.elementBinders";
		
		ret = (HashMap<String,ElementBinder>)ctx.getObject(ELEMENT_BINDERS);
		if (ret==null) {
			String name = null;
			String className = null;
			
			ret = new HashMap<String,ElementBinder>();
			ctx.addObject(ELEMENT_BINDERS, ret);
			try {
				Enumeration<URL> resources = PageUtils.class.getClassLoader().getResources("META-INF/page-elements.txt");
				while(resources.hasMoreElements()) {
					URL url = resources.nextElement();
					DataInputStream din = new DataInputStream(url.openStream());
					String line = din.readLine();
					while (line!=null) {
						int index = line.indexOf("=");
						if (index>0) {
							name = line.substring(0,index);
							className = line.substring(index+1);
							Class<? extends ElementBinder> cl = (Class<? extends ElementBinder>)Class.forName(className);
							ret.put(name, cl.newInstance());
						}
						line = din.readLine();
					}
				}
			} catch(Exception e) {
				throw new JavascribeException("Couldn't load Page Element Binders",e);
			}
		}

		return ret;
	}
	*/
	
	public static HashMap<String,Element> getViewElements(ProcessorContext ctx,String pageName) {
		HashMap<String,Element> ret = null;
		final String VIEW_ELEMENTS = "com.dave.components.js.page.ViewElements";
		
		ret = (HashMap<String,Element>)ctx.getObject(VIEW_ELEMENTS+'.'+pageName);
		if (ret==null) {
			ret = new HashMap<String,Element>();
			ctx.putObject(VIEW_ELEMENTS+'.'+pageName, ret);
		}
		
		return ret;
	}

	public static HashMap<String,String> getModelAttributes(ProcessorContext ctx,String pageName) {
		HashMap<String,String> ret = null;
		final String MODEL_ATTRIBUTES = "com.dave.components.js.page.ModelAttributes";
		
		ret = (HashMap<String,String>)ctx.getObject(MODEL_ATTRIBUTES+"."+pageName);
		if (ret==null) {
			ret = new HashMap<String,String>();
			ctx.putObject(MODEL_ATTRIBUTES+"."+pageName, ret);
		}
		
		return ret;
	}

}

