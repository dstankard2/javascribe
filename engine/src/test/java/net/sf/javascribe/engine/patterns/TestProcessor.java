package net.sf.javascribe.engine.patterns;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;

@Plugin
public class TestProcessor implements ComponentProcessor<TestPattern> {

	@Override
	public void process(TestPattern component, ProcessorContext ctx) throws JavascribeException {
		// TODO Auto-generated method stub
		ctx.getLog().info("Processing TestPattern");
	}

	
}
