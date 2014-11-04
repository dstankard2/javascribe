package net.sf.javascribe.patterns.dataobject;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.java5.Java5Annotation;
import net.sf.jsom.java5.Java5MemberDeclaration;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class JsonObjectProcessor {

	@ProcessorMethod(componentClass=JsonObject.class)
	public void processJsonObject(JsonObject comp,ProcessorContext ctx) throws JavascribeException {
		String name = comp.getName();
		
		ctx.setLanguageSupport("Java");
		VariableType type = ctx.getType(name);
		if (type==null) {
			throw new JavascribeException("Could not find a type called '"+name+"'");
		}
		if (!(type instanceof AttributeHolder)) {
			throw new JavascribeException("Type '"+name+"' is not a data object");
		}
		if (!(type instanceof JavaVariableType)) {
			throw new JavascribeException("Type '"+name+"' is not a Java type");
		}
		
		AttributeHolder holder = (AttributeHolder)type;
		
		applyJsonAnnotations(holder,ctx);
		
	}
	
	protected void applyJsonAnnotations(AttributeHolder type,ProcessorContext ctx) throws JavascribeException {
		List<String> jsonAnnotatedTypes = null;
		
		jsonAnnotatedTypes = (List<String>)ctx.getObject("JSON_TYPES");
		if (jsonAnnotatedTypes==null) {
			jsonAnnotatedTypes = new ArrayList<String>();
			ctx.putObject("JSON_TYPES", jsonAnnotatedTypes);
		}
		JavaVariableType javaType = (JavaVariableType)type;
		Java5SourceFile file = JsomUtils.getJavaFile(javaType.getImport(), ctx);

		if (jsonAnnotatedTypes.contains(type.getName())) {
			return;
		}
		jsonAnnotatedTypes.add(type.getName());
		
		// Attempt to annotate this type with Jackson

		if (file==null) { // This file is not part of the codebase.  Ignore.
			return;
		}

		for(String a : type.getAttributeNames()) {
			Java5MemberDeclaration dec = file.getPublicClass().getVariableDeclaration(a);
			Java5Annotation an = new Java5Annotation("org.codehaus.jackson.annotate.JsonProperty");
			dec.getAnnotations().add(an);
			VariableType attrType = ctx.getType(type.getAttributeType(a));
			if (attrType instanceof AttributeHolder) {
				applyJsonAnnotations((AttributeHolder)attrType,ctx);
			}
		}
	}
	
}

