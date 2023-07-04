package net.sf.javascribe.patterns.js.template.directives;

import java.util.List;

import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;
import net.sf.javascribe.patterns.js.template.parsing.ElementDirective;


@Plugin
public class LocalModelElementDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-local-model";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getDomAttribute("ref");
		String properties = ctx.getDomAttribute("properties");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		if ((properties==null) || (properties.trim().length()==0)) {
			throw new JavascribeException("Directive js-local-model requires a 'properties' attribute");
		}
		if (DirectiveUtils.getPageName(ctx)!=null) {
			throw new JavascribeException("You can't use a local model on a page template or page-aware template");
		}
		if (execCtx.getVariableType(DirectiveUtils.LOCAL_MODEL_VAR)!=null) {
			throw new JavascribeException("This template already has a local model");
		}
		if (ref!=null) ref = ref.trim();
		if (ref!=null) {
			if (execCtx.getVariableType(ref.trim())!=null) {
				throw new JavascribeException("This template already has a variable with name '"+ref+"'");
			}
		}

		List<PropertyEntry> attrs = JavascribeUtils.readParametersAsList(properties, ctx.getProcessorContext());
		boolean hasDispatcher = false;
		if (ctx.getExecCtx().getVariableType(DirectiveUtils.EVENT_DISPATCHER_FN_VAR)!=null) {
			hasDispatcher = true;
		}
		LocalModelType modelType = new LocalModelType(ctx.getFunction().getName(),ctx.getProcessorContext());
		ctx.getProcessorContext().addVariableType(modelType);
		String var = DirectiveUtils.LOCAL_MODEL_VAR;
		String modelFn = ctx.newVarName("_m", modelType.getName(), execCtx);
		StringBuilder code = ctx.getCode();
		StringBuilder objCode = new StringBuilder();
		code.append("function "+modelFn+"() {\n");

		objCode.append("var _obj = {\n");

		boolean first = true;
		for(PropertyEntry att : attrs) {
			String name = att.getName();
			if (first) first = false;
			else objCode.append(',');
			objCode.append("set "+name+"("+name+") {\n");
			objCode.append("if ("+name+"===_"+name+") return;\n");
			objCode.append("_"+name+" = "+name+";\n");
			if (hasDispatcher) {
				objCode.append(DirectiveUtils.EVENT_DISPATCHER_FN_VAR+"('"+name+"Changed');\n");
			}
			objCode.append("}\n");
			objCode.append(",get "+name+"() { return _"+name+"; }\n");
			code.append("var _"+name+";\n");
		}

		objCode.append("};\n");
		code.append(objCode.toString());
		code.append("return _obj;\n}\n");
		code.append("var "+var+" = "+modelFn+"();\n");
		execCtx.addVariable(DirectiveUtils.LOCAL_MODEL_VAR, modelType.getName());
		if (ref!=null) {
			code.append("var "+ref+" = "+var+";\n");
			execCtx.addVariable(ref, modelType.getName());
		}

		/*
		for(AttribEntry att : attrs) {
			String upper = JavascribeUtils.getUpperCamelName(att.getName());
			code.append(var+"._"+att.getName()+" = undefined;\n");
			code.append(var+".get"+upper+" = function() { return this._"+att.getName()+";}.bind("+var+");\n");
			code.append(var+".set"+upper+" = function(value) {\n");
			code.append("if (value==this._"+att.getName()+") return;\n");
			code.append("this._"+att.getName()+" = value;\n");
			if (hasDispatcher) {
				code.append(DirectiveUtils.EVENT_DISPATCHER_FN_VAR+".dispatch('"+att.getName()
						+"Changed');\n");
			}
			code.append("}.bind("+var+");\n");
			modelType.addAttribute(att.getName(), att.getType());
		}
		*/
	}

}

