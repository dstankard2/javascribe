package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class ViewElementDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-view-element";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String eltValue = ctx.getTemplateAttributes().get("js-view-element").trim();
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
		String ref = DirectiveUtils.PAGE_VAR+".view."+eltValue;
		code.append(ExpressionUtil.evaluateSetExpression(ref, ctx.getElementVarName(), ctx.getExecCtx())+";\n");
	}

}
