package net.sf.javascribe.patterns.java.async;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.patterns.xml.java.async.ScheduledJob;

@Plugin
public class ScheduledJobProcessor implements ComponentProcessor<ScheduledJob> {

	// TODO: Implement this
	@Override
	public void process(ScheduledJob component, ProcessorContext ctx) throws JavascribeException {
		String pkg = JavaUtils.getJavaPackage(component, ctx);
		String className = "";
		String name = component.getName();
		String rule = component.getRule();
		String cronString = component.getCronString();
		
		
	}

}
