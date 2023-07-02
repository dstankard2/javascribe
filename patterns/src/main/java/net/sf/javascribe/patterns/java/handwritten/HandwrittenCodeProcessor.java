package net.sf.javascribe.patterns.java.handwritten;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.patterns.xml.java.handwritten.HandwrittenCode;

@Plugin
public class HandwrittenCodeProcessor implements ComponentProcessor<HandwrittenCode> {
	String watchPath = null;

	@Override
	public void process(HandwrittenCode comp, ProcessorContext ctx) throws JavascribeException {
		String path = comp.getPath();
		
		ApplicationResource res = ctx.getBuildContext().getApplicationResource(path);
		
		if (res==null) {
			throw new JavascribeException("Could not find resource '"+path+"'");
		}
		
		if (res instanceof ApplicationFile) {
			throw new JavascribeException("Handwritten Java code component can only watch a directory");
		} else {
			ApplicationFolder folder = (ApplicationFolder)res;
			watchDirectory(folder, ctx);
		}
	}
	
	protected void watchDirectory(ApplicationFolder folder, ProcessorContext ctx) {
		HandwrittenCodeFolderWatcher folderWatcher = new HandwrittenCodeFolderWatcher(folder.getPath());
		ctx.addFolderWatcher(folder.getPath(),folderWatcher);
	}

}

