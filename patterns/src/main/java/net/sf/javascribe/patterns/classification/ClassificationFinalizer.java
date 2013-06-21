package net.sf.javascribe.patterns.classification;

import java.util.List;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.java5.Java5DataObjectSourceFile;

/**
 * Once all the classifications have been read into the system, we want to go through the 
 * data objects in the system and classify them by applying the classification interfaces to 
 * the source files.  To make sure that a data object gets the most specific classification 
 * possible, go through the classification list in reverse order that they've been added.
 * @author Dave
 *
 */
@Scannable
@Processor
public class ClassificationFinalizer {
	GeneratorContext ctx = null;
	List<String> dataObjectNames = null;
	List<String> classificationNames = null;
	
	@ProcessorMethod(componentClass=ClassificationFinalizerComp.class)
	public void process(GeneratorContext ctx) throws JavascribeException {
		this.ctx = ctx;
		dataObjectNames = (List<String>)ctx.getObject(ClassificationProcessor.DATA_OBJECTS);
		classificationNames = (List<String>)ctx.getObject("ClassificationNames");
		System.out.println("Applying classification interfaces to Data Objects.");

		if (dataObjectNames!=null) {
			for(String s : dataObjectNames) {
				applyInterfaces(s);
			}
		}
		
	}
	
	private void applyInterfaces(String typeName) throws JavascribeException {
		JavaBeanType beanType = (JavaBeanType)ctx.getTypes().getType(typeName);
		Java5DataObjectSourceFile src = (Java5DataObjectSourceFile)JsomUtils.getJavaFile(beanType.getImport(), ctx);

//		System.out.println("Applying classifications to "+typeName);
		// Iterate through the classification names backwards.
		for(String s : classificationNames) {
//		for(int i=classificationNames.size()-1;i>=0;i--) {
//			String s = classificationNames.get(i);
//			System.out.println("Testing classification "+s);
			JavaBeanType classificationType = (JavaBeanType)ctx.getTypes().getType(s);
			if (fitsClassification(beanType,classificationType)) {
//				System.out.println("Applying interface '"+s+"' to bean '"+src.getPublicClass().getClassName()+"'");
				src.getPublicClass().addImplementedInterface(classificationType.getImport());
				break;
			}
		}
	}
	
	// Does obj have all the attributes of cl?
	private boolean fitsClassification(JavaBeanType obj,JavaBeanType cl) {
		
		for(String attr : cl.getAttributeNames()) {
			if (obj.getAttributeType(attr)==null) return false;
		}
		
		return true;
	}

}

