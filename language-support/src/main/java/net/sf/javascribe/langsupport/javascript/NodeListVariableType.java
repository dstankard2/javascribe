package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class NodeListVariableType implements JavascriptVariableType {

	@Override
	public String getName() {
		return "NodeList";
	}

	@Override
	public JavascriptCode instantiate(String varName, String value,CodeExecutionContext execCtx) throws JavascribeException {
//		JavascriptCode ret = new JavascriptCode(false);
//		ret.append(varName+" = '';\n");
//		return ret;
		throw new JavascribeException("NodeList does not support instantiate");
	}

	@Override
	public JavascriptCode declare(String name, CodeExecutionContext execCtx) {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append("var "+name+";\n");
		return ret;
	}

}
