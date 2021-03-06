package net.sf.javascribe.patterns.view.impl;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class LocalModelDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-local-model";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getDomAttributes().get("ref");
		String attribs = ctx.getDomAttributes().get("attribs");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		if ((attribs==null) || (attribs.trim().length()==0)) {
			throw new JavascribeException("Directive js-local-model requires a 'attribs' attribute");
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

		List<Attribute> attrs = JavascribeUtils.readAttributes(ctx.getProcessorContext(), attribs);
		boolean hasDispatcher = false;
		if (ctx.getExecCtx().getVariableType(DirectiveUtils.EVENT_DISPATCHER_VAR)!=null) {
			hasDispatcher = true;
		}
		String var = DirectiveUtils.LOCAL_MODEL_VAR;
		StringBuilder code = ctx.getCode();
		code.append("var "+var+" = {};\n");
		if (ref!=null) {
			code.append("var "+ref+" = "+var+";\n");
		}
		LocalModelType modelType = new LocalModelType(ctx.getFunction().getName());
		ctx.getProcessorContext().getTypes().addType(modelType);
		execCtx.addVariable(DirectiveUtils.LOCAL_MODEL_VAR, modelType.getName());
		if (ref!=null)
			execCtx.addVariable(ref, modelType.getName());
		for(Attribute att : attrs) {
			String upper = JavascribeUtils.getUpperCamelName(att.getName());
			code.append(var+"._"+att.getName()+" = undefined;\n");
			code.append(var+".get"+upper+" = function() { return this._"+att.getName()+";}.bind("+var+");\n");
			code.append(var+".set"+upper+" = function(value) {\n");
			code.append("if (value==this._"+att.getName()+") return;\n");
			code.append("this._"+att.getName()+" = value;\n");
			if (hasDispatcher) {
				code.append(DirectiveUtils.EVENT_DISPATCHER_VAR+".dispatch('"+att.getName()
						+"Changed');\n");
			}
			code.append("}.bind("+var+");\n");
			modelType.addAttribute(att.getName(), att.getType());
		}
	}

}

