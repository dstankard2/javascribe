package net.sf.javascribe.langsupport.java;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.exception.JavascribeException;

public class JavaClassSourceFile extends JavaSourceFile<JavaClassSource> {

	public JavaClassSourceFile(ProcessorContext ctx) throws JavascribeException {
		super(JavaClassSource.class, ctx);
	}

	public JavaClassSourceFile(ProcessorContext ctx, JavaClassSource copy) throws JavascribeException {
		super(JavaClassSource.class, ctx);
		this.src = copy;
	}

	public SourceFile copy() {
		StringBuilder src = this.getSource();
		JavaClassSource copy = Roaster.parse(JavaClassSource.class, src.toString());
		try {
			return new JavaClassSourceFile(ctx, copy);
		} catch(JavascribeException e) {
			// should be impossible
		}
		return null;
	}

}

