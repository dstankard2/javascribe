package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.CodeExecutionContext;

public class ObjectVariableType implements JavascriptVariableType {

	@Override
	public String getName() {
		return "object";
	}

	@Override
	public JavascriptCode instantiate(String varName, String value,CodeExecutionContext execCtx) {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append(varName+" = '';\n");
		return ret;
	}

	@Override
	public JavascriptCode declare(String name, CodeExecutionContext execCtx) {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append("var "+name+";\n");
		return ret;
	}

}

