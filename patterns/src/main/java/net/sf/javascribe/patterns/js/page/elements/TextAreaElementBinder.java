package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.Binding;

@Scannable
@ElementBinder(elementType="textarea")
public class TextAreaElementBinder extends DivElementBinder {

	@ElementBinding(bindingType="enable")
	public String inputEnableBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		
		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx, false));

		ret = JavascribeUtils.basicTemplating("js-mvvm-input-enable-binding.txt", values, ctx.getCtx());

		return ret;
	}
	
	@ElementBinding(bindingType="value")
	public String inputValueBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		String template = null;
		
		values.put("pageName", ctx.getPageName());
		values.put("element", binding.getElement());
		
		event = BinderUtils.getEventToTrigger(target, event, ctx, false);
		values.put("event", event);
		values.put("getter", BinderUtils.getGetter(target));
		values.put("setter", BinderUtils.getSetExpression(target, "this.view."+binding.getElement()+".value"));
		if (target.indexOf('.')<0) {
			template = "js-mvvm-textarea-value-binding.txt";
		} else {
			template = "js-mvvm-textarea-value-binding-nested.txt";
		}
		ret = JavascribeUtils.basicTemplating(template, values, ctx.getCtx());
		
		return ret;
	}
	
}

