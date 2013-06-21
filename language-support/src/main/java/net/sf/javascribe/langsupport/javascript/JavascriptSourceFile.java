package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.SourceFile;

public class JavascriptSourceFile implements SourceFile {
	StringBuilder code = null;
	boolean min = false;
	String path = null;
	
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return this.path;
	}
	
	public JavascriptSourceFile(boolean min) {
		code = new StringBuilder();
		this.min = min;
	}

	public StringBuilder getSource() {
		return code;
	}
	public void setSource(StringBuilder source) {
		this.code = source;
	}

}

