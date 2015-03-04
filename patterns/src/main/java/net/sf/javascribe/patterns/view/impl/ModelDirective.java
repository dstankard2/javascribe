package net.sf.javascribe.patterns.view.impl;

import java.util.HashMap;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class ModelDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-model";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String model = ctx.getTemplateAttributes().get("js-model");
		StringBuilder b = ctx.getCode();

		ctx.continueRenderElement(ctx.getExecCtx());
		String var = ctx.getElementVarName();

		String template = null;
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("eltVar", var);
		if (ctx.getElementName().equals("select")) {
			template = "renderer/js-model-select.txt";
			String val = "${_page.model."+model+"}";
			val = ExpressionUtil.evaluateValueExpression(val, "object", ctx.getExecCtx());
			params.put("getter", val);
			params.put("controllerEvent",DirectiveUtils.getEventForModelRef(model));
			params.put("setter",DirectiveUtils.getModelSetterCode(model, "val", ctx));
			String fnName = ctx.newVarName("_f", "function", ctx.getExecCtx());
			params.put("fn", fnName);
		} else if (ctx.getElementName().equals("input")) {
			template = "renderer/js-model-input.txt";
			if (ctx.getDomAttributes().get("type").equals("text")) {
				params.put("changeEvent", "onkeyup");
			} else {
				params.put("changeEvent", "onchange");
			}
			String val = "${_page.model."+model+"}";
			val = ExpressionUtil.evaluateValueExpression(val, "object", ctx.getExecCtx());
			params.put("getter", val);
			params.put("controllerEvent",DirectiveUtils.getEventForModelRef(model));
			params.put("setter",DirectiveUtils.getModelSetterCode(model, "val", ctx));
		} else if (ctx.getElementName().equals("textarea")) {
		}
		if (template!=null) {
			b.append(JavascribeUtils.basicTemplating(template, params, ctx.getProcessorContext()));
		}
	}

}
