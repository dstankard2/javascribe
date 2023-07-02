package net.sf.javascribe.patterns.test;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public class RequireAttributeProcessor implements ComponentProcessor<RequireAttributePattern>{

	@Override
	public void process(RequireAttributePattern component, ProcessorContext ctx) throws JavascribeException {
		String name = component.getRequiredAttribute();

		String type = ctx.getSystemAttribute(name);
		if (type==null) {
			throw new JavascribeException("Found no system attribute "+name);
		}
	}

}

