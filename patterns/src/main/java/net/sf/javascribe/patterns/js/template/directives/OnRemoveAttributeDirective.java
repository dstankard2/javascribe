package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.parsing.JavascriptParser;
import net.sf.javascribe.patterns.js.parsing.JavascriptParsingResult;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;

@Plugin
public class OnRemoveAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-onremove";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String code = ctx.getTemplateAttribute("js-onremove");

		ctx.continueRenderElement();
		StringBuilder fn = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		JavascriptParser eval = new JavascriptParser(code, newCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptParsingResult result = eval.evalCodeBlock();
		fn.append(ctx.getElementVarName()+".$$remove.push(function() {\n");
		fn.append(result.getCode());
		fn.append("\n});\n");
	}

}
