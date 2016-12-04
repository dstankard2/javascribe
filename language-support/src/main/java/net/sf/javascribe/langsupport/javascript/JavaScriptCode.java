package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.JavascribeException;

public class JavascriptCode implements Code {
	StringBuilder src = new StringBuilder();
	boolean min = false;
	
	public JavascriptCode(boolean min) {
		this.min = min;
	}
	
	public JavascriptCode(boolean min,StringBuilder src) {
		this.min = min;
		this.src = src;
	}
	
	public void merge(JavascriptCode other) throws JavascribeException {
		src.append(other.getCodeText());
	}

	public JavascriptCode append(String s) {
		src.append(s);
		return this;
	}

	@Override
	public String getCodeText() {
		return src.toString();
	}

	@Override
	public void appendCodeText(String s) throws JavascribeException {
		src.append(s);
	}

}
