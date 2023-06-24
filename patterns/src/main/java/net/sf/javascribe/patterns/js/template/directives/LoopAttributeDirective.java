package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;

@Plugin
public class LoopAttributeDirective extends AttributeDirectiveBase {

	@Override
	public int getPriority() { return 2; }
	
	@Override
	public String getAttributeName() {
		return "js-loop";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		
		String a = ctx.getTemplateAttribute("js-loop");
		int i = a.indexOf(" in ");
		String eltVar = a.substring(0, i).trim();
		String list = a.substring(i+4).trim();
		
		String includeIf = ctx.getTemplateAttribute("js-loop-include-if");
		if ((includeIf!=null) && (includeIf.trim().length()==0)) {
			includeIf = null;
		}
		
		String indexVar = null;
		
		final String indexIndicator = " index ";
		if (list.indexOf(indexIndicator)>0) {
			int indicatorIndex = list.indexOf(indexIndicator);
			indexVar = list.substring(indicatorIndex+indexIndicator.length());
			list = list.substring(0, list.indexOf(indexIndicator));
			ctx.getProcessorContext().getLog().warn("Found indexVar as "+indexVar);
			if (execCtx.getVariableType(indexVar)!=null) {
				throw new JavascribeException("Tried to define index variable '"+indexVar+"' but it already exists in the code execution context");
			}
		}

		if (execCtx.getVariableType(eltVar)!=null) {
			throw new JavascribeException("Couldn't create loop variable '"+eltVar+"' as there is already a variable in the current execution context with that name");
		}
		String in = ctx.newVarName("_i","object",execCtx);
		
		String func = ctx.newVarName("_lf", "function", execCtx);
		execCtx.addVariable(func, "function");
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		String args = eltVar;
		if (indexVar!=null) {
			args = args + ',' + indexVar;
			newCtx.addVariable(indexVar, "integer");
		}
		b.append("var "+func+" = function("+args+"){\n");
		newCtx.addVariable(eltVar, "object");
		// TODO: Pass newCtx or existing one?  Should be new one...
		ctx.continueRenderElement(newCtx);
		b.append("}\n");
		b.append("try {\n");
		b.append("for(var "+in+"=0;"+in+"<"+list+".length;"+in+"++) {\n");
		args = eltVar;
		if (indexVar!=null) {
			args = args + ','+in;
		}
		b.append("var "+eltVar+" = "+list+"["+in+"];\n");
		if (includeIf!=null) {
			b.append("if ("+includeIf+") {\n");
		}
		b.append(func+"("+args+");\n}\n");
		if (includeIf!=null) {
			b.append("}\n");
		}
		b.append("}catch(_err){}\n");
	}

}

