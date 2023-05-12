package net.sf.javascribe.langsupport.java;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaEnumSource;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.exception.JasperException;

public class JavaEnumSourceFile extends JavaSourceFile<JavaEnumSource> {

	public JavaEnumSourceFile(ProcessorContext ctx) throws JasperException {
		super(JavaEnumSource.class,ctx);
	}
	
	public JavaEnumSourceFile(ProcessorContext ctx, JavaEnumSource copy) throws JasperException {
		super(JavaEnumSource.class, ctx);
		this.src = copy;
	}

	public SourceFile copy() {
		StringBuilder src = this.getSource();
		JavaEnumSource copy = Roaster.parse(JavaEnumSource.class, src.toString());
		try {
			return new JavaEnumSourceFile(ctx, copy);
		} catch(JasperException e) {
			// should be impossible
		}
		return null;
	}

}

