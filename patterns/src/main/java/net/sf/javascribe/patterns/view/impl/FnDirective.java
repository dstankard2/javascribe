package net.sf.javascribe.patterns.view.impl;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class FnDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-fn";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String html = ctx.getInnerHtml();
		String name = ctx.getDomAttributes().get("name");
		String params = ctx.getDomAttributes().get("params");
		String event = ctx.getDomAttributes().get("event");
		StringBuilder code = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);

		//if ((name==null) && (event==null)) {
		//	throw new JavascribeException("js-fn requires that function have either a name or an event specified");
		//}
		if (params==null) params = "";
		if (newCtx.getTypeForVariable(name)!=null) {
			throw new JavascribeException("Cannot define a function named '"+name+"' as a variable of that name already exists in the execution context");
		}

		boolean execute = false;
		if (name==null) {
			name = ctx.newVarName("_f", "function", execCtx);
			if (event==null) execute = true;
		}
		else execCtx.addVariable(name, "function");
		
		code.append("function "+name+"(");
		if (params.trim().length()>0) {
			boolean first = true;
			List<Attribute> atts = JavascribeUtils.readAttributes(ctx.getProcessorContext(), params);
			for(Attribute att : atts) {
				if (first) first = false;
				else code.append(',');
				code.append(att.getName());
				newCtx.addVariable(att.getName(), att.getType());
			}
		}
		code.append(") {\n");
		JavascriptEvaluator eval = new JavascriptEvaluator(html, newCtx);
		eval.parseCodeBlock();
		if (eval.getError()!=null) {
			throw new JavascribeException("Couldn't build js-fn - Error parsing code: '"+eval.getError()+"'");
		}
		code.append(eval.getResult());
		code.append("}\n");
		if (execute) {
			code.append(name+"();\n");
		}
		if (event!=null) {
			if (DirectiveUtils.getPageName(ctx)!=null) {
				code.append(DirectiveUtils.PAGE_VAR+".event('"+event+"',"+name+");\n");
			}
		}
	}

}

