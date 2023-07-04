package net.sf.javascribe.patterns.java.dataobject;

import org.jboss.forge.roaster.model.source.JavaEnumSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaEnumSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaEnumType;
import net.sf.javascribe.patterns.xml.java.dataobject.OrdinalEnum;

@Plugin
public class OrdinalEnumProcessor implements ComponentProcessor<OrdinalEnum> {

	@Override
	public void process(OrdinalEnum comp, ProcessorContext ctx) throws JavascribeException {
		String ref = comp.getRef();
		ctx.setLanguageSupport("Java8");
		JavaUtils.getJavaPackage(comp, ctx);
		String className = comp.getName();
		String pkg = JavaUtils.getJavaPackage(comp, ctx);
		JavaEnumSourceFile file = new JavaEnumSourceFile(ctx);
		file.getSrc().setPackage(pkg);
		file.getSrc().setName(className);
		ctx.addSourceFile(file);
		JavaEnumSource e = file.getSrc();

		for (String value : comp.getValue()) {
			e.addEnumConstant(value);
		}

		JavaEnumType en = new JavaEnumType(className, pkg, ctx.getBuildContext());
		ctx.addVariableType(en);

		if (ref.trim().length() > 0) {
			if (ctx.getSystemAttribute(ref) != null) {
				throw new JavascribeException("ref '" + ref + "' is already a system attribute");
			}
		}
		ctx.addSystemAttribute(ref, en.getName());
	}

}
