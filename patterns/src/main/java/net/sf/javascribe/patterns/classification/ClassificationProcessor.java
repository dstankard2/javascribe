package net.sf.javascribe.patterns.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaBeanType;
import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.patterns.dataobject.DataObject;
import net.sf.javascribe.patterns.model.DatabaseTable;
import net.sf.javascribe.patterns.model.EntityManagerComponent;
import net.sf.javascribe.patterns.model.EntityManagerUtils;

@Scannable
@Processor
public class ClassificationProcessor {

	public static final String DATA_OBJECTS = "Classification_DataObjects";

	@ProcessorMethod(componentClass=DataObject.class)
	public void processDataObject(DataObject comp,GeneratorContext ctx) {
		List<String> l = null;

		l = (List<String>)ctx.getObject(DATA_OBJECTS);
		if (l==null) {
			l = new ArrayList<String>();
			ctx.addObject(DATA_OBJECTS, l);
		}
		
		l.add(comp.getName());
	}
	
	@ProcessorMethod(componentClass=EntityManagerComponent.class)
	public void processDataObject(EntityManagerComponent comp,GeneratorContext ctx) throws JavascribeException {
		List<String> l = null;

		l = (List<String>)ctx.getObject(DATA_OBJECTS);
		if (l==null) {
			l = new ArrayList<String>();
			ctx.addObject(DATA_OBJECTS, l);
		}
		
		List<DatabaseTable> tables = EntityManagerUtils.readTables(comp, ctx);
		for(DatabaseTable t : tables) {
			String s = EntityManagerUtils.getEntityName(t.getName(), ctx, comp);
			l.add(s);
		}
	}
	
	@ProcessorMethod(componentClass=Classification.class)
	public void process(Classification classification,GeneratorContext ctx) throws JavascribeException {

		JavaBeanType type = null;
		Map<String,Classification> classifications = null;
		String className = null;
		String pkg = null;
		
		System.out.println("Processing classification '"+classification.getName()+"'");

		pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty("classification.pkg"));
		className = classification.getName();

		classifications = (Map<String,Classification>)ctx.getObject("Classifications");
		
		if (classifications==null) {
			ctx.addComponent(new ClassificationFinalizerComp());
			ctx.addComponent(new ClassificationAttributeProcessingComp());
			classifications = new HashMap<String,Classification>();
			ctx.addObject("Classifications", classifications);
		}
		
		if (classification.getName()==null) {
			throw new JavascribeException("Found a classification with no name");
		}
		if (ctx.getTypes().getType(className)!=null) {
			throw new JavascribeException("Illegal Classification: Found a type already in the system");
		}
		if (classification.getAttributes()==null) {
			throw new JavascribeException("Attribute 'attributes' is required on classification");
		}

		classifications.put(classification.getName(),classification);

		type = new JsomJavaBeanType(className,pkg,className);
		
		ctx.getTypes().addType(type);
		ctx.addAttribute(JavascribeUtils.getLowerCamelName(className), className);
		ctx.addAttribute(JavascribeUtils.getLowerCamelName(className)+"List", "list/"+className);
	}

}

class ClassificationFinalizerComp extends ComponentBase {
	
	public int getPriority() { return CorePatternConstants.PRIORITY_CLASSIFICATION_FINALIZER; }

}

class ClassificationAttributeProcessingComp extends ComponentBase {
	
	public int getPriority() { return CorePatternConstants.PRIORITY_CLASSIFICATION+1; }

}

