package net.sf.javascribe.langsupport.java;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.exception.JavascribeException;

public class JavaInterfaceSourceFile extends JavaSourceFile<JavaInterfaceSource> {

	public JavaInterfaceSourceFile(ProcessorContext ctx) throws JavascribeException {
		super(JavaInterfaceSource.class, ctx);
	}

	public JavaInterfaceSourceFile(ProcessorContext ctx, JavaInterfaceSource copy) throws JavascribeException {
		super(JavaInterfaceSource.class, ctx);
		this.src = copy;
	}

	public SourceFile copy() {
		StringBuilder src = this.getSource();
		JavaInterfaceSource copy = Roaster.parse(JavaInterfaceSource.class, src.toString());
		try {
			return new JavaInterfaceSourceFile(ctx, copy);
		} catch(JavascribeException e) {
			// should be impossible
		}
		return null;
	}

}

