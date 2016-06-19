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
		String html = DirectiveUtils.unescapeXml(ctx.getInnerHtml()).trim();
		String name = ctx.getDomAttributes().get("name");
		String params = ctx.getDomAttributes().get("params");
		String event = ctx.getDomAttributes().get("event");
		StringBuilder code = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		boolean isFunction = ((event!=null) || (name!=null));

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

		if (isFunction) {
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
		} else {
			code.append("(function() {");
		}
		

		if ((html.startsWith("<!--"))) {
		//if ((html.startsWith("<!--")) && (html.endsWith("-->"))) {
			html = html.substring(4).trim();
			html = html.substring(0, html.length()-3).trim();
		}
		
		JavascriptEvaluator eval = new JavascriptEvaluator(html,newCtx);
		DirectiveUtils.populateImpliedVariables(eval);
		JavascriptEvalResult result = eval.evalCodeBlock();
		if (result.getErrorMessage()!=null) {
			throw new JavascribeException("Couldn't build js-fn - Error parsing code: '"+result.getErrorMessage()+"'");
		}
		code.append(result.getResult().toString().trim());
		if (isFunction) {
			code.append("}\n");
			if (execute) {
				code.append(name+"();\n");
			}
		} else {
			code.append("})();\n");
		}
		
		if (event!=null) {
			if (ctx.getContainerVarName()==null) {
				throw new JavascribeException("You may not assign an event to a js-fn unless it is inside a HTML Template DOM Element");
			}
			String dispatcher = null;
			if (execCtx.getVariableType(DirectiveUtils.PAGE_VAR)!=null) dispatcher = DirectiveUtils.PAGE_VAR+".event";
			else if (execCtx.getVariableType(DirectiveUtils.EVENT_DISPATCHER_VAR)!=null) dispatcher = DirectiveUtils.EVENT_DISPATCHER_VAR+".event";
			if (dispatcher==null) {
				throw new JavascribeException("Couldn't attach a js-fn to event '"+event+"' because there is no event dispatcher present");
			}
			String ref = DirectiveUtils.parsePartialExpression(event, execCtx);
			code.append(dispatcher+"("+ref+","+name+","+ctx.getContainerVarName()+");\n");
		}
	}

}

