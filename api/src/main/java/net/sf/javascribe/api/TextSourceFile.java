package net.sf.javascribe.api;

/**
 * A basic implementation of the SourceFile interface.  A component processor 
 * can call getSource() and append to the returned StringBuilder.
 * @author DCS
 */
public class TextSourceFile implements SourceFile {
	StringBuilder source = new StringBuilder();
	String path = null;
	
	@Override
	public StringBuilder getSource() throws JavascribeException {
		return source;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

}

