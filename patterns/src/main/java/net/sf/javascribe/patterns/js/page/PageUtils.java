package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.types.ExportedModuleType;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;

public class PageUtils {

	public static PageInfo getPageInfo(String pageName, ProcessorContext ctx) {
		PageInfo ret = null;
		
		ret = (PageInfo)ctx.getObject("javascribe.patterns.page."+pageName);
		
		return ret;
	}
	
	public static void addPageInfo(PageInfo pageInfo, ProcessorContext ctx) throws JavascribeException {
		String pageName = pageInfo.getName();
		ctx.setObject("javascribe.patterns.page."+pageName, pageInfo);
		PageBuilderComponent comp = new PageBuilderComponent(pageInfo);
		ctx.addComponent(comp);
		
		// Create model type for this page
		PageModelType modelType = new PageModelType(pageName,false,ctx);
		ctx.addVariableType(modelType);
		ModuleType pageType = new ModuleType(pageName,JavascriptUtils.getModulePath(ctx), ExportedModuleType.CONSTRUCTOR);
		//JavascriptServiceType pageType = new JavascriptServiceType(pageName);
		ctx.addVariableType(pageType);
		pageInfo.setModelTypeName(modelType.getName());
		pageInfo.setPageTypeName(pageType.getName());

		// Add attributes to page type
		pageType.addAttribute("model", modelType.getName());
		ServiceOperation event = new ServiceOperation("event");
		event.addParam("eventName", "string");
		event.addParam("callback", "function");
		pageType.addOperation(event);
	}
	
	public static String getPageModelTypeName(String pageName) {
		return "PageModel_"+pageName;
	}

	public static PageModelType getPageModelType(String pageName,ProcessorContext ctx) throws JavascribeException {
		String name = getPageModelTypeName(pageName);
		return JavascribeUtils.getType(PageModelType.class, name, ctx);
	}

	public static void addModelAttribute(String pageName,String attrib,String type,ProcessorContext ctx) throws JavascribeException {
		PageModelType t = getPageModelType(pageName,ctx);
		t.addAttribute(attrib, type);
	}
	
}

