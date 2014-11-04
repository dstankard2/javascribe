package net.sf.javascribe.langsupport.javascript;

import java.util.List;

public interface JavascriptServiceObject extends JavascriptVariableType {
	
	public void addOperation(JavascriptFunction fn);
	public List<JavascriptFunction> getOperations();

}
