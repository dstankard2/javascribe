package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;

public class PageModelType extends JavascriptBaseObjectType {
	String pageName = null;

	public PageModelType(String pageName) {
		this.pageName = pageName;
	}
	
	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		StringBuilder b = new StringBuilder();
		String upperCamel = JavascribeUtils.getUpperCamelName(attribName);
		
		if (varName!=null) {
			b.append(varName+".get"+upperCamel+"()");
		} else {
			b.append(pageName+",.model.get"+upperCamel+"()");
		}
		return b.toString();
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		String ret = null;
		String upperCamel = JavascribeUtils.getUpperCamelName(attribName);
		
		if (varName!=null) {
			ret = varName+".set"+upperCamel+"("+evaluatedValue+")";
		} else {
			ret = pageName+".model.set"+upperCamel+"("+evaluatedValue+")";
		}
		return ret;
	}

	@Override
	public String getAttributeType(String attrib) {
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

	@Override
	public String getName() {
		return pageName+"Model";
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("You cannot instantiate a page model.  It is initialized by generated code");
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("You cannot declare a page model.  It is initialized by generated code");
	}

}
