package net.sf.javascribe.patterns.js.page;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@Processor
public class PageModelProcessor {

	private static final Logger log = Logger.getLogger(PageModelProcessor.class);

	@ProcessorMethod(componentClass=PageModel.class)
	public void process(PageModel model,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		if ((model.getPageName()==null) || (model.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found a page model with no pageName specified.");
		}
		
		log.info("Processing model for page '"+model.getPageName()+"'");

		StringBuilder initCode = PageUtils.getInitFunction(ctx, model.getPageName());
		String pageName = model.getPageName();
		
		PageType pageType = PageUtils.getPageType(ctx, model.getPageName());
		PageUtils.ensureModel(ctx,pageType);
		PageModelType modelType = PageUtils.getModelType(ctx, model.getPageName());
		
		for(Attribute a : model.getAttribute()) {
			String name = a.getName();
			String typeName = ctx.getAttributeType(name);
			if (typeName==null) typeName = "object";
			addModelAttribute(modelType, name, typeName, initCode, a.getOnChange(), pageName);
		}
	}

	public static void addModelAttribute(PageModelType modelType,String name,String typeName,StringBuilder code,String onChange,String pageName) throws JavascribeException {
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Found a model attribute with no name");
		}
		if (modelType.getAttributeType(name)!=null) {
			// Attribute is already there.  No work to do.
			return;
		}
		
		modelType.addAttribute(name, typeName);
		String attr = "" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
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

