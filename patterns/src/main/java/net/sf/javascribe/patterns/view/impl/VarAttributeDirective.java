package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

@Scannable
public class VarAttributeDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		// TODO Auto-generated method stub
		return "js-var";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String name = ctx.getTemplateAttributes().get("js-var");

		if (name.trim().length()==0) {
			throw new JavascribeException("Found invalid js-var attribute value '"+name+"'");
		}
		if (ctx.getExecCtx().getVariableType(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' as there is already another one in the current code execution context");
		}
		ctx.continueRenderElement();
		
		b.append("var "+name+" = "+ctx.getElementVarName()+";\n");
		ctx.getExecCtx().addVariable(name, ctx.getExecCtx().getVariableType(ctx.getElementVarName()));
	}

}