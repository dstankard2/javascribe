package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;

/**
 * A page is a hybrid of a data object and a service object, as it has 
 * both attributes (i.e. view, controller and model) and operations.
 * @author DCS
 *
 */
public class PageType extends JavascriptBaseObjectType {
	//HashMap<String,String> attributes = new HashMap<String,String>();
	String name = null;
	
	public PageType(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/*
	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		StringBuilder b = new StringBuilder();
		
		b.append(varName+'.'+attribName);
		
		return b.toString();
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		return varName+'.'+attribName+" = "+evaluatedValue;
	}
	*/

	/*
	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		return attributes.get(attrib);
	}

	@Override
	public List<String> getAttributeNames() throws JavascribeException {
		List<String> names = new ArrayList<String>();
		
		for(String s : attributes.keySet()) {
			names.add(s);
		}
		return names;
	}
	
	public void addAttribute(String name,String type) {
		attributes.put(name, type);
	}
	*/

	public boolean hasOperation(String name) {
		for(JavascriptFunctionType op : getOperations()) {
			if (op.getName().equals(name))
				return true;
		}
		return false;
	}

	@Override
	public Code instantiate(String name, String value,CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.appendCodeText(name+" = window."+this.name+";\n");
		return ret;
	}

}

