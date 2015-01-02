package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

//This is not scannable because it will be added to the renderer list manually.
public class ShowRenderer implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ATTRIBUTE };
	}

	@Override
	public String getName() {
		return "js-show";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String cond = ctx.getAttributes().get("js-show");
		ctx.getAttributes().remove("js-show");

		ctx.continueRenderElement(ctx.getExecCtx());
		if (DirectiveUtils.getPageName(ctx)!=null) {
			String event = ctx.getAttributes().get("js-event");
			if (event==null) {
				throw new JavascribeException("Directive js-show requires attribute js-event when used on a page template");
			}
		} else {
			b.append("if (!"+cond+") {\n");
			b.append(ctx.getElementVarName()+".style.display = 'none';\n}\n");
		}
	}

}
