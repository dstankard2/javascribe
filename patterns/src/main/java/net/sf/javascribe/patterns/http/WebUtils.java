package net.sf.javascribe.patterns.http;

import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.javascript.types.JavascriptDataObjectType;
import net.sf.javascribe.patterns.java.http.JavaWebUtils;

public class WebUtils {

	private static final String WEBSERVICE_DEFINITIONS = "PATTERNS_HTTP_ENDPOINTS";

	public static WebServiceModule getWebServiceDefinition(String buildId, String module,ProcessorContext ctx,boolean force) throws JavascribeException {
		WebServiceModule ret = null;

		String objectName = WEBSERVICE_DEFINITIONS+"_" + buildId+"_"+module;
		ret = (WebServiceModule)ctx.getObject(objectName);
		if ((ret==null) && (force)) {
			ret = new WebServiceModule();
			ctx.setObject(objectName, ret);
		}
		
		// We need to have a Java Webapp platform
		// TODO: It doesn't have to be Tomcat
		JavaWebUtils.ensureWebPlatform(ctx, buildId);

		return ret;
	}

	// Ensures that data object types exist for Javascript.  Will not check other types
	public static void ensureJavascriptDataObjectType(String typeName, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");

		if (typeName.startsWith("list/")) {
			ensureJavascriptDataObjectType(typeName.substring(5), ctx);
		}
		
		JavaVariableType type = JavascribeUtils.getType(JavaVariableType.class, typeName, ctx);

		ctx.setLanguageSupport("Javascript");
		// This has already been added
		if (ctx.getVariableType(typeName)!=null) {
			return;
		}

		if (type instanceof JavaDataObjectType) {
			JavaDataObjectType javaType = (JavaDataObjectType)type;
			JavaClassSourceFile src = JavaUtils.getClassSourceFile(javaType.getImport(), ctx);
			JavascriptDataObjectType newType = new JavascriptDataObjectType(typeName, false,ctx);
			
			for(String attr : javaType.getAttributeNames()) {
				String propertyTypeName = javaType.getAttributeType(attr);
				newType.addAttribute(attr, propertyTypeName);

				// Add any nested types first
				if (propertyTypeName.startsWith("list/")) {
					ensureJavascriptDataObjectType(propertyTypeName.substring(5), ctx);
				} else {
					ensureJavascriptDataObjectType(propertyTypeName, ctx);
				}
				
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

}

