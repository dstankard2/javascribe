package net.sf.javascribe.patterns.js.page.elements;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.js.page.Binding;

@Scannable
@ElementBinder(elementType="anchor")
public class AnchorElementBinder extends DivElementBinder {

	@ElementBinding(bindingType="href")
	public String anchorHrefBinding(Binding binding,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		Map<String,String> values = new HashMap<String,String>();
		String target = binding.getTarget();

		values.put("element",binding.getElement());
		values.put("function",BinderUtils.getTargetAccessString(target, ctx, true));

		ret = JavascribeUtils.basicTemplating("js-mvvm-anchor-href-binding.txt", values);

		return ret;
	}

}
