package net.sf.javascribe.patterns.view.impl;

public class JavascriptEvalString {
	String code = null;
	int index = 0;

	public JavascriptEvalString copy() {
		JavascriptEvalString ret = new JavascriptEvalString(code);
		ret.index = index;
		return ret;
	}

	public JavascriptEvalString(String code) {
		this.code = code;
	}
	
	public void skip(int n) {
		index+=n;
	}
	
	public void backtrack() {
		if (index>0) index--;
	}
	
	public char next(boolean consume) {
		char ret;
		if (index<code.length()) {
			ret = code.charAt(index);
		} else {
			ret = 0;
		}
		if (consume) index++;
		
		return ret;
	}
	
	public void skipWs() {
		char c = next(true);
		
		while((Character.isWhitespace(c)) && (c!=0)) {
			c = next(true);
		}
		if (c!=0) backtrack();
	}
	
	public boolean startsWith(String s) {
		return toString().startsWith(s);
	}
	public boolean startsWith(char c) {
		return toString().startsWith(""+c);
	}
	
	/*
	public void skip(int chars) {
		index+=chars;
	}
	*/
	
	public String toString() {
		if (index<code.length()) return code.substring(index);
		else return "";
	}

}
