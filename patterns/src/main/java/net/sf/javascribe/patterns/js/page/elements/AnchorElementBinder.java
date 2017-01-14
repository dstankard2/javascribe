package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.xml.page.Binding;

@Scannable
@ElementBinder(elementType="anchor")
public class AnchorElementBinder extends DivElementBinder {

	@ElementBinding(bindingType="onclick")
	public String anchorOnclickBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String func = BinderUtils.getTargetAccessString(target, ctx, true);

		if (func.startsWith("this.")) {
			func = ctx.getPageName()+'.'+func.substring(5);
		}
		values.put("element",binding.getElement());
		values.put("function",func);

		ret = JavascribeUtils.basicTemplating("js-mvvm-anchor-onclick-binding.txt", values,ctx.getCtx());

		return ret;
	}
	
	@ElementBinding(bindingType="href")
	public String anchorHrefBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();
		String func = BinderUtils.getTargetAccessString(target, ctx, true);

		if (func.startsWith("this.")) {
			func = ctx.getPageName()+'.'+func.substring(5);
		}
		values.put("element",binding.getElement());
		values.put("function",func);

		ret = JavascribeUtils.basicTemplating("js-mvvm-anchor-href-binding.txt", values, ctx.getCtx());

		return ret;
	}

}
