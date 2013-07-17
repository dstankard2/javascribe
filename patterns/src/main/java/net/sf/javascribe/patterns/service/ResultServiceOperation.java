package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;

public interface ResultServiceOperation {

	public String getResultType(ProcessorContext ctx) throws JavascribeException;
	public String getResultName(ProcessorContext ctx) throws JavascribeException;

}

