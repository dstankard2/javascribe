package net.sf.javascribe.patterns.test;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;

@Plugin
public class FirstProcessor implements ComponentProcessor<FirstPattern> {

	@Override
	public void process(FirstPattern component, ProcessorContext ctx) throws JavascribeException {
		ctx.getLog().info("Processing FirstPattern");

		ctx.setLanguageSupport("Java8");
		
		JavaServiceType service = null;
		String name = component.getServiceName();
		
		service = JavascribeUtils.getType(JavaServiceType.class, name, ctx);
		if (service==null) {
			service = new JavaServiceType(name, "pkg."+name, ctx.getBuildContext());
			ctx.addVariableType(service);
		}
	}

}
