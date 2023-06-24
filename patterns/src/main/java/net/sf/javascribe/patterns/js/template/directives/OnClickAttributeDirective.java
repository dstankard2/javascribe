package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;

@Plugin
public class OnClickAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-onclick";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String click = ctx.getTemplateAttribute("js-onclick");
		
		ctx.continueRenderElement(ctx.getExecCtx());
		String var = ctx.getElementVarName();

		b.append(var+".addEventListener('click', function($event) {\n");
		b.append("$event.stopPropagation();\n");
		b.append(click);
		if (click==null) {
			b.append("/* no-op */");
		}
		else if (!click.endsWith(";")) {
			b.append(";");
		}
		b.append("\n});\n");
	}

}

