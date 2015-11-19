package net.sf.javascribe.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;

import org.apache.log4j.Logger;

/**
 * A set of utility methods that a component processor might benefit from.
 * @author DCS
 */
public class JavascribeUtils {

	private static final Logger log = Logger.getLogger(JavascribeUtils.class);

	/**
	 * returns true if the string is null or the trimmed length is 0.
	 * @param s String to test.
	 * @return true if the trimmed string is lngth 0.
	 */
	public static boolean isEmpty(String s) {
		if (s==null) return true;
		if (s.trim().length()==0) return true;
		return false;
	}
	
	/**
	 * Reurns the "natural" attribute name for the given type.  This is 
	 * generally the lower camel variation of the type name. If the type 
	 * name is "list/*" then the natural name is the lower camel name of 
	 * the list element type with "List" appended.
	 * @param typeName Name of an attribute type to get the attribute name 
	 * for.
	 * @return Natural attribute name.
	 */
	public static String getNaturalAttributeName(String typeName) {
		String ret = null;
		
		if (typeName.startsWith("list/")) {
			ret = getLowerCamelName(typeName.substring(5))+"List";
		} else {
			ret = getLowerCamelName(typeName);
		}
		
		return ret;
	}

	/**
	 * Assuming that the type name if a variable type name (generally 
	 * upper camel), returns the lower camel variation.
	 * @param typeName
	 * @return Lower camel variation of the typeName.
	 */
	public static String getLowerCamelName(String typeName) {
		return Character.toLowerCase(typeName.charAt(0))+typeName.substring(1);
	}
	
	/**
	 * Assuming that the attributeName is a variable name (generally 
	 * lower camel), returns the upper camel variation.
	 * @param attributeName 
	 * @return Upper camel version of the name.
	 */
	public static String getUpperCamelName(String attributeName) {
		return Character.toUpperCase(attributeName.charAt(0))+attributeName.substring(1);
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
			log.debug("Found value for parameter "+paramName+" as "+explicitParams.get(paramName));
			return ExpressionUtil.buildValueExpression(explicitParams.get(paramName), paramTypeName, execCtx);
		}
		
		Map<String,AttributeHolder> toCheck = new HashMap<String,AttributeHolder>();
		List<String> vars = execCtx.getVariableNames();
		// Check variables
		for(String v : vars) {
			if (v.equals(paramName)) {
				log.debug("Found param "+paramName+" in exec ctx");
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
					log.debug("Found parameter "+paramName+" in attribute holder "+v);
					return ExpressionUtil.buildValueExpression("${"+v+"."+paramName+"}", paramTypeName, execCtx);
				}
			}
		}

		return null;
	}
	
	public static HashMap<String,String> readParameters(ProcessorContext ctx,String paramString) throws JavascribeException {
		HashMap<String,String> ret = new HashMap<String,String>();
		String params[] = paramString.split(",");
		if (paramString.trim().length()==0) return ret;
		
		for(String p : params) {
			int i = p.indexOf("=");
			String name = p.substring(0, i);
			String value = p.substring(i+1);
			ret.put(name, value);
		}
		
		return ret;
	}

	/**
	 * Takes a comma-separated list of attribute declarations and returns 
	 * a list of name/type combinations.  For attributes that do not 
	 * explicitly have a type, the processor context will be queried for 
	 * the type of the attribute.  For attributes that explicitly have type, 
	 * the processor context will be queried to ensure that the declared 
	 * type is consistent with what has already been defined.
	 * 
	 * @param ctx
	 * @param attribs
	 * @return A list of attributes found in the specified string.
	 * @throws JavascribeException
	 */
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
				attribName = att.substring(0, i).trim();
				attribType = att.substring(i+1).trim();
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
	
	public static String basicTemplating(String templateName,Map<String,String> replacements,ProcessorContext ctx) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		InputStream in = null;

		try {
			in = ctx.getEngineProperties().getClasspathResource("META-INF/"+templateName);
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

