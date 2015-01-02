package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;
import net.sf.javascribe.langsupport.javascript.JavascriptServiceObject;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class RenderDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ELEMENT };
	}

	@Override
	public String getName() {
		return "render";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String template = ctx.getAttributes().get("js-template");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		int i = template.indexOf('.');
		if (i<=0) {
			throw new JavascribeException("A template call must refer to a function on an object");
		}
		String objName = template.substring(0, i);
		String rule = template.substring(i+1);
		String typeName = ctx.getProcessorContext().getAttributeType(objName);
		JavascriptVariableType t = (JavascriptVariableType)ctx.getProcessorContext().getType(typeName);
		if (t==null) {
			throw new JavascribeException("Could not find a Javascript object called '"+objName+"'");
		}
		if (!(t instanceof JavascriptServiceObject)) {
			throw new JavascribeException("Javascript type '"+objName+"' is not a service object");
		}

		JavascriptServiceObject ser = (JavascriptServiceObject)t;
		if (execCtx.getVariableType(objName)==null) {
			b.append(ser.declare(objName, execCtx));
			b.append(ser.instantiate(objName, null, execCtx));
			execCtx.addVariable(objName, ser.getName());
		}

		String resVar = ctx.newVarName("_r", "object", execCtx);
		PageModelType model = DirectiveUtils.getPageModelType(ctx);
		boolean done = false;
		for(JavascriptFunction op : ser.getOperations()) {
			if (!op.getName().equals(rule)) continue;
			String s = DirectiveUtils.attemptInvoke(resVar, objName, op, model, execCtx);
			if (s==null) continue;
			b.append(s);
			done = true;
			break;
		}
		if (!done) {
			throw new JavascribeException("Couldn't invoke template "+template);
		}
		
		b.append("(function() {\n");
		b.append("var "+resVar+";\n");
		b.append("}());\n");

	}

}

