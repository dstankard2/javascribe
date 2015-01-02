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
	
	@ElementBinding(bindingType="class")
	public String divClassBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		
		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx));
		
		ret = JavascribeUtils.basicTemplating("js-mvvm-dom-class-binding.txt", values, ctx.getCtx());
		
		return ret;
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
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx));
		
		ret = JavascribeUtils.basicTemplating("js-mvvm-dom-display-binding.txt", values, ctx.getCtx());
		
		return ret;
	}

	@ElementBinding(bindingType="content")
	public String divContentBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String event = binding.getEvent();
		String template = null;
		
		values.put("element",binding.getElement());
		values.put("pageName", ctx.getPageName());
		values.put("evalValue",BinderUtils.evalTarget(target, "val", ctx).getCodeText());
		values.put("event", BinderUtils.getEventToTrigger(target, event, ctx));
		template = "js-mvvm-dom-content-binding.txt";

		/*
		if (target.indexOf('.')<0) {
			values.put("element",binding.getElement());
			values.put("pageName", ctx.getPageName());
			values.put("evalValue",BinderUtils.evalTarget(target, "val", ctx).getCodeText());
//			values.put("function",BinderUtils.getTargetAccessString(target, ctx, false));
			values.put("event", BinderUtils.getEventToTrigger(target, event, ctx, false));
			template = "js-mvvm-dom-content-binding.txt";
		} else {
			int i = target.indexOf('.');
			String modelAttrib = target.substring(0, i);
			if (ctx.getModelAttributeType(modelAttrib)==null) {
				throw new JavascribeException("Invalid model attribute specified in element binding '"+modelAttrib+"'");
			}
			values.put("lowerCamelModelAttrib",modelAttrib);
			values.put("upperCamelModelAttrib",JavascribeUtils.getUpperCamelName(modelAttrib));
			String func = BinderUtils.getTargetAccessString(target, ctx, false);
			values.put("element",binding.getElement());
			values.put("event", modelAttrib+"Changed");
			values.put("pageName", ctx.getPageName());
			values.put("function",func);
			template = "js-mvvm-dom-content-nested-binding.txt";
		}
		*/
		
		ret = JavascribeUtils.basicTemplating(template, values, ctx.getCtx());

		return ret;
	}
}

