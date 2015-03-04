package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class OptionDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "option";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String eltVar = ctx.newVarName("_o","option", ctx.getExecCtx());
		StringBuilder code = ctx.getCode();
		
		String val = ctx.getDomAttributes().get("value");
		ctx.getDomAttributes().remove("value");
		code.append("var "+eltVar+" = _d.createElement('option');\n");
		code.append(eltVar+".value = '"+val+"';\n");
		code.append(eltVar+".innerHTML = '"+ctx.getInnerHtml()+"';\n");
		code.append(ctx.getContainerVarName()+".appendChild("+eltVar+");\n");
	}

}
