package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirectiveBase;
import net.sf.javascribe.patterns.view.DirectiveContext;

@Scannable
public class ClickDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-click";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String click = ctx.getTemplateAttributes().get("js-click");
		
		ctx.continueRenderElement(ctx.getExecCtx());
		String var = ctx.getElementVarName();

		b.append(var+".onclick = function(event) {\n");
		b.append("event.stopPropagation();\n");
		b.append(click+"(this);\n");
		b.append("}\n");
	}

}

