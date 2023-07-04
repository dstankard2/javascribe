package net.sf.javascribe.patterns.java.http;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.xml.java.http.ServletFilterGroup;

@Plugin
public class ServletFilterChainProcessor implements ComponentProcessor<ServletFilterGroup> {

	@Override
	public void process(ServletFilterGroup comp, ProcessorContext ctx) throws JavascribeException {
		String name = comp.getName();
		String filters = comp.getFilters();

		if (name.trim().length()==0) {
			throw new JavascribeException("ServletFilterChain has no name defined");
		}
		
		if (filters.trim().length()==0) {
			throw new JavascribeException("ServletFilterChain has no filters specified");
		}
		
		String[] filterNames = filters.split(",");
		JavaWebUtils.addServletFilterChain(name, filterNames, ctx);
	}

	
}
