package net.sf.javascribe.patterns.java.dataobject;

import java.util.List;

import org.jboss.forge.roaster.model.source.MethodSource;

import net.sf.javascribe.api.AttribEntry;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.patterns.xml.java.dataobject.DataObject;

@Plugin
public class DataObjectProcessor implements ComponentProcessor<DataObject> {

	DataObject comp = null;
	ProcessorContext ctx = null;
	
	@Override
	public void process(DataObject comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		JavaClassSourceFile src = new JavaClassSourceFile(ctx);
		String name = comp.getName();
		String lowerCamel = JavascribeUtils.getLowerCamelName(name);
		String multiple = JavascribeUtils.getMultiple(lowerCamel);
		String pkg = comp.getJavaRootPackage()+'.'+comp.getPkg();
		String props = comp.getProperties();
		JavaDataObjectType newType = new JavaDataObjectType(name,pkg+'.'+name,ctx.getBuildContext());

		ctx.addVariableType(newType);
		ctx.addSystemAttribute(lowerCamel, name);
		ctx.addSystemAttribute(lowerCamel+"List", "list/"+name);
		ctx.addSystemAttribute(multiple, "list/"+name);

		List<AttribEntry> attribs = JavascribeUtils.readParametersAsList(props, ctx);

		if (name.equals(lowerCamel)) {
			throw new JavascribeException("Type name '"+name+"' is not a valid name for a data object");
		}
		
		else if (attribs.size()==0) {
			throw new JavascribeException("Data Object "+name+" has no attributes");
		}

		src.getSrc().setName(name);
		src.getSrc().setPackage(pkg);

		src.getSrc().addMethod().setConstructor(true).setBody("").setPublic();

		MethodSource<?> constructor = src.getSrc().addMethod().setConstructor(true).setPublic();
		JavaCode constructorCode = new JavaCode();

		for(AttribEntry a : attribs) {
			String n = a.getName();
			JavaVariableType type = (JavaVariableType)a.getType();
			src.addImport(type);
			src.getSrc().addProperty(type.getClassName(), n);
			newType.addProperty(n, type.getName());
			constructorCode.appendCodeText("this."+n+" = "+n+";\n");
			constructor.addParameter(type.getClassName(), n);
		}

		if (comp.getExtend().trim().length()>0) {
			String ex = comp.getExtend();
			JavaDataObjectType exType = JavascribeUtils.getType(JavaDataObjectType.class, ex, ctx);
			for(String a : exType.getAttributeNames()) {
				String aTypeName = exType.getAttributeType(a);
				JavaVariableType aType = JavascribeUtils.getType(JavaVariableType.class, aTypeName, ctx);
				if (newType.getAttributeType(a)!=null) {
					if (!newType.getAttributeType(a).equals(aTypeName)) {
						throw new JavascribeException("Found inconsistent types for system attribute '"+a+"'");
					}
				} else {
					String upperCamel = JavascribeUtils.getUpperCamelName(a);
					constructor.addParameter(aType.getClassName(), a);
					src.addImport(aType);
					//constructor.addParameter(aType.getImport(), a);
					constructorCode.appendCodeText("super.set"+upperCamel+"("+a+");\n");
					newType.addProperty(a, aTypeName);
				}
			}
			newType.getSuperTypes().add(ex);
			if (exType.getIsInterface()) {
				src.getSrc().addInterface(exType.getImport());
			} else {
				src.getSrc().setSuperType(exType.getImport());
			}
		}
		
		constructor.setBody(constructorCode.getCodeText());
		
		ctx.addSourceFile(src);
	}

}
