package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class ClickDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ATTRIBUTE };
	}

	@Override
	public String getName() {
		return "js-click";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String click = ctx.getAttributes().get("js-click");
		
		ctx.getAttributes().remove("js-click");
		ctx.continueRenderElement(ctx.getExecCtx());
		String var = ctx.getElementVarName();

		b.append(var+".onclick = function() {\n");
		b.append(click+"(this);\n");
		b.append("}\n");
	}

}

