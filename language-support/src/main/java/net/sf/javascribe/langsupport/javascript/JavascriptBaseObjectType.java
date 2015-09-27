package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * Base superclass for all Javascript types
 * @author Dave
 *
 */
public abstract class JavascriptBaseObjectType implements AttributeHolder {
	protected List<JavascriptFunctionType> operations = new ArrayList<JavascriptFunctionType>();
	//protected String name;
	protected Map<String,String> attributes = new HashMap<String,String>();
	
	public void addAttribute(String name,String type) {
		attributes.put(name, type);
	}
	
	private static final List<String> baseAttributeNames = Arrays.asList(new String[] {
			
	});

	public abstract String getName();

	public void addOperation(JavascriptFunctionType fn) {
		operations.add(fn);
	}
	public List<JavascriptFunctionType> getOperations() {
		return operations;
	}
	
	public List<JavascriptFunctionType> getOperations(String name) {
		List<JavascriptFunctionType> ret = new ArrayList<JavascriptFunctionType>();
		
		for(JavascriptFunctionType fn : operations) {
			if (fn.getName().equals(name)) ret.add(fn);
		}
		
		return ret;
	}
	
	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.appendCodeText(name+" = { };\n");
		return ret;
	}
	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.appendCodeText("var "+name+";\n");
		return ret;
	}
	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		return varName+"."+attribName;
	}
	
	// By default, an object's attribute is accessed by referencing varName.attribName
	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		return varName+"."+attribName+" = "+evaluatedValue;
	}
	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		String type = attributes.get(attrib);
		if (type==null) {
			for(JavascriptFunctionType fn : operations) {
				if (fn.getName().equals(attrib)) {
					type = "function";
					break;
				}
			}
		}
		return attributes.get(attrib);
	}
	@Override
	public List<String> getAttributeNames() throws JavascribeException {
		List<String> ret = new ArrayList<String>();
		
		ret.addAll(baseAttributeNames);
		for(String s : attributes.keySet()) {
			ret.add(s);
		}
		
		return ret;
	}

}
