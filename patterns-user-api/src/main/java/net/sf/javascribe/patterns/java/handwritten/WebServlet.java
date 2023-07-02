package net.sf.javascribe.patterns.java.handwritten;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.SOURCE)
public @interface WebServlet {

	String filterGroup() default "";
	String[] uri();
	String name();
	
}
