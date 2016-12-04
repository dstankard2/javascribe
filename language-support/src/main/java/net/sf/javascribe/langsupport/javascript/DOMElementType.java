package net.sf.javascribe.langsupport.javascript;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;


public class DOMElementType extends JavascriptBaseObjectType {
	protected Map<String,String> attributes = new HashMap<String,String>(); // Accessed via getAttribute
	protected Map<String,String> properties = new HashMap<String,String>(); // Accessed via dot notation

	public String getName() {
		return "DOMElement";
	}
	
	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		if (attributes.get(attribName)!=null) {
			String getter = "get"+JavascribeUtils.getUpperCamelName(attribName);
			return varName+'.'+getter+"()";
		}
		return varName+"."+attribName;
	}

	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		String ret = null;
		
		ret = super.getAttributeType(attrib);
		if (ret==null) {
			if (attributes.get(attrib)!=null) {
				ret = attributes.get(attrib);
			} else if (properties.get(attrib)!=null) {
				ret = properties.get(attrib);
			} else ret = "object";
		}
		
		return ret;
	}

	{
		properties.put("tagvalue", "object");
		properties.put("tagName", "string");
		properties.put("tabIndex", "integer");
		properties.put("parentNode", "DOMElement");
		properties.put("className", "string");
		properties.put("contentEditable", "boolean");
		properties.put("dir", "string");
		properties.put("id", "string");
		properties.put("innerHTML", "string");
		properties.put("nodeName", "string");
		properties.put("offsetHeight", "integer");
		properties.put("offsetLeft", "integer");
		properties.put("offsetTop", "integer");
		properties.put("offsetWidth", "integer");
		properties.put("colSpan", "integer");
		properties.put("style", "DOMStyle");
		properties.put("classList", "DOMClassList");
		properties.put("min", "integer");
		properties.put("max", "integer");
		properties.put("id", "string");

		properties.put("disabled", "string");
		JavascriptFunctionType fn = new JavascriptFunctionType("addEventListener");
		fn.addParam("event", "string");
		fn.addParam("callback", "function");
		addOperation(fn);
	}
	
}

