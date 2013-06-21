package net.sf.javascribe.patterns.js.page;

import java.util.HashMap;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;

@Scannable
@Processor
public class PageModelProcessor {

	@ProcessorMethod(componentClass=PageModel.class)
	public void process(PageModel model,GeneratorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		if ((model.getPageName()==null) || (model.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found a page model with no pageName specified.");
		}
		
		System.out.println("Processing model for page '"+model.getPageName()+"'");
		
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);

		StringBuilder code = src.getSource();
		String pageName = model.getPageName();
		
		JavascriptVariableType pageType = PageUtils.getPageType(ctx, model.getPageName());
		PageUtils.ensureModel(ctx,pageType);
		JavascriptVariableType modelType = PageUtils.getModelType(ctx, model.getPageName());
		HashMap<String,String> modelAttributes = PageUtils.getModelAttributes(ctx, model.getPageName());
		
		code.append(pageName+".model = { };\n");
		for(Attribute a : model.getAttribute()) {
			String name = a.getName();
			String typeName = ctx.getAttributeType(name);
			if (typeName==null) typeName = "var";
			addModelAttribute(modelType, modelAttributes, name, typeName, code, a.getOnChange(), pageName);
/*
			modelType.addVariableAttribute(name, "var");
			String onChange = a.getOnChange();
			if ((a.getName()==null) || (a.getName().trim().length()==0)) {
				throw new JavascribeException("Found a model attribute with no name");
			}
			if (modelAttributes.get(name)!=null) {
				throw new JavascribeException("Cannot have the same attribute twice in a model");
			}
			String attr = "" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			if (!first) code.append(",");
			code.append(name+" : null\n");
			code.append(",get"+attr+" : function get"+attr+"() { return this."+name+"; }\n");
			code.append(",set"+attr+" : function set"+attr+"(val) {\nvar oldValue = this."+name+";\nif (oldValue==val) return;\nthis."+name+" = val;\n");
			if ((onChange!=null) && (onChange.trim().length()>0)) {
				String vals[] = onChange.split(",");
				for(String v : vals) {
					code.append(pageName+".controller.dispatch(\""+v+"\",{ oldValue : oldValue, value : this."+name+" });\n");
				}
			}
			code.append(pageName+".controller.dispatch(\""+name+"Changed\",{ oldValue : oldValue, value : this."+name+"});\n");
			code.append("}\n");
			if (ctx.getAttributeType(name)==null) {
				throw new JavascribeException("Couldn't find type for attribute '"+name+"'");
			}
			modelAttributes.put(name, ctx.getAttributeType(name));

			first = false;
*/
		}

//		code.append("};\n");
	}

	public static void addModelAttribute(JavascriptVariableType modelType,HashMap<String,String> modelAttributes,String name,String typeName,StringBuilder code,String onChange,String pageName) throws JavascribeException {
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Found a model attribute with no name");
		}
		if (modelType.getAttributeType(name)!=null) {
			// Attribute is already there.  No work to do.
			return;
//			throw new JavascribeException("Cannot have the same attribute twice in a model");
		}
		
		modelType.addVariableAttribute(name, "var");
		modelAttributes.put(name, "var");
		String attr = "" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		code.append(pageName).append(".model.").append(name).append(" = null;\n")
				.append(pageName).append(".model.get").append(attr)
				.append(" = function() {return this.").append(name).append(";}.bind("+pageName+".model);\n");

		code.append(pageName).append(".model.set"+attr+" = function set"+attr+"(val) {\nvar oldValue = this."+name+";\nif (oldValue==val) return;\nthis."+name+" = val;\n");
		if ((onChange!=null) && (onChange.trim().length()>0)) {
			String vals[] = onChange.split(",");
			for(String v : vals) {
				code.append(pageName+".controller.dispatch(\""+v+"\",{ oldValue : oldValue, value : this."+name+" });\n");
			}
		}
		code.append(pageName+".controller.dispatch(\""+name+"Changed\",{ oldValue : oldValue, value : this."+name+"});\n");
		code.append("}.bind("+pageName+".model);\n");
	}
	
}

