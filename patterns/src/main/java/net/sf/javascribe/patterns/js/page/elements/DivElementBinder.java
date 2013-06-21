package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.Binding;

@Scannable
@ElementBinder(elementType="div")
public class DivElementBinder {

	@BindToPage
	public String bindElementToPage(String elementName,String pageName) {
		return pageName+".view."+elementName+" = document.getElementById(\""+elementName+"\");\n";
	}
	
	@ElementBinding(bindingType="display")
	public String divDisplayBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		
		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx, false));
		
		ret = JavascribeUtils.basicTemplating("js-mvvm-dom-display-binding.txt", values);
		
		return ret;
	}

	@ElementBinding(bindingType="content")
	public String divContentBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		
		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx, false));
		
		ret = JavascribeUtils.basicTemplating("js-mvvm-dom-content-binding.txt", values);

		return ret;
	}
}

