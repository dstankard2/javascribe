package net.sf.javascribe.patterns.build;

import java.util.List;

import net.sf.javascribe.api.BuildComponentProcessor;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.BuildProcessorContext;
import net.sf.javascribe.api.Command;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.engine.data.files.DefaultBuildContext;

@Plugin
public class JavaBuildProcessor implements BuildComponentProcessor<JavaBuild> {
	JavaBuild buildComponent;
	BuildProcessorContext ctx;
	JavaFolderWatcher watcher = null;
	ApplicationFolder folder = null;
	
	@Override
	public void initialize(JavaBuild buildComponent, BuildProcessorContext ctx) throws JavascribeException {
		ctx.getLog().info("Time to initialize a build");
		this.ctx = ctx;
		this.buildComponent = buildComponent;
		folder = ctx.getFolder();
		this.watcher = new JavaFolderWatcher();
		String watchPath = folder.getPath()+"src";
		ctx.addFolderWatcher(watchPath, watcher);
	}

	@Override
	public BuildContext createBuildContext() {
		return new DefaultBuildContext(ctx);
	}

	@Override
	public void generateBuild() throws JavascribeException {
		System.out.println("Time to generate build");
	}

	@Override
	public List<Command> build() {
		return null;
	}

	@Override
	public List<Command> clean() {
		return null;
	}

}

