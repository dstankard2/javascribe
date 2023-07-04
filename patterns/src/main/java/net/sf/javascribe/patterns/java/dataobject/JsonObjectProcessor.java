package net.sf.javascribe.patterns.java.dataobject;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
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
		JavascriptDataObjectType newType = new JavascriptDataObjectType(name, false,ctx);
		
		ctx.setLanguageSupport("Javascript");
		for(String attr : javaType.getAttributeNames()) {
			String type = javaType.getAttributeType(attr);
			newType.addAttribute(attr, type);
		}
		ctx.addVariableType(newType);
	}

}

