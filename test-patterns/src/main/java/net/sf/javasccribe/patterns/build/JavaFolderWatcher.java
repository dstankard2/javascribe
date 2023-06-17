package net.sf.javasccribe.patterns.build;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FolderWatcher;

public class JavaFolderWatcher implements FolderWatcher {
	
	public JavaFolderWatcher() {
	}

	@Override
	public void process(ProcessorContext ctx, ApplicationFile changedFile) throws JavascribeException {
		String path = changedFile.getPath();
		ctx.getLog().info("Processing a changed file "+path);
	}

	@Override
	public String getName() {
		return "JavaFolderWatcher";
	}

	@Override
	public int getPriority() {
		return 1000;
	}

}

