package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An accessor for an operation on a business object
 * @author Dave
 *
 */
public class JavaOperation {

	private String name = null;
	private List<String> parameterNames = new ArrayList<String>();
	private HashMap<String,String> parameterTypes = new HashMap<String,String>();
	private String returnType = null;

	public String getName() {
		return name;
	}
	public JavaOperation setName(String name) {
		this.name = name;
		return this;
	}
	public List<String> getParameterNames() {
		return parameterNames;
	}
	public JavaOperation addParameter(String name,String type) {
		parameterNames.add(name);
		parameterTypes.put(name, type);
		return this;
	}
	public HashMap<String, String> getParameterTypes() {
		return parameterTypes;
	}
	public String getReturnType() {
		return returnType;
	}
	public JavaOperation setReturnType(String returnType) {
		this.returnType = returnType;
		return this;
	}
	
}

