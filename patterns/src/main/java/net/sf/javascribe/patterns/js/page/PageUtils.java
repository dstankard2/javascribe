package net.sf.javascribe.patterns.js.page;

import java.util.HashMap;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.patterns.xml.page.Element;

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
			StringBuilder init = getInitFunction(ctx, pageType.getName());
			PageModelType type = new PageModelType(pageType.getName());
			ctx.getTypes().addType(type);
			pageType.addAttribute("model", type.getName());
			init.append("this.model = { };\n");
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

	public static void addModelAttribute(PageModelType modelType,String name,String typeName,String onChange,String pageName, ProcessorContext ctx) throws JavascribeException {
		StringBuilder code = PageUtils.getInitFunction(ctx, pageName);
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Found a model attribute with no name");
		}
		if (modelType.getAttributeType(name)!=null) {
			// Attribute is already there.  No work to do.
			return;
		}
		
		modelType.addAttribute(name, typeName);
		String attr = JavascribeUtils.getUpperCamelName(name);
		//String attr = "" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		code.append("this.model.").append(name).append(" = undefined;\n")
				.append("this.model.get").append(attr)
				.append(" = function() {return this.").append(name).append(";}.bind("+pageName+".model);\n");

		code.append("this.model.set"+attr+" = function(val) {\n");
		if ((typeName.equals("integer")) || (typeName.equals("longint"))) {
			code.append("if (isNaN(val)) val = undefined;\nelse val = Number(val);\n");
		}
		code.append("var oldValue = this."+name+";\nif (oldValue===val) return;\nthis."+name+" = val;\n");
		if ((onChange!=null) && (onChange.trim().length()>0)) {
			String vals[] = onChange.split(",");
			for(String v : vals) {
				code.append(pageName+".event(\""+v+"\");\n");
			}
		}
		code.append(pageName+".event(\""+name+"Changed\");\n");
		code.append("}.bind("+pageName+".model);\n");
	}
	
}

