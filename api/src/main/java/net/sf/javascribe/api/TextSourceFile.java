package net.sf.javascribe.api;

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
