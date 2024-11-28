package net.sf.javascribe.patterns.java.handwritten;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.FileProcessor;

public class HandwrittenCodeFileProcessor implements FileProcessor {

	ProcessorContext ctx = null;
	int priority = 0;
	FileHandler handler = null;
	String path = null;

	Exception exception = null;
	
	public HandwrittenCodeFileProcessor(String path) {
		this.path = path;
	}

	@Override
	public void init(ProcessorContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public int getPriority() {
		if (handler!=null) {
			return handler.getPriority();
		}
		return 0;
	}

	// TODO: In remove, remove 
	@Override
	public void setFile(ApplicationFile changedFile) {
		try {
			ctx.getLog().info("Creating file handler for "+changedFile.getPath());
			handler = new FileHandler(changedFile,ctx);
			exception = null;
		} catch(Exception e) {
			this.exception = e;
		}
	}
	
	@Override
	public String getName() {
		return "HandwrittenCodeFileProcessor["+path+"]";
	}

	@Override
	public void process() throws JavascribeException {
		if (exception!=null) {
			throw new JavascribeException("Couldn't read application file", exception);
		}
		handler.process();
	}

}

