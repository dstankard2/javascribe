package net.sf.javascribe.api.config;

/**
 * <p>Any scannable class found in the JavaScribe lib directory that 
 * implements this interface will be added to the JAXB context used to 
 * read component XML files.</p>
 * 
 * <p>Please note, any class that implements this interface must also 
 * have the @Scannable annotation in order for the Javascribe classpath 
 * scanner to find it.  The class should also be in a JAR file in the lib 
 * directory.</p>
 * @author Dave
 *
 */
public interface ComponentConfigElement {

}
