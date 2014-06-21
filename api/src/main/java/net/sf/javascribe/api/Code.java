package net.sf.javascribe.api;


/**
 * Represents a snippet of code.  The language of the code is not specified.
 * @author DCS
 *
 */
public interface Code {

	public String getCodeText() throws JavascribeException;
	public void appendCodeText(String s) throws JavascribeException;

}
