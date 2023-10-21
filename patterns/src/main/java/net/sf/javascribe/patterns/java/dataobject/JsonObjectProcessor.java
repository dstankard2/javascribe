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
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.javascript.types.JavascriptDataObjectType;
import net.sf.javascribe.patterns.xml.java.dataobject.JsonObject;

@Plugin
public class JsonObjectProcessor implements ComponentProcessor<JsonObject> {

	@Override
	public void process(JsonObject comp, ProcessorContext ctx) throws JavascribeException {
		String name = comp.getName();
		ctx.setLanguageSupport("Java8");
		
		if (name.trim().length()==0) {
			throw new JavascribeException("name attribute is required for JSON object");
		}

		JavaDataObjectType javaType = JavascribeUtils.getType(JavaDataObjectType.class, name, ctx);
		if (javaType==null) {
			throw new JavascribeException("Could not find type '"+name+"' to create a JSON type for it");
		}
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(javaType.getImport(), ctx);
		JavascriptDataObjectType newType = new JavascriptDataObjectType(name, false,ctx);
		
		ctx.setLanguageSupport("Javascript");
		for(String attr : javaType.getAttributeNames()) {
			String type = javaType.getAttributeType(attr);
			newType.addAttribute(attr, type);
			if (type.equals("datetime")) {
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
			}
		}
		ctx.addVariableType(newType);
	}

}

