package net.sf.javascribe.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Javascribe plugin that must be found by the classpath scanner.
 * Any ComponentBase implementation, component processor or plugin must have 
 * this annotation.
 * @author DCS
 */
@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Scannable {

}
