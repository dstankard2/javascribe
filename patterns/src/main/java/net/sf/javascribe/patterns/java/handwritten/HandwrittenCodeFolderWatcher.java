package net.sf.javascribe.patterns.java.handwritten;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FolderWatcher;

public class HandwrittenCodeFolderWatcher implements FolderWatcher {
	//private ProcessorContext ctx = null;
	private String path = null;
	
	public HandwrittenCodeFolderWatcher(String path) {
		this.path = path;
	}

	@Override
	public String getName() {
		return "HandwrittenCodeFolderWatcher["+path+"]";
	}

	@Override
	public void process(ProcessorContext ctx, ApplicationFile applicationFile) throws JavascribeException {
		String name = applicationFile.getName();
		if (!name.endsWith(".java")) return;
		
		String path = applicationFile.getPath();
		
		HandwrittenCodeFileProcessor w = new HandwrittenCodeFileProcessor(path);
		ctx.addFileProcessor(path, w);
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
