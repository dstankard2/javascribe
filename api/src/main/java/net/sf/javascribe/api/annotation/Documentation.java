package net.sf.javascribe.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks documentation on the specific part of the pattern.  Can contain an 
 * explanation, one or more examples and a flag specifying if it's required.<p/>
 * If this annotation is on a Component class, a HTML file will be generated in the Javascribe
 * docs folder.<p/>
 * If this annotation is on a Build/Component class field that has the @@XmlElement or @@XmlAttribute 
 * annotation, it will generate documentation for that field.<p/>
 * If this annotation is on a Build/Component class method that has the @@ConfigProperty 
 * annotation,it will generate documentation for that configuration in this pattern.
 * @author DCS
 */
@Target(value= {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Documentation {

	boolean required() default true;
	String name();
	String description() default "";
	String example() default "";

}
