package net.sf.javascribe.api;

import java.util.List;

public interface LanguageSupport {

	/**
	 * Returns the name of this language, as it should be referred to in 
	 * ProcessorContext.setLanguageSupport.
	 * @return
	 */
	public String languageName();
	
	/**
	 * Returns globally available or "atomic" types for this language, 
	 * such as "integer", "string", "object", etc.
	 * @return List of base types supported by this language.
	 */
	public List<VariableType> getBaseVariableTypes();

}

