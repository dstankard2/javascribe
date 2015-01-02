package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class OptionDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ELEMENT };
	}

	@Override
	public String getName() {
		return "option";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String eltVar = ctx.newVarName("_o","option", ctx.getExecCtx());
		StringBuilder code = ctx.getCode();
		
		String val = ctx.getAttributes().get("value");
		code.append("var "+eltVar+" = _d.createElement('option');\n");
		code.append(eltVar+".value = '"+val+"';\n");
		code.append(eltVar+".innerHTML = '"+ctx.getInnerHtml()+"';\n");
		code.append(ctx.getContainerVarName()+".appendChild("+eltVar+");\n");
	}

}
