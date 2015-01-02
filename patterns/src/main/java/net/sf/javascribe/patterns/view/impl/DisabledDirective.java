package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class DisabledDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ATTRIBUTE };
	}

	@Override
	public String getName() {
		return "js-disabled";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String value = ctx.getAttributes().get("js-disabled");
		
		ctx.getAttributes().remove("js-disabled");
		ctx.continueRenderElement();
		
		String cond = DirectiveUtils.evaluateIf(value, ctx.getExecCtx());
		code.append("try {\n");
		code.append("if ("+cond+") "+ctx.getElementVarName()+".disabled = true;\n");
		code.append("else "+ctx.getElementVarName()+".disabled = false;\n");
		code.append("}catch(_err){ }\n");
	}

}
