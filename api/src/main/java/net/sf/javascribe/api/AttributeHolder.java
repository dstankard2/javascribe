/*
 * Created on Jul 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.javascribe.api;

import java.util.List;



/**
 * A variable type that has attributes.  These tend 
 * to be data objects (like JPA entities or object representations of JSON 
 * objects, things like this).
 * @author DCS
 */
public interface AttributeHolder extends VariableType {

    /**
     * Returns a string which represents the code needed to retrieve the specified attribute from 
     * the specified attribute holder, transforming it (if necessary) into the specified type.
     * This transformation is necessary in the case of attribute holders that hold generic 
     * types (such as Maps).
     * @param varName Name of the attribute holder instance we are retrieving the value from.
     * @param attribName Attribute to retrieve from the attribute holder.
     * @param targetType The type we want to interpret resulting value as.
     * @param execCtx Current code execution context.
     * @return A string containing the code to retrieve the specified attribute, as the specified type.
     * @throws IllegalArgumentException If the attribute cannot be interpreted as being of the targetType.
     * @throws ProcessingException If there is another issue.
     */
	public String getCodeToRetrieveAttribute(String varName,String attribName,String targetType,CodeExecutionContext execCtx) throws IllegalArgumentException,JavascribeException;

	/**
	 * 
	 * @param varName Name of the attribute holder instance we are trying to modify.
	 * @param attribName Name of the attribute to modify on the specified attribute holder.
	 * @param evaluatedValue Evaluated expression to set the specified attribute to.
	 * @param execCtx Current code execution context.
	 * @return Code to set the specified attribute to the given value.
	 */
	public String getCodeToSetAttribute(String varName,String attribName,String evaluatedValue,CodeExecutionContext execCtx) throws JavascribeException;
	
	/**
	 * Returns the type of the specified attribute.
	 * @param attrib The attribute to retrieve the type for.
	 * @return Type of the specified attribute.
	 */
	public String getAttributeType(String attrib) throws JavascribeException;

	public List<String> getAttributeNames() throws JavascribeException;

}

