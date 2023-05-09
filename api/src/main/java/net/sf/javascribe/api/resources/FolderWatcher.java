package net.sf.javascribe.api.resources;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JasperException;

public interface FolderWatcher {

	int getPriority();

	void process(ProcessorContext ctx,ApplicationFile changedFile) throws JasperException;

	String getName();

}

