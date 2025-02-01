package net.sf.javascribe.patterns.java.dataobject;

import java.util.List;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.patterns.xml.java.dataobject.ApplyClassification;

@Plugin
public class ApplyClassificationProcessor implements ComponentProcessor<ApplyClassification> {

	@Override
	public void process(ApplyClassification comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		String cl = comp.getClassificationName();
		List<String> dos = comp.getObject();
		boolean autoApply = comp.getAutoApply();

		if (autoApply) {
			ctx.getLog().warn("Classification auto-apply is not currently supported");
		}
		JavaDataObjectType clType = JavascribeUtils.getType(JavaDataObjectType.class, cl, ctx);
		if (clType==null) {
			throw new JavascribeException("Cannot apply classification to object '"+cl+"' - type not found");
		}
		for(String dataObj : dos) {
			JavaDataObjectType ty = JavascribeUtils.getType(JavaDataObjectType.class, dataObj, ctx);
			if (ty==null) {
				throw new JavascribeException("Didn't find type '"+dataObj+"' to apply classification to");
			}
			for(String attr : clType.getAttributeNames()) {
				if (!clType.getAttributeType(attr).equals(ty.getAttributeType(attr))) {
					throw new JavascribeException("Could not apply classification '"+cl+"' to data object '"+dataObj+"' - data object does not have attribute '"+attr+"'");
				}
			}
			ty.getSuperTypes().add(clType.getName());
			JavaClassSourceFile src = JavaUtils.getClassSourceFile(ty.getImport(), ctx);
			src.getSrc().addInterface(clType.getImport());
		}
	}

}

