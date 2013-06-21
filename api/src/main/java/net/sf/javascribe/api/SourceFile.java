package net.sf.javascribe.api;

public interface SourceFile {

	public StringBuilder getSource() throws JavascribeException;
	public String getPath();
	public void setPath(String path);
	
}

