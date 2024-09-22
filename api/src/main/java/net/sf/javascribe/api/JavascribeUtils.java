package net.sf.javascribe.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.DataObjectType;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.api.types.ServiceType;
import net.sf.javascribe.api.types.VariableType;

/**
 * General utilities for use by pattern processors
 * @author DCS
 *
 */
public class JavascribeUtils {

	/**
	 * Returns the "natural" attribute name for the given type.  This is 
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
	
	public static boolean isLowerCamel(String str) {
		return getLowerCamelName(str).equals(str);
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
	
	public static boolean isUpperCamelName(String str) {
		return getUpperCamelName(str).equals(str);
	}
	
	public static String getSingle(String name) {
		String ret = null;
		
		if (name.endsWith("List")) {
			ret = name.substring(0, name.length()-4);
		} else if (name.endsWith("ies")) {
			ret = name.substring(0, name.length()-3);
		} else if (name.endsWith("s")) {
			ret = name.substring(0, name.length()-1);
		}
		
		return ret;
	}

	public static String getMultiple(String name) {
		String ret = null;
		if (name.endsWith("y")) {
			ret = name.substring(0, name.length()-2)+"ies";
		} else {
			ret = name + 's';
		}
		return ret;
	}
	
	/**
	 * Read a list of comma-separated parameters.  Each parameter can be of format "name" or "name:type" where name is a system attribute.
	 * The types returned should not be modifying without first informing the ProcessorContext
	 * @param paramString
	 * @param ctx
	 * @return
	 * @throws JavascribeException
	 */
	public static List<PropertyEntry> readParametersAsList(String paramString,ProcessorContext ctx) throws JavascribeException {
		List<PropertyEntry> ret = new ArrayList<>();
		
		if (paramString==null) {
			return ret;
		}

		paramString = paramString.trim();
		if (paramString.length()==0) return ret;
		
		String[] parts = paramString.split(",");
		for(String part : parts) {
			part = part.trim();
			int index = part.indexOf(':');

			if (index>0) {
				String name = part.substring(0, index).trim();
				String typeName = part.substring(index+1).trim();
				VariableType type = ctx.getVariableType(typeName);
				if (type==null) {
					throw new JavascribeException("Found no variable type named '"+typeName+"'");
				}
				if (typeName.startsWith("list/")) {
					ListType listType = (ListType)type;
					String elementTypeName = typeName.substring(5);
					VariableType elementType = getType(VariableType.class, elementTypeName, ctx);
					type = listType.getListTypeWithElementTypName(elementType);
				}
				ctx.addSystemAttribute(name, typeName);
				ret.add(new PropertyEntry(name,type,true));
			} else if (index==0) {
				throw new JavascribeException("Found invalid attribute name '"+part+"'");
			} else {
				String typeName = ctx.getSystemAttribute(part.trim());
				if (typeName==null) {
					throw new JavascribeException("Couldn't find type for property '"+part+"'");
				}
				VariableType type = JavascribeUtils.getType(VariableType.class, typeName, ctx);
				if (type==null) {
					throw new JavascribeException("Found no variable type named '"+typeName+"'");
				}
				if ((type instanceof ListType) && (typeName.startsWith("list/"))) {
					String elementTypeName = typeName.substring(5);
					VariableType elementType = getType(VariableType.class, elementTypeName, ctx);
					ListType listType = (ListType)type;
					type = listType.getListTypeWithElementTypName(elementType);
				}
				ret.add(new PropertyEntry(part,type,false));
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends VariableType> T getType(Class<T> cl, String name,ProcessorContext ctx) throws JavascribeException {
		T ret = null;
		VariableType type = null;
		
		if (name==null) {
			throw new JavascribeException("Couldn't find type with null name");
		}
		if (ctx==null) {
			throw new JavascribeException("Tried to find type '"+name+"' but got null ProcessorContext");
		}
		type = ctx.getVariableType(name);
		if (type!=null) {
			if (cl.isAssignableFrom(type.getClass())) {
				ret = (T)type;
			} else {
				throw new JavascribeException("Variable Type '"+name+"' was not of type '"+cl.getCanonicalName()+"'");
			}
			if ((ret instanceof ListType) && (name.startsWith("list/"))) {
				String elementTypeName = name.substring(5);
				ListType listType = (ListType)ret;
				VariableType elementType = getType(VariableType.class,elementTypeName,ctx);
				ret = (T)listType.getListTypeWithElementTypName(elementType);
			}
			
		}
		/*
		if (type==null) {
			return null;
			//throw new JavascribeException("Could not find type '"+name+"' for current language");
		}
		*/
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends VariableType> T getTypeForSystemAttribute(Class<T> cl, String name,ProcessorContext ctx) throws JavascribeException {
		T ret = null;
		VariableType type = null;
		String typeName = ctx.getSystemAttribute(name);
		
		if (typeName==null) throw new JavascribeException("Could not find system attribute '"+name+"'");
		
		type = ctx.getVariableType(typeName);
		if (type==null) throw new JavascribeException("Couldn't find type '"+typeName+"'");
		if (cl.isAssignableFrom(type.getClass())) {
			ret = (T)type;
		} else {
			throw new JavascribeException("Variable Type '"+name+"' was not of type '"+cl.getCanonicalName()+"'");
		}
		
		return ret;
	}
	
	public static HashMap<String,String> readParametersAsMap(String paramString,ProcessorContext ctx) throws JavascribeException {
		HashMap<String,String> ret = new HashMap<>();
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
	
	public static String getObjectName(String ref) throws JavascribeException {
		String ret = null;
		int i = ref.indexOf('.');
		if (i<=0) {
			throw new JavascribeException("Couldn't extract object name from reference '"+ref+"'");
		}
		ret = ref.substring(0, i);
		return ret;
	}

	public static String getRuleName(String ref) throws JavascribeException {
		String ret = null;
		int i = ref.indexOf('.');
		if (i<=0) {
			throw new JavascribeException("Couldn't extract rule name from reference '"+ref+"'");
		}
		ret = ref.substring(i+1);
		return ret;
	}

	public static String evaluateReference(String ref,CodeExecutionContext execCtx) throws JavascribeException {
		String ret = null;
		
		String parts[] = ref.split("\\.");
		if (parts.length==1) {
			ret = ref;
		} else {
			DataObjectType parentType = null;
			String parentTypeName = null;
			for(String part : parts) {
				if (ret==null) {
					ret = part;
					parentTypeName = execCtx.getVariableType(part);
					parentType = execCtx.getType(DataObjectType.class, parentTypeName);
				} else {
					if (parentType.getAttributeType(part)==null) {
						throw new JavascribeException("Couldn't evaluate reference '"+ref+"' - type '"+parentTypeName+"' didn't have an attribute called '"+part+"'");
					}
					ret = parentType.getCodeToRetrieveAttribute(ret, part, null, execCtx);
				}
			}
		}
		
		return ret;
	}
	
	public static List<ServiceOperation> findRuleFromTypeAndRef(String ruleRef,ProcessorContext ctx) throws JavascribeException {
		List<ServiceOperation> ret = new ArrayList<>();
		int i = ruleRef.indexOf('.');
		
		if (i<0) {
			throw new JavascribeException("Reference '"+ruleRef+"' does not refer to a service and a rule");
		}
		String ref = ruleRef.substring(0, i);
		String ruleName = ruleRef.substring(i+1);
		
		ServiceType type = JavascribeUtils.getType(ServiceType.class, ref, ctx);
		for(ServiceOperation op : type.getOperations()) {
			if (op.getName().equals(ruleName)) {
				ret.add(op);
			}
		}
		
		return ret;
	}
	
	public static List<ServiceOperation> findRuleFromRef(String ruleRef,ProcessorContext ctx) throws JavascribeException {
		List<ServiceOperation> ret = new ArrayList<>();
		int i = ruleRef.indexOf('.');
		
		if (i<1) {
			throw new JavascribeException("Reference '"+ruleRef+"' does not refer to a service and a rule");
		}
		String ref = ruleRef.substring(0, i);
		String ruleName = ruleRef.substring(i+1);
		
		ServiceType type = JavascribeUtils.getTypeForSystemAttribute(ServiceType.class, ref, ctx);
		for(ServiceOperation op : type.getOperations()) {
			if (op.getName().equals(ruleName)) {
				ret.add(op);
			}
		}
		
		return ret;
	}
	
	/* TODO: Fix this file.  Determine what to do about type dependencies vs originators

	public static String getTypeForRef(String ref,ProcessorContext ctx) throws JavascribeException {
		String ret = null;
		String parts[] = ref.split("\\.");
		
		if (parts.length==1) {
			ret = ctx.getSystemAttribute(parts[0]);
		} else {
			DataObjectType parentType = null;
			String parentTypeName = null;
			for(String part : parts) {
				if (ret==null) {
					ret = part;
					parentTypeName = ctx.getSystemAttribute(part);
					parentType = JavascribeUtils.getType(DataObjectType.class, parentTypeName, ctx);
					//parentType = execCtx.getType(DataObjectType.class, parentTypeName);
				} else {
					if (parentType.getAttributeType(part)==null) {
						throw new JavascribeException("Couldn't evaluate reference '"+ref+"' - type '"+parentTypeName+"' didn't have an attribute called '"+part+"'");
					}
					ret = parentType.getAttributeType(part);
				}
			}
		}
		
		return ret;
	}

	/**
	 * Does the data object type have the super
	 * @param type
	 * @param superTypeName
	 * @param ctx
	 * @return
	 * @throws JavascribeException
	 //
	public static boolean isSubclass(DataObjectType type,String superTypeName,ProcessorContext ctx) throws JavascribeException {
		boolean ret = false;
		
		while(type.getSuperTypes().size()>0) {
			List<String> typeNames = type.getSuperTypes();
			if (typeNames.contains(superTypeName)) {
				return true;
			}
			for(String typeName : typeNames) {
				DataObjectType s = JavascribeUtils.getType(DataObjectType.class, typeName, ctx, true);
				if (isSubclass(s, superTypeName,ctx)) {
					return true;
				}
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends SourceFile> T getSourceFile(Class<T> sourceFileClass, String path,ProcessorContext ctx) throws JavascribeException {
		T ret = null;
		
		if (ctx==null) {
			throw new JavascribeException("Tried to find source file '"+path+"' but got null ProcessorContext");
		}
		SourceFile src = ctx.getSourceFile(path);
		if (src==null) {
			throw new JavascribeException("Couldn't find source file at path '"+path+"'");
		}
		if (sourceFileClass.isAssignableFrom(src.getClass())) {
			ret = (T)src;
		} else {
			throw new JavascribeException("Source File '"+path+"' was not of type '"+sourceFileClass.getCanonicalName()+"'");
		}
		
		return ret;
	}
*/
}
