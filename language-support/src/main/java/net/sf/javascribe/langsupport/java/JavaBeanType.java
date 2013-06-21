/*
 * Created on May 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.javascribe.api.AttributeHolder;

/**
 * @author DCS
 */
public abstract class JavaBeanType implements AttributeHolder,JavaVariableType {
	protected String className = null;
	protected HashMap<String,String> attributes = new HashMap<String,String>();
	protected String pkg = null;
	protected String name = null;
	
	public List<String> getAttributeNames() {
		ArrayList<String> ret = new ArrayList<String>();
		
		Set<String> atts = attributes.keySet();
		for(String s : atts) {
			ret.add(s);
		}
		
		return ret;
	}
	
	public JavaBeanType(String name,String pkg,String cl) {
	    this.name = name;
	    className = cl;
	    this.pkg = pkg;
	}

	public String getGetterMethodName(String attrib) {
		String ret = null;
		
		if (attributes.get(attrib)!=null) {
			ret = "get"+Character.toUpperCase(attrib.charAt(0))+attrib.substring(1);
		}
		return ret;
	}
	
	public String getSetterMethodName(String attrib) {
		String ret = null;
		
		if (attributes.get(attrib)!=null) {
			ret = "set"+Character.toUpperCase(attrib.charAt(0))+attrib.substring(1);
		}
		return ret;
	}
	
	public void setPkg(String s) { pkg = s; }
	
	public void setClassName(String cl) { className = cl; }

	public String getImport() { return pkg+'.'+className; }
	public String getClassName() { return className; }

	public void addAttribute(String name,String type) {
		name = Character.toLowerCase(name.charAt(0))+name.substring(1);
		attributes.put(name,type);
	}

//	public abstract JavaCode declare(String var,CodeExecutionContext execCtx);
	
	@Override
	public String getAttributeType(String attrib) { 
		return attributes.get(attrib);
	}
	
	public String getName() { return name; }

}

