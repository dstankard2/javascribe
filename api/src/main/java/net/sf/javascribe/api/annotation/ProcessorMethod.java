package net.sf.javascribe.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.javascribe.api.config.ComponentBase;

/**
 * Marks a method as a processor for a component pattern.
 * A method with this annotation should have two parameters: an instance of 
 * the component class it processes, and a ProcessorContext.  It may throw 
 * an exception (most likely JavascribeException) and should return void.
 * @author DCS
 */
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ProcessorMethod {

	/**
	 * The class that this method will process.
	 * @return The subclass of ComponentBase that this method processes.
	 */
	Class<? extends ComponentBase> componentClass();

}
