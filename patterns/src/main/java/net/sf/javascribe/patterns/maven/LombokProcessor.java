package net.sf.javascribe.patterns.maven;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.xml.maven.LombokSupport;

@Plugin
public class LombokProcessor implements ComponentProcessor<LombokSupport> {

	@Override
	public void process(LombokSupport component, ProcessorContext ctx) throws JavascribeException {
		BuildContext bctx = ctx.getBuildContext();
		
		if (!(bctx instanceof MavenBuildContext)) {
			throw new JavascribeException("Lombok is only supported when there is a Maven build component present");
		}

		String lombokVersion = component.getLombokVersion();
		MavenBuildContext mavenCtx = (MavenBuildContext)bctx;

		mavenCtx.addDependency("lombok");
		mavenCtx.addAnnotationProcessor(lombokVersion);
	}

}
