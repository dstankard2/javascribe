package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.parsing.JavascriptParser;
import net.sf.javascribe.patterns.js.parsing.JavascriptParsingResult;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;

@Plugin
public class DisabledAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-disabled";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String value = ctx.getTemplateAttribute("js-disabled");
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
		JavascriptParser eval = new JavascriptParser(value,ctx.getExecCtx());
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptParsingResult result = eval.evalExpression();
		String cond = result.getCode();
		code.append("try {\n");
		code.append("if ("+cond+") "+ctx.getElementVarName()+".disabled = true;\n");
		code.append("else "+ctx.getElementVarName()+".disabled = false;\n");
		code.append("}catch(_err){\n");
		code.append(ctx.getElementVarName()+".disabled = false;\n");
		code.append("}\n");
	}

}
