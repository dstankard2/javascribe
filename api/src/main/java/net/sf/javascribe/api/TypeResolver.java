package net.sf.javascribe.api;

/**
 * An API that gives component processors access to types that have already 
 * been added to the system.  Likewise, a component processor may add a type 
 * to the system for other component processors to access.
 * @author DCS
 *
 */
public interface TypeResolver {

	/**
	 * Add a type to the system.
	 * @param type
	 * @throws JavascribeException If the type already exists.
	 */
	public void addType(VariableType type) throws JavascribeException;
	public void addOrReplaceType(VariableType type);
	public VariableType getType(String name);
	
}
