package net.sf.javascribe.api;

import java.util.List;

/**
 * Represents a supported language platform.  The platform has a name and 
 * may have some number of inherently supported variable types (such as int 
 * or java.lang.String).
 * @author DCS
 *
 */
public interface LanguageSupport {

	/**
	 * Returns the name of this language, as it should be referred to in 
	 * ProcessorContext.setLanguageSupport.
	 * @return The name of this language.
	 */
	public String languageName();
	
	/**
	 * Returns globally available or "atomic" types for this language platform, 
	 * such as "integer", "string", "object", etc.
	 * @return List of base types supported by this language.
	 */
	public List<VariableType> getBaseVariableTypes();

}

