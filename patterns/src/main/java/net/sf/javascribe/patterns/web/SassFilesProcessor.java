package net.sf.javascribe.patterns.web;

import java.util.ArrayList;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.maven.ExecutionConfig;
import net.sf.javascribe.patterns.maven.MavenBuildContext;
import net.sf.javascribe.patterns.maven.MavenUtils;
import net.sf.javascribe.patterns.maven.PluginConfig;
import net.sf.javascribe.patterns.maven.PropertySet;
import net.sf.javascribe.patterns.xml.web.SassFiles;

@Plugin
public class SassFilesProcessor implements ComponentProcessor<SassFiles> {

	@Override
	public void process(SassFiles comp, ProcessorContext ctx) throws JavascribeException {
		String src = comp.getSourcePath();
		String dest = comp.getOutputPath();
		
		if (src.trim().length()==0) {
			throw new JavascribeException("No sourcePath specified for sassFiles component");
		}
		if (dest.trim().length()==0) {
			throw new JavascribeException("No outputPath specified for sassFiles component");
		}
		
		if (MavenUtils.isMavenBuild(ctx)) {
			handleMaven(src,dest,ctx);
		} else {
			ctx.getLog().warn("SassFiles component not generating any files");
		}
	}
	
	protected void handleMaven(String src,String dest,ProcessorContext ctx) {
		MavenBuildContext bctx = MavenUtils.getMavenBuildContext(ctx);
		PluginConfig cfg = null;
		ExecutionConfig execConfig = new ExecutionConfig();
		String srcPath = ctx.getResource(".").getPath();
		String buildPath = bctx.getApplicationFolderPath();

		cfg = PluginConfig.builder().artifact("nl.geodienstencentrum.maven:sass-maven-plugin:3.5.5").configuration(new PropertySet("configuration")).dependencies(new ArrayList<>())
				.executions(new ArrayList<>()).build();
		
		cfg.getExecutions().add(execConfig);
		srcPath = srcPath.substring(buildPath.length()) + src;
		execConfig.setId("sassProcessSource");
		execConfig.setPhase("generate-sources");
		execConfig.getGoals().add("update-stylesheets");
		ctx.getLog().warn("Defaulting to sass-maven-plugin version 2.22");
		
		execConfig.getConfiguration().addProperty("sassSourceDirectory", srcPath);
		//execConfig.getConfiguration().addProperty("buildDirectory", "${basedir}/"+dest);
		//execConfig.getConfiguration().addProperty("baseOutputDirectory", "${baseDir}/"+dest);
		execConfig.getConfiguration().addProperty("destination", dest);

		bctx.addPlugin(cfg);
	}

}
