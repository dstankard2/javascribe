package net.sf.javascribe.patterns.view.impl;

public class JaEvalString {

	private String code = null;
	private int index = 0;
	
	public JaEvalString copy() {
		return new JaEvalString(code,index);
	}
	public JaEvalString(String code,int place) {
		this.code = code;
		index = place;
	}
	public int getPlace() {
		return index;
	}
	public void setPlace(int i) {
		index = i;
	}
	public String getCode() {
		if (getRemaining()==0) return "";
		return code.substring(index);
	}
	
	public char next() {
		if (getRemaining()<1) return 0;
		char c = code.charAt(index);
		index++;
		return c;
	}
	public char nextNonWs() {
		if (getRemaining()<1) return 0;
		char c = code.charAt(index);
		index++;
		while(Character.isWhitespace(c)) {
			if (getRemaining()==0) return 0;
			c = code.charAt(index);
			index++;
		}
		return c;
	}
	public void toNextNonWs() {
		if (getRemaining()<1) return;
		char c = next();
		while(Character.isWhitespace(c)) {
			if (getRemaining()<1) return;
			c = next();
		}
		backtrack();
	}
	public int getRemaining() {
		int ret = code.length()-index;
		if (ret<0) ret = 0;
		return ret;
	}
	public void backtrack() {
		index--;
	}
	public String findLiteral(String s) {
		int prev = index;
		while(Character.isWhitespace(code.charAt(index))) index++;
		if (code.indexOf(s)==0) {
			index+=s.length();
			return s;
		}
		index = prev;
		
		return null;
	}
	public boolean startsWith(String s) {
		return (code.indexOf(s)==0);
	}
	public void skip(int chars) {
		index+=chars;
	}
}
