package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class JavascriptServiceObjectImpl implements JavascriptServiceObject {
	String name = null;
	List<JavascriptFunction> operations = new ArrayList<JavascriptFunction>();
	
	public JavascriptServiceObjectImpl(String name) {
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
		ret.append(name+" = "+this.name+";\n");
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
	public List<JavascriptFunction> getOperations() {
		return operations;
	}

	public void addOperation(JavascriptFunction fn) {
		operations.add(fn);
	}

}
