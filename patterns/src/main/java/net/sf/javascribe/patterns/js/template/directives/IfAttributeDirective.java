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
public class IfAttributeDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 1; }
	
	@Override
	public String getAttributeName() {
		return "js-if";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String cond = ctx.getTemplateAttribute("js-if");
		CodeExecutionContext existingCtx = ctx.getExecCtx();

		if ((cond==null) || (cond.trim().length()==0)) {
			throw new JavascribeException("Directive js-if requires a condition");
		}
		JavascriptParser eval = new JavascriptParser(cond,existingCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptParsingResult result = eval.evalExpression();
		String finalCond = result.getCode();

		String boolVar = ctx.newVarName("_b", "boolean", existingCtx);
		b.append("var "+boolVar+" = false;\n");
		b.append("try {\n"+boolVar+" = ("+finalCond+");\n} catch(_err) { }\n");
		b.append("if ("+boolVar+") {\n");

		CodeExecutionContext newCtx = new CodeExecutionContext(existingCtx);
		ctx.continueRenderElement(newCtx);

		b.append("}\n");
		
	}
	
}

