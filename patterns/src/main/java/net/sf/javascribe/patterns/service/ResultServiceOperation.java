package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.GeneratorContext;

public interface ResultServiceOperation {

	public String getResultType(GeneratorContext ctx) throws JavascribeException;
	public String getResultName(GeneratorContext ctx) throws JavascribeException;

}

