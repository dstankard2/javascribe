package net.sf.javascribe.patterns.tomcat;

import java.util.ArrayList;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.java.http.JavaWebUtils;
import net.sf.javascribe.patterns.maven.ExecutionConfig;
import net.sf.javascribe.patterns.maven.MavenBuildContext;
import net.sf.javascribe.patterns.maven.MavenUtils;
import net.sf.javascribe.patterns.maven.PluginConfig;
import net.sf.javascribe.patterns.maven.PropertySet;
import net.sf.javascribe.patterns.xml.tomcat.EmbedTomcatJar;

@Plugin
public class EmbedTomcatJarProcessor implements ComponentProcessor<EmbedTomcatJar> {

	public void process(EmbedTomcatJar comp, ProcessorContext ctx) throws JavascribeException {
		String context = comp.getContext();
		String jarName = comp.getJarName();
		Integer port = comp.getPort();
		String pkg = comp.getPkg();

		ctx.getLog().info("Processing embed tomcat application "+comp.getComponentName());

		ctx.getLog().info("Embed Tomcat is added webapp variable types");
		JavaWebUtils.addServletTypes(ctx);
		EmbedTomcatFinalizer fin = new EmbedTomcatFinalizer(jarName,context,port);
		ctx.addComponent(fin);
		
		ctx.getBuildContext().addDependency("tomcat-embed-core");
		ctx.getBuildContext().addDependency("tomcat-embed-jasper");
		ctx.getBuildContext().addDependency("tomcat-jasper");
		
		// For a Maven Build, add the Maven assembly plugin to build a Jar with dependencies
		if (MavenUtils.isMavenBuild(ctx)) {
			PluginConfig cfg = PluginConfig.builder().artifact("org.apache.maven.plugins:maven-assembly-plugin").configuration(new PropertySet("configuration"))
					.dependencies(new ArrayList<>()).executions(new ArrayList<>()).build();
			cfg.getConfiguration().addNestingProperty("archive").addPropertyWithNestedProperties("manifestEntries").addPropertySingleValue("Main-Class", pkg+'.'+"TomcatMain");
			ExecutionConfig exec = new ExecutionConfig();
			exec.setPhase("package");
			cfg.getExecutions().add(exec);
			exec.getConfiguration().addPropertyValueList("descriptorRefs", "descriptorRef").addValue("jar-with-dependencies");
			exec.getGoals().add("single");
			MavenUtils.addMavenPlugin(cfg, ctx);
			
			MavenBuildContext bctx = MavenUtils.getMavenBuildContext(ctx);

			bctx.addBuildPhase("package");
			bctx.setFinalName(jarName);

			cfg = PluginConfig.builder().artifact("org.codehaus.mojo:exec-maven-plugin").configuration(new PropertySet("configuration")).dependencies(new ArrayList<>())
					.executions(new ArrayList<>()).build();
			exec = new ExecutionConfig();
			cfg.getExecutions().add(exec);
			exec.setId("Embed Tomcat");
			exec.setPhase("install");
			exec.getGoals().add("exec");
			exec.getConfiguration().addProperty("executable", "java -jar target\\"+jarName+"-jar-with-dependencies.jar");
		}
		EmbedTomcatRuntimePlatform platform = JavaWebUtils.addWebPlatform(ctx);
		platform.setContextRoot(context);
	}
	
}
