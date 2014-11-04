package net.sf.javascribe.api;

import java.io.IOException;
import java.io.InputStream;

import net.sf.javascribe.api.config.ComponentBase;

/**
 * This class is the primary interface between a component processor and 
 * the code generation engine.  It has methods for accessing configuration, 
 * accessing and adding source files, accessing declared variable types and 
 * setting the current language support.
 * @author DCS
 */
public interface ProcessorContext {

	/**
	 * Sets the current supported language.  Setting this makes a different set 
	 * of variable types available.
	 * @param languageName Java or Javascript
	 * @throws JavascribeException If the language name is invalid.
	 */
	public void setLanguageSupport(String languageName) throws JavascribeException;

	/**
	 * Returns the value of the specified configuration property.
	 * @param name Name of the property to return.
	 * @return value of this property.
	 */
	public String getProperty(String name);
	
	/**
	 * Returns the value of the specified configuration property, or 
	 * throws a JavascribeException if the property is not found.
	 * @param name Name of the property to return.
	 * @return Value of this property.
	 * @throws JavascribeException If the property is not found.
	 */
	public String getRequiredProperty(String name) throws JavascribeException;

	/**
	 * Returns the name of the type for the specified system attribute.
	 * @param attribute Name of system attribute to return.
	 * @return Type name, or null if the system attribute is not defined.
	 */
	public String getAttributeType(String attribute);
	
	/**
	 * Returns the specified variable type.
	 * @param typeName The type specified.
	 * @return The type for the specified name, or null.
	 */
	public VariableType getType(String typeName);
	
	/**
	 * Returns the file system path to the output directory for this code 
	 * distribution.
	 * @return String representation of the directory.
	 */
	public String getBuildRoot();
	
	/**
	 * Returns the source file at the specified path, or null if it has not 
	 * been defined.
	 * @param path File path.
	 * @return The source file, or null if not found.
	 */
	public SourceFile getSourceFile(String path);
	
	/**
	 * Adds the specified source file at its specified path.
	 * @param sourceFile Source file to add to output distribution.
	 * @throws JavascribeException If a file of the same path is already added.
	 */
	public void addSourceFile(SourceFile sourceFile) throws JavascribeException;

	/**
	 * Returns the type resolver for the current language as specified by 
	 * setLanguageSupport().
	 * @return Types currently supported for the selected language, or null if no language support has been selected.
	 */
	public TypeResolver getTypes();

	/**
	 * Adds an attribute to the current software system with the given name and type.
	 * This newly created system attribute is then available to other component processors.
	 * @param name Name of new attribute.
	 * @param type Type of new attribute.
	 * @throws JavascribeException If the system attribute already exists.
	 */
	public void addAttribute(String name,String type) throws JavascribeException;

	/**
	 * Returns EngineProperties, which gives access to all Scannable classes.
	 * @return EngineProperties for the current processor context.
	 */
	public EngineProperties getEngineProperties();

	/**
	 * Add an object to an object map that is global to all processors.
	 * @param name Name of object to put.
	 * @param object Object to make available.
	 */
	public void putObject(String name,Object object);
	
	/**
	 * Get an object that has been added previously.
	 * @param name Name of obect to retrieve.
	 * @return Object, or null if it is not there.
	 */
	public Object getObject(String name);
	
	/**
	 * Adds a component to the list of components to be processed.  The 
	 * newly-created list is re-sorted so the added component will be 
	 * processed in order.
	 * @param component Component to add.
	 */
	public void addComponent(ComponentBase component);
	
	/**
	 * Retrieves a resource from the application definition zip file.  
	 * The caller is responsible for closing the input stream.
	 * @param path Path of the file inside the zip
	 * @return InputStream to the given resource, or null.
	 */
	public InputStream getResource(String path) throws IOException;
	
}

