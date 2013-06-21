package net.sf.javascribe.patterns.servlet;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@Processor
public class WebServletFilterProcessor {
	
	@ProcessorMethod(componentClass=WebServletFilter.class)
	public void process(WebServletFilter filter,GeneratorContext ctx) throws JavascribeException {
		WebXmlFile webXml = null;
		
		System.out.println("Processing web servlet filter '"+filter.getName()+"'");

		if (filter.getName()==null) {
			throw new JavascribeException("unable to find name of filter component");
		}
		if (filter.getClassName()==null) {
			throw new JavascribeException("unable to find className of filter component");
		}

		webXml = WebUtils.getWebXml(ctx);
		webXml.addFilter(filter.getName(), filter.getClassName());
		
		ServletFilterType filterType = new ServletFilterType(filter.getName());
		ctx.getTypes().addType(filterType);
	}

}

