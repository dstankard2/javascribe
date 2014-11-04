package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a callable function on a Javascript-based object.
 * 
 * Even though, in Javascript, a function is an object, for the sake of 
 * Javascribe design patterns we treat it as only a function that can be 
 * invoked.
 * 
 * For an object that is instantiated, use an AttributeHolder or 
 * JavascriptServiceObjectType that extend JavascriptVariableType.
 * @author DCS
 *
 */
public class JavascriptFunction {
	List<String> paramNames = new ArrayList<String>();
	HashMap<String,String> paramTypes = new HashMap<String,String>();
	String name = null;
	boolean returnValue = false;
	String obj = null;

	public JavascriptFunction(String obj,String name) {
		this.obj = obj;
		this.name = name;
	}

	public void addParam(String name,String type) {
		paramNames.add(name);
		paramTypes.put(name, type);
	}
	public List<String> getParamNames() {
		return paramNames;
	}
	public String getParamType(String paramName) {
		return paramTypes.get(paramName);
	}
	
	public boolean isReturnValue() {
		return returnValue;
	}
	public void setReturnValue(boolean returnValue) {
		this.returnValue = returnValue;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}

