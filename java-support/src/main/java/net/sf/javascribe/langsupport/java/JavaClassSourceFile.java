package net.sf.javascribe.langsupport.java;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public class JavaClassSourceFile extends JavaSourceFile<JavaClassSource> {

	public JavaClassSourceFile(ProcessorContext ctx) throws JavascribeException {
		super(JavaClassSource.class, ctx);
	}

	public JavaClassSourceFile(ProcessorContext ctx, JavaClassSource copy) throws JavascribeException {
		super(JavaClassSource.class, ctx);
		this.src = copy;
	}

}

