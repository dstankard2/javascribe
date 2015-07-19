package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;

@Scannable
public class RefDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-ref";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String ref = ctx.getTemplateAttributes().get("js-ref").trim();
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
		if (ctx.getExecCtx().getTypeForVariable(ref)!=null) {
			throw new JavascribeException("js-ref cannot create a reference named '"+ref+"' because it already exists.");
		}
		code.append("var "+ref+" = "+ctx.getElementVarName()+";\n");
		ctx.getExecCtx().addVariable(ref, "DomElement");
	}

}
