package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class JavascriptDataObjectImpl implements JavascriptDataObject {
	private String name = null;
	private HashMap<String,String> attributes = new HashMap<String,String>();
	
	public JavascriptDataObjectImpl(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.append(name+" = { };\n");
		return ret;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		ret.append("var "+name+";\n");
		return ret;
	}

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		return varName + '.' + attribName;
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		return varName+'.'+attribName+" = "+evaluatedValue+";\n";
	}

	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		return attributes.get(attrib);
	}

	@Override
	public List<String> getAttributeNames() throws JavascribeException {
		List<String> ret = new ArrayList<String>();
		for(String s : attributes.keySet()) {
			ret.add(s);
		}
		return ret;
	}
	
	public void addAttribute(String name,String type) {
		attributes.put(name, type);
	}

}

