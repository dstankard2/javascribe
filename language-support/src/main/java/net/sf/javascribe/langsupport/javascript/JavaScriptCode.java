package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.JavascribeException;

public class JavaScriptCode implements Code {
	StringBuilder src = new StringBuilder();
	boolean min = false;
	
	public JavaScriptCode(boolean min) {
		this.min = min;
	}
	
	public JavaScriptCode(boolean min,StringBuilder src) {
		this.min = min;
		this.src = src;
	}
	
	public void merge(JavaScriptCode other) throws JavascribeException {
		src.append(other.getCodeText());
	}

	public JavaScriptCode append(String s) {
		src.append(s);
		return this;
	}

	@Override
	public String getCodeText() throws JavascribeException {
		return src.toString();
	}

	@Override
	public void appendCodeText(String s) throws JavascribeException {
		src.append(s);
	}

}
