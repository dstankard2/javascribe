package net.sf.javascribe.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;
import net.sf.javascribe.api.ProcessorContext;

public class JavascribeUtils {

	public static String getLowerCamelName(String typeName) {
		return Character.toLowerCase(typeName.charAt(0))+typeName.substring(1);
	}
	
	public static String getUpperCamelName(String typeName) {
		return Character.toUpperCase(typeName.charAt(0))+typeName.substring(1);
	}
	
	public static String getObjectName(String str) throws JavascribeException {
		int index = 0;
		String ret = null;
		
		if (str==null) throw new JavascribeException("Cannot find an object name in a null reference string");
		index = str.indexOf(".");
		if (index<0) throw new JavascribeException("Couldn't find object name in rule string '"+str+"'");
		ret = str.substring(0, index);
		return ret;
	}
	
	public static String getRuleName(String str) throws JavascribeException {
		int index = 0;
		String ret = null;
		
		if (str==null) throw new JavascribeException("Cannot find a rule name in a null reference string");
		if (index<0) throw new JavascribeException("Couldn't find rule name in rule string '"+str+"'");
		index = str.indexOf(".");
		ret = str.substring(index+1);
		return ret;
	}
	
	public static ValueExpression findParameterValue(String paramName,String paramTypeName,CodeExecutionContext execCtx,Map<String,String> explicitParams) throws JavascribeException {
		if ((explicitParams!=null) && (explicitParams.containsKey(paramName))) {
			return ExpressionUtil.buildValueExpression(explicitParams.get(paramName), paramTypeName, execCtx);
		}
		
		Map<String,AttributeHolder> toCheck = new HashMap<String,AttributeHolder>();
		List<String> vars = execCtx.getVariableNames();
		// Check variables
		for(String v : vars) {
			if (v.equals(paramName)) {
				return ExpressionUtil.buildValueExpression("${"+v+"}", paramTypeName, execCtx);
			}
			VariableType type = execCtx.getTypeForVariable(v);
			if (type instanceof AttributeHolder) {
				AttributeHolder holder = (AttributeHolder)type;
				toCheck.put(v, holder);
			}
		}
		for(String v : toCheck.keySet()) {
			AttributeHolder holder = toCheck.get(v);
			if (holder.getAttributeType(paramName)!=null) {
				if (holder.getAttributeType(paramName).equals(paramTypeName)) {
					return ExpressionUtil.buildValueExpression("${"+v+"."+paramName+"}", paramTypeName, execCtx);
				}
			}
		}

		return null;
	}
	
	public static HashMap<String,String> readParameters(ProcessorContext ctx,String paramString) throws JavascribeException {
		HashMap<String,String> ret = new HashMap<String,String>();
		String params[] = paramString.split(",");
		
		for(String p : params) {
			int i = p.indexOf("=");
			String name = p.substring(0, i);
			String value = p.substring(i+1);
			ret.put(name, value);
		}
		
		return ret;
	}
	
	public static List<Attribute> readAttributes(ProcessorContext ctx,String attribs) throws JavascribeException {
		List<Attribute> ret = new ArrayList<Attribute>();

		// Return empty list for empty string
		if (attribs==null) return ret;
		if (attribs.trim().length()==0) return ret;

		String[] attrs = attribs.trim().split(",");
		
		for(String att : attrs) {
			String attribName,attribType;
			int i = att.indexOf(':');
			if (i < 0) {
				attribName = att;
				attribType = ctx.getAttributeType(att);
				
				if (attribType==null) {
					if (att.endsWith("String")) attribType = "string";
					if (att.endsWith("Id")) attribType = "integer";
				}
				
				if (attribType==null) 
					throw new JavascribeException("Could not find type for attribute '"+attribName+"'");
			}
			else {
				attribName = att.substring(0, i);
				attribType = att.substring(i+1);
				if (ctx.getType(attribType)==null) {
					throw new JavascribeException("Found invalid attribute type '"+attribType+"' for attribute '"+attribName+"'");
				}
				if (ctx.getAttributeType(attribName)!=null) {
					if (!ctx.getAttributeType(attribName).equals(attribType)) {
						throw new JavascribeException("Attribute '"+attribName+"' has inconsistent type in system");
					}
				}
			}
			Attribute attr = new Attribute(attribName,attribType);
			ret.add(attr);
		}
		
		return ret;
	}
	
	public static String basicTemplating(String templateName,Map<String,String> replacements) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/"+templateName);
		InputStream in = null;

		if (url==null) {
			throw new JavascribeException("Couldn't load template '"+templateName+"'");
		}

		try {
			in = url.openStream();
			while(in.available()>0) {
				build.append((char)in.read());
			}
			basicTemplating(build,replacements);
		} catch(IOException e) {
			throw new JavascribeException("Couldn't perform templating",e);
		} finally {
			if (in!=null) try { in.close(); } catch(Exception e) { }
		}

		return build.toString();
	}

	public static void basicTemplating(StringBuilder value,Map<String,String> replacements) throws JavascribeException {
		int start = value.indexOf("${");

		while(start>=0) {
			int end = value.indexOf("}", start+1);
			if (end<0) {
				throw new JavascribeException("Found unterminated variable in template");
			}
			String name = value.substring(start+2, end);
			String v = replacements.get(name);
			if (v==null) {
				throw new JavascribeException("Didn't find replacement for template name '"+name+"'");
			}
			value.replace(start, end+1, v);
			start = value.indexOf("${");
		}
	}
	
}

