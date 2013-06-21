package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;

public class JavascriptVariableType implements VariableType,AttributeHolder {
	private String name = null;
	private String type = null; // Only for instantiating.
	HashMap<String,String> attributes = new HashMap<String,String>();
	
	public JavascriptVariableType(String name) {
		this.name = name;
		this.type = name;
		if (type.startsWith(JavascriptConstants.JS_TYPE)) {
			type = type.substring(JavascriptConstants.JS_TYPE.length());
		}
	}
	
	public List<String> getAttributeNames() {
		ArrayList<String> ret = new ArrayList<String>();
		
		for(String n : attributes.keySet()) {
			ret.add(n);
		}
		
		return ret;
	}
	
	public void addVariableAttribute(String name,String type) {
		attributes.put(name, type);
	}
	
	public void addFunctionAttribute(String name) {
		attributes.put(name, "js_function");
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JavaScriptCode instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("Javascript type does not support instantiate");
	}

	@Override
	public JavaScriptCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavaScriptCode ret = new JavaScriptCode(false);
		ret.append("var "+name+" = new "+type+"();");
		return ret;
	}

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
//		StringBuilder build = new StringBuilder();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttributeType(String attrib) {
		return attributes.get(attrib);
	}

}

