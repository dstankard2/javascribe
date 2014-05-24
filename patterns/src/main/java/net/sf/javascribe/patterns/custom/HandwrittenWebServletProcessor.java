package net.sf.javascribe.patterns.custom;

import java.util.StringTokenizer;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.servlet.WebUtils;
import net.sf.javascribe.patterns.servlet.WebXmlFile;

@Scannable
@Processor
public class HandwrittenWebServletProcessor {

	@ProcessorMethod(componentClass=HandwrittenWebServlet.class)
	public void process(HandwrittenWebServlet comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		WebXmlFile webXmlFile = WebUtils.getWebXml(ctx);
		
		webXmlFile.addServlet(comp.getName(), comp.getName(), comp.getClassName());
		webXmlFile.addServletMapping(comp.getName(), comp.getUriPath());
		
		String filters = comp.getFilters();
		if (filters.trim().length()>0) {
			StringTokenizer tok = new StringTokenizer(filters,",");
			while (tok.hasMoreTokens()) {
				webXmlFile.addFilterMapping(tok.nextToken(), comp.getUriPath());
			}
		}
	}
	
}
