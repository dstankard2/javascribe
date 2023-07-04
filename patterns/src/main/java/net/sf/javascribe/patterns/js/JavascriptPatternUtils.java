package net.sf.javascribe.patterns.js;

import java.util.Set;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.javascript.types.JavascriptDataObjectType;

public class JavascriptPatternUtils {

	public static void addJavascriptTypeForJavaDataObjectType(JavaDataObjectType javaType,ProcessorContext ctx) throws JavascribeException {
		
		String name = javaType.getName();
		JavascriptDataObjectType jsType = new JavascriptDataObjectType(javaType.getName(),false,ctx);
		for(String a : javaType.getAttributeNames()) {
			String t = javaType.getAttributeType(a);
			jsType.addAttribute(a, t);
			JavaVariableType attrType = JavascribeUtils.getType(JavaVariableType.class, t, ctx);
			if (attrType instanceof JavaDataObjectType) {
				addJavascriptTypeForJavaDataObjectType((JavaDataObjectType)attrType,ctx);
			}
		}
		
		ctx.setLanguageSupport("Javascript");
		if (ctx.getVariableType(name)==null) {
			ctx.addVariableType(jsType);
		}

		ctx.setLanguageSupport("Java8");
	}

	public static AjaxClientProvider getAjaxClientProvider(String name,ProcessorContext ctx) {
		Set<Class<AjaxClientProvider>> classes = ctx.getApplicationContext().getPlugins(AjaxClientProvider.class);
		for(Class<AjaxClientProvider> cl : classes) {
			try {
				AjaxClientProvider val = cl.getConstructor().newInstance();
				if (val.getName().equals(name)) return val;
			} catch(Exception e) {
				ctx.getLog().error("Couldn't instantiate Ajax Client Provider "+cl.getCanonicalName());
				e.printStackTrace();
			}
		}
		return null;
	}

}
