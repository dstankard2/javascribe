package net.sf.javascribe.api;


public interface Code {

	public String getCodeText() throws JavascribeException;
	public void appendCodeText(String s) throws JavascribeException;

}
