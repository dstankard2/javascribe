package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class EventDispatcherDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-event-dispatcher";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getDomAttributes().get("ref");
		StringBuilder code = ctx.getCode();
		CodeExecutionContext execCtx = ctx.getExecCtx();
		String var = DirectiveUtils.EVENT_DISPATCHER_VAR;

		if (ref!=null) {
			ref = ref.trim();
			if (execCtx.getVariableType(ref)!=null) {
				throw new JavascribeException("Couldn't add event dispatcher as there is already a variable in this template called '"+ref+"'");
			}
		}
		if (execCtx.getVariableType(var)!=null) {
			throw new JavascribeException("Couldn't add event dispatcher as there is already one in this template");
		}

		code.append("var "+var+" = new EventDispatcher();\n");
		if (ref!=null) {
			code.append("var "+ref+" = "+var+";\n");
			execCtx.addVariable(ref, "object");
		}
		execCtx.addVariable(var, "object");
	}

}

