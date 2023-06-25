package net.sf.javascribe.patterns.test.userfiles;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public class FolderWatchingProcessor implements ComponentProcessor<FolderWatchingPattern> {

	@Override
	public void process(FolderWatchingPattern component, ProcessorContext ctx) throws JavascribeException {
		String path = component.getPath();
		FirstFolderWatcher watcher = new FirstFolderWatcher();
		ctx.addFolderWatcher(path, watcher);
	}

}

