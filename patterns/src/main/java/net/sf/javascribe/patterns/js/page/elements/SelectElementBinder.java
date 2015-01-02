package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.Binding;

@Scannable
@ElementBinder(elementType="select")
public class SelectElementBinder extends InputElementBinder {

	@ElementBinding(bindingType="value")
	public String selectValueBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();

		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx));
		
		if (target.indexOf('.')>0) {
			throw new JavascribeException("Value binding to select does not support nested model attributes.");
		}
		if (ctx.getModelAttributeType(target)==null) {
			throw new JavascribeException("Target of binding is not a model attribute.");
		}
		values.put("upperTargetName",JavascribeUtils.getUpperCamelName(target));
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));

		ret = JavascribeUtils.basicTemplating("js-mvvm-select-value-binding.txt", values, ctx.getCtx());

		return ret;
	}
	
}

