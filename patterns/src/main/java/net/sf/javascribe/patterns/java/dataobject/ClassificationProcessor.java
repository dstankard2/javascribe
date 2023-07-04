package net.sf.javascribe.patterns.java.dataobject;

import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaInterfaceSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.patterns.xml.java.dataobject.Classification;

@Plugin
public class ClassificationProcessor implements ComponentProcessor<Classification> {

	@Override
	public void process(Classification comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		JavaInterfaceSourceFile file = new JavaInterfaceSourceFile(ctx);
		String pkg = JavaUtils.getJavaPackage(comp, ctx);
		String className = comp.getName();
		JavaInterfaceSource src = file.getSrc();
		src.setName(className);
		src.setPackage(pkg);
		JavaDataObjectType type = new JavaDataObjectType(className, pkg+'.'+className, ctx.getBuildContext());

		type.setIsInterface(true);

		String lowerCamel = JavascribeUtils.getLowerCamelName(className);
		String multi = JavascribeUtils.getMultiple(lowerCamel);
		
		ctx.addSystemAttribute(lowerCamel, className);
		ctx.addSystemAttribute(multi, "list/"+className);
		ctx.addSystemAttribute(lowerCamel+"List", "list/"+className);
		
		if (comp.getExtend().trim().length()>0) {
			String exs[] = comp.getExtend().split(",");
			for(String ex : exs) {
				JavaDataObjectType exType = JavascribeUtils.getType(JavaDataObjectType.class, ex, ctx);
				if (!exType.getIsInterface()) {
					throw new JavascribeException("A classification may only extend another classification");
				}
				src.addInterface(exType.getImport());
				for(String n : exType.getAttributeNames()) {
					if (type.getAttributeType(n)==null) {
						String t = exType.getAttributeType(n);
						type.addProperty(n, t);
					}
				}
			}
		}
		
		if (comp.getAttributes().trim().length()==0) {
			throw new JavascribeException("Classification must define one or more attributes");
		}
		String[] attribs = comp.getAttributes().split(",");
		for(String attr : attribs) {
			String typeName = null;
			if (attr.indexOf(':')>0) {
				int i = attr.indexOf(':');
				typeName = attr.substring(i+1);
				attr = attr.substring(0, i);
				String existingType = ctx.getSystemAttribute(attr);
				if ((existingType!=null) && (!existingType.equals(typeName))) {
					throw new JavascribeException("Found inconsistent types for system attribute '"+attr+"'");
				}
				ctx.addSystemAttribute(attr, typeName);
			} else {
				typeName = ctx.getSystemAttribute(attr);
				if (typeName==null) {
					throw new JavascribeException("Couldn't find type for classification attribute '"+attr+"'");
				}
			}
			JavaVariableType attrType = JavascribeUtils.getType(JavaVariableType.class, typeName, ctx);
			String propertyClass = null;
			if (typeName.startsWith("list")) {
				propertyClass = JavaUtils.getClassDisplayForList(typeName, ctx);
			} else {
				propertyClass = attrType.getClassName();
			}
			String upperCamel = JavascribeUtils.getUpperCamelName(attr);
			file.addImport(attrType);
			src.addMethod().setName("set"+upperCamel).setPublic().addParameter(propertyClass, attr);
			src.addMethod().setName("get"+upperCamel).setPublic().setReturnType(propertyClass);
			file.addImport(attrType);
		}
		
		ctx.addSourceFile(file);
		ctx.addVariableType(type);
	}

	
}
