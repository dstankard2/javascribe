package net.sf.javascribe.patterns.view.impl;

import java.util.ArrayList;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;
import net.sf.javascribe.patterns.xml.page.Binding;
import net.sf.javascribe.patterns.xml.page.Bindings;

@Scannable
public class GenericBindingDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-generic-binding";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String target = ctx.getDomAttributes().get("target");
		String event = ctx.getDomAttributes().get("event");
		String pageName = DirectiveUtils.getPageName(ctx);
		
		if ((target==null) || (target.trim().length()==0)) {
			throw new JavascribeException("Directive js-generic-binding requires a target attribute");
		}
		if ((event==null) || (event.trim().length()==0)) {
			throw new JavascribeException("Directive js-generic-binding requires a event attribute");
		}
		if (pageName==null) {
			throw new JavascribeException("Directive js-generic-binding can only be used on a page template");
		}
		
		Bindings comp = new Bindings();
		comp.setPageName(pageName);
		comp.setBinding(new ArrayList<Binding>());
		Binding binding = new Binding();
		binding.setEvent(event);
		binding.setTarget(target);
		binding.setType("generic");
		comp.getBinding().add(binding);
		ctx.getProcessorContext().addComponent(comp);
	}

}

