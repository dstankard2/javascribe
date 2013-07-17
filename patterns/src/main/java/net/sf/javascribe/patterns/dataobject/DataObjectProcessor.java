package net.sf.javascribe.patterns.dataobject;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaBeanType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DataObjectSourceFile;

@Scannable
@Processor
public class DataObjectProcessor {

	@ProcessorMethod(componentClass=DataObject.class)
	public void process(DataObject comp,ProcessorContext ctx) throws JavascribeException {
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DataObject.DATA_OBJECT_PKG));
		JavaBeanType type = null;
		Java5DataObjectSourceFile src = null;
		
		ctx.setLanguageSupport("Java");

		if (comp.getName()==null)
			throw new JavascribeException("Attribute 'name' is required for DomainData");
		if (comp.getAttributes()==null)
			throw new JavascribeException("Attribute 'attributes' is required for DomainData");

		System.out.println("Processing domain data object '"+comp.getName()+"'");

		try {
			type = new JsomJavaBeanType(comp.getName(),pkg,comp.getName());
			src = new Java5DataObjectSourceFile(new JavascribeVariableTypeResolver(ctx.getTypes()));
			src.setPackageName(pkg);
			src.getPublicClass().setClassName(comp.getName());
			if (ctx.getTypes().getType(type.getName())!=null) {
				System.out.println("Replacing type "+type.getName());
				ctx.getTypes().addOrReplaceType(type);
			} else {
				ctx.getTypes().addType(type);
			}
			JsomUtils.addJavaFile(src, ctx);

			// Default Constructor
			Java5CodeSnippet constCode = new Java5CodeSnippet();

			Java5ClassConstructor con = JsomUtils.createConstructor(src, ctx);
			con.setMethodBody(constCode);
			src.getPublicClass().addMethod(con);

			// Constructor
			con = JsomUtils.createConstructor(src, ctx);
			src.getPublicClass().addMethod(con);
			constCode = new Java5CodeSnippet();
			con.setMethodBody(constCode);

			List<Attribute> attrs = JavascribeUtils.readAttributes(ctx, comp.getAttributes());
			for(Attribute att : attrs) {
				src.addJavaBeanProperty(att.getName(), att.getType());
				type.addAttribute(att.getName(), att.getType());
				con.addArg(att.getType(), att.getName());
				constCode.append("this."+att.getName()+" = "+att.getName()+";\n");
				if (ctx.getAttributeType(att.getName())==null) {
					ctx.addAttribute(att.getName(), att.getType());
				}
			}

			// Add attribute to generator context
			String attribName = JavascribeUtils.getLowerCamelName(comp.getName());
			ctx.addAttribute(attribName, comp.getName());
			ctx.addAttribute(attribName+"List", "list/"+comp.getName());
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while processing data object",e);
		}
	}

}

