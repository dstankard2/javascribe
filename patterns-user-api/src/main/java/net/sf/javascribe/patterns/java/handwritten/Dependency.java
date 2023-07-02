package net.sf.javascribe.patterns.java.handwritten;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.SOURCE)
public @interface Dependency {

	String ref() default "";

}
