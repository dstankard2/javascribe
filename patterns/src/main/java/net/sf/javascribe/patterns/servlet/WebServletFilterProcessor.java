package net.sf.javascribe.patterns.servlet;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.xml.servlet.WebServletFilter;

@Scannable
@Processor
public class WebServletFilterProcessor {
	
	private static final Logger log = Logger.getLogger(WebServletFilterProcessor.class);

	@ProcessorMethod(componentClass=WebServletFilter.class)
	public void process(WebServletFilter filter,ProcessorContext ctx) throws JavascribeException {
		WebXmlFile webXml = null;
		ctx.setLanguageSupport("Java");
		
		log.info("Processing web servlet filter '"+filter.getName()+"'");

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

