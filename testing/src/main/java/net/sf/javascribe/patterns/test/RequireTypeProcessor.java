package net.sf.javascribe.patterns.test;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.VariableType;

public class RequireTypeProcessor implements ComponentProcessor<RequireTypePattern>{

	@Override
	public void process(RequireTypePattern component, ProcessorContext ctx) throws JavascribeException {
		String name = component.getRequiredType();
		String lang = component.getLang();

		ctx.setLanguageSupport(lang);
		
		VariableType type = JavascribeUtils.getType(VariableType.class, name, ctx);
		if (type==null) {
			throw new JavascribeException("Required type '"+name+"' for lang '"+lang+"' is not found");
		}
	}

}

