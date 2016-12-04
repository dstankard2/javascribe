package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.types.DateType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class Java5DateType extends JavaVariableTypeBase implements Java5Type,DateType {

	public Java5DateType() {
		super("date","java.sql","Date");
	}
	
	@Override
	public String instantiate() {
		StringBuilder b = new StringBuilder();
		b.append("new java.util.Date(System.currentTimeMillis());\n");
		return b.toString();
	}

	@Override
	public Java5CodeSnippet instantiate(String varName, String value) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JsomUtils.merge(ret, instantiate(varName,value,null));
		return ret;
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JsomUtils.merge(ret, super.declare(varName,null));
		return ret;
	}

}

