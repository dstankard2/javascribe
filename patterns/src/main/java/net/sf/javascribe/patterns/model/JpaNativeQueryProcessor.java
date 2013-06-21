package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@Processor
public class JpaNativeQueryProcessor {

	@ProcessorMethod(componentClass=JpaNativeQuery.class)
	public void processJpaNativeQuery(JpaNativeQuery query,GeneratorContext ctx) throws JavascribeException {
		System.out.println("Processing SQL Query '"+query.getName()+"' - Skipping");
		
	}
	
}
