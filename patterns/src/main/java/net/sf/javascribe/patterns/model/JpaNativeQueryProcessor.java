package net.sf.javascribe.patterns.model;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
@Processor
public class JpaNativeQueryProcessor {

	private static final Logger log = Logger.getLogger(JpaNativeQueryProcessor.class);

	@ProcessorMethod(componentClass=JpaNativeQuery.class)
	public void processJpaNativeQuery(JpaNativeQuery query,ProcessorContext ctx) throws JavascribeException {
		log.info("Processing SQL Query '"+query.getName()+"' - Skipping");
		
	}
	
}
