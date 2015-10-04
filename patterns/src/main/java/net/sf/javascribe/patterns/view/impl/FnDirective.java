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
				if (att.getType()==null) throw new JavascribeException("Couldn't find type for js-fn param named '"+att.getName()+"'");
				newCtx.addVariable(att.getName(), att.getType());
			}
		}
		code.append(") {\n");
		JaEval2 eval = new JaEval2(html,newCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JaEvalResult result = eval.parseCodeBlock();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException("Couldn't build js-fn - Error parsing code: '"+result.getErrorMessage()+"'");
		}
		code.append(result.getResult().toString());
		code.append("}\n");
		if (execute) {
			code.append(name+"();\n");
		}
		if (event!=null) {
			if (DirectiveUtils.getPageName(ctx)!=null) {
				String ref = DirectiveUtils.parsePartialExpression(event, execCtx);
				code.append(DirectiveUtils.PAGE_VAR+".event("+ref+","+name+","+ctx.getContainerVarName()+");\n");
			}
		}
	}

}

