package net.sf.javascribe.patterns.view.impl;

import java.util.HashMap;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class RadioButtonGroupDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-radio-button-group";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String eltVar = ctx.getElementVarName();
		String model = ctx.getTemplateAttributes().get("js-model");
		StringBuilder code = ctx.getCode();
		String doc = DirectiveUtils.DOCUMENT_REF;
		String options = ctx.getTemplateAttributes().get("js-options");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		String className = ctx.getDomAttributes().get("class");
		
		code.append(eltVar+" = "+doc+".createElement('div');\n");
		if (className!=null) {
			code.append(eltVar+".className = '"+className+"';\n");
			code.append(eltVar+".classList.add('js-radio-group');\n");
		} else {
			code.append(eltVar+".className = 'js-radio-group';\n");
		}

		ctx.continueRenderElement();
		
		String btnVar = ctx.newVarName("_b", "Node", execCtx);
		String radioContVar = ctx.newVarName("_c", "Node", execCtx);
		String spanVar = ctx.newVarName("_s", "Node", execCtx);
		String radioName = ctx.newVarName("_n", "Node", execCtx);
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("radioContVar", radioContVar);
		params.put("btnVar", btnVar);
		params.put("spanVar", spanVar);
		params.put("eltVar", eltVar);
		params.put("radioName", radioName);
		code.append("var "+btnVar+","+spanVar+","+radioContVar+";\n");
		String ref = null;
		if (model!=null) {
			if (DirectiveUtils.getPageName(ctx)==null) {
				throw new JavascribeException("A radio button group may only have a model binding when it is on a page template");
			}
			ExpressionUtil.buildValueExpression(DirectiveUtils.PAGE_VAR+".model."+model, "object", execCtx);
			ref = DirectiveUtils.PAGE_VAR+".model."+model;
		}
		
		StringTokenizer tok = new StringTokenizer(options,",");
		while(tok.hasMoreTokens()) {
			String t = tok.nextToken();
			int index = t.indexOf(':');
			String text = t.substring(0,index);
			String value = t.substring(index+1);
			params.put("text", text);
			params.put("value", value);
			code.append(JavascribeUtils.basicTemplating("directive/radio-buttons/radio_button_dom.txt", params, ctx.getProcessorContext()));
			if (ref!=null) {
				String setRef = ExpressionUtil.evaluateSetExpression(ref, ExpressionUtil.buildValueExpression(value, "string", execCtx), execCtx);
				code.append(btnVar+".onclick = function() {\n");
				code.append(setRef+";\n");
				code.append("};\n");
				String event = DirectiveUtils.getEventForModelRef(model);
				code.append(DirectiveUtils.PAGE_VAR+".controller.addEventListener('"+event+"',function() {\n");
				code.append("if ("+DirectiveUtils.getValidReference(model, execCtx)+"=='"+value+"') this.click();\n");
				code.append("}.bind("+btnVar+");\n");
			}
		}
	}

}
