package net.sf.javascribe.patterns.js.page;

import java.util.HashMap;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;

public class PageUtils {

	public static PageType getPageType(ProcessorContext ctx,String pageName) throws JavascribeException {
		VariableType t = ctx.getType(pageName);
		PageType ret = null;
		
		if (t!=null) {
			if (!(t instanceof PageType)) {
				throw new JavascribeException("Type '"+pageName+"' is not a page type");
			}
			ret = (PageType)t;
		}

		return ret;
	}
	
	public static void ensureModel(ProcessorContext ctx,PageType pageType) throws JavascribeException {
		if (pageType.getAttributeType("model")==null) {
			PageModelType type = new PageModelType(pageType.getName());
			ctx.getTypes().addType(type);
			pageType.addAttribute("model", type.getName());
			JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
			src.getSource().append(pageType.getName()+".model = { };\n");
		}
	}
	
	public static PageModelType getModelType(ProcessorContext ctx,String pageName) throws JavascribeException {
		PageModelType ret = null;
		PageType pageType = getPageType(ctx, pageName);
		
		if (pageType==null) {
			throw new JavascribeException("Found no page called '"+pageName+"'");
		}
		ret = (PageModelType)ctx.getType(pageName+"Model");
		
		return ret;
	}
	
	public static StringBuilder getInitFunction(ProcessorContext ctx,String pageName) throws JavascribeException {
		StringBuilder ret = null;
		String objectName = "initFunc_"+pageName;

		ret = (StringBuilder)ctx.getObject(objectName);
		if (ret==null) {
			ret = new StringBuilder();
			ctx.putObject(objectName, ret);
			ret.append(pageName+".init = function() {\n");
		}
		
		return ret;
	}
	
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

}

