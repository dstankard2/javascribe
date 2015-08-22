package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;

@Scannable
public class DisabledDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-disabled";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String value = ctx.getTemplateAttributes().get("js-disabled");
		
		ctx.getTemplateAttributes().remove("js-disabled");
		ctx.continueRenderElement();
		
		JavascriptEvaluator eval = new JavascriptEvaluator(value,ctx.getExecCtx());
		eval.parseExpression();
		if (eval.getError()!=null) {
			throw new JavascribeException(eval.getError());
		}
		String cond = eval.getResult();
		code.append("try {\n");
		code.append("if ("+cond+") "+ctx.getElementVarName()+".disabled = true;\n");
		code.append("else "+ctx.getElementVarName()+".disabled = false;\n");
		code.append("}catch(_err){");
		code.append(ctx.getElementVarName()+".disabled = false;\n");
		code.append("}\n");
	}

}