package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;

@Plugin
public class VarAttributeDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 3; }
	
	@Override
	public String getAttributeName() {
		return "js-var";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String name = ctx.getTemplateAttribute("js-var");

		if (name.trim().length()==0) {
			throw new JavascribeException("Found invalid js-var attribute value '"+name+"'");
		}
		if (ctx.getExecCtx().getVariableType(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' as there is already another one in the current code execution context");
		}
		b.append("var "+name+" = "+ctx.getElementVarName()+";\n");
		ctx.getExecCtx().addVariable(name, ctx.getExecCtx().getVariableType(ctx.getElementVarName()));
		ctx.continueRenderElement(ctx.getExecCtx());
	}

}

