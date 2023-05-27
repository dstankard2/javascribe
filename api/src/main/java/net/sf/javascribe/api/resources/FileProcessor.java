package net.sf.javascribe.api.resources;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public interface FileProcessor {

	int getPriority();
	void init(ProcessorContext ctx);
	void setFile(ApplicationFile applicationFile);
	void process() throws JavascribeException;
	String getName();

}

