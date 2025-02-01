package net.sf.javascribe.patterns.java.dataobject;

import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.javascript.types.JavascriptDataObjectType;
import net.sf.javascribe.patterns.http.WebUtils;
import net.sf.javascribe.patterns.xml.java.dataobject.JsonObject;

@Plugin
public class JsonObjectProcessor implements ComponentProcessor<JsonObject> {

	protected void createNewType(String name, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		
		JavaVariableType type = JavascribeUtils.getType(JavaVariableType.class, name, ctx);
		
		if (type!=null) {
			return;
		}
		if (!(type instanceof JavaDataObjectType)) {
			return;
		}

		JavaDataObjectType javaType = (JavaDataObjectType)type;
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(javaType.getImport(), ctx);
		JavascriptDataObjectType newType = new JavascriptDataObjectType(name, false,ctx);
		
		ctx.setLanguageSupport("Javascript");
		
		// This has already been added
		if (ctx.getVariableType(name)!=null) {
			return;
		}
		
		if (ctx.getVariableType(name) == null) {
			for(String attr : javaType.getAttributeNames()) {
				String propertyTypeName = javaType.getAttributeType(attr);
				newType.addAttribute(attr, propertyTypeName);

				// Add any nested types first
				if (propertyTypeName.startsWith("list/")) {
					createNewType(propertyTypeName.substring(5), ctx);
				} else {
					createNewType(propertyTypeName, ctx);
				}
				/*
				JavaVariableType propertyType = JavascribeUtils.getType(JavaVariableType.class, propertyTypeName, ctx);
				if (propertyType instanceof JavaDataObjectType) {
					createNewType(propertyTypeName, ctx);
				} else if (propertyTypeName.startsWith("list/")) {
					createNewType(propertyTypeName.substring(5), ctx);
				}
				*/
				
				if (propertyTypeName.equals("datetime")) {
					// The LocalDateTime property needs to have a Json Serializer
					FieldSource field = src.getSrc().getField(attr);
					src.getSrc().addImport("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
					src.getSrc().addImport("com.fasterxml.jackson.databind.annotation.JsonSerialize");
					src.getSrc().addImport("com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer");
					src.getSrc().addImport("com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer");
					src.getSrc().addImport("com.fasterxml.jackson.annotation.JsonFormat");
					AnnotationSource an = field.addAnnotation("JsonDeserialize");
					an.setClassValue("using", LocalDateTimeDeserializer.class);
					an = field.addAnnotation("JsonSerialize");
					an.setClassValue("using", LocalDateTimeSerializer.class);
					ctx.getBuildContext().addDependency("jackson-datatype-jsr310");
					
					an = field.addAnnotation(JsonFormat.class);
					an.setEnumValue("shape", JsonFormat.Shape.STRING);
					an.setStringValue("pattern", "MM-dd-yyyy hh:mm:ss");
				} else if (propertyTypeName.equals("date")) {
					FieldSource field = src.getSrc().getField(attr);
					src.getSrc().addImport("com.fasterxml.jackson.annotation.JsonFormat");

					AnnotationSource an = field.addAnnotation("JsonFormat");
					an.setEnumValue("shape", JsonFormat.Shape.STRING);
					an.setStringValue("pattern", "yyyy-MM-dd");

					ctx.getBuildContext().addDependency("jackson-datatype-jsr310");
				}
				
			}
			ctx.addVariableType(newType);
		}
	}

	@Override
	public void process(JsonObject comp, ProcessorContext ctx) throws JavascribeException {
		String name = comp.getName();
		
		if (name.trim().length()==0) {
			throw new JavascribeException("name attribute is required for JSON object");
		}

		WebUtils.ensureJavascriptDataObjectType(name, ctx);
	}

}

