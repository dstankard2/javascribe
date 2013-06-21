package net.sf.javascribe.api;

import java.util.List;

public interface LanguageSupport {

	public String languageName();
	public List<VariableType> getBaseVariableTypes();

}

