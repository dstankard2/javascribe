package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.types.LongIntType;

public class LongIntVariableType extends JavascriptBaseObjectType implements LongIntType {

	@Override
	public String getName() {
		return "longint";
	}

	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append(name+" = "+value+";\n");
		return ret;
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(false);
		ret.append("var "+name+";\n");
		return ret;
	}

}
