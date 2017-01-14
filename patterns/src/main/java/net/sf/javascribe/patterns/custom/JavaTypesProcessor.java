package net.sf.javascribe.patterns.custom;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.JavaVariableTypeImpl;
import net.sf.javascribe.patterns.xml.custom.JavaType;
import net.sf.javascribe.patterns.xml.custom.JavaTypes;

@Scannable
@Processor
public class JavaTypesProcessor {

	@ProcessorMethod(componentClass=JavaTypes.class)
	public void process(JavaTypes types,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		JavaVariableType type = null;
		
		System.out.println("Processing custom Java Types.");

		for(JavaType t : types.getJavaType()) {
			if ((t.getName().trim().length()==0) || (t.getIm().trim().length()==0) 
					|| (t.getClazz().trim().length()==0)) {
				throw new JavascribeException("Invalid JavaType specified - Attributes 'class', 'import' and 'name' are all required.");
			}
			type = new JavaVariableTypeImpl(t.getName(), JavaUtils.getPkg(t.getIm()), t.getClazz());
			ctx.getTypes().addType(type);
		}
	}

}
