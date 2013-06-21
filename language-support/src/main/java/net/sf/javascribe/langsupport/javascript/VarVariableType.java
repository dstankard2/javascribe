package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.VariableType;

public class VarVariableType implements VariableType {

	@Override
	public String getName() {
		return "var";
	}

	@Override
	public JavaScriptCode instantiate(String varName, String value,CodeExecutionContext execCtx) {
		JavaScriptCode ret = new JavaScriptCode(false);
		ret.append(varName+" = '';\n");
		return ret;
	}

	@Override
	public JavaScriptCode declare(String name, CodeExecutionContext execCtx) {
		JavaScriptCode ret = new JavaScriptCode(false);
		ret.append("var "+name+";\n");
		return ret;
	}

}

