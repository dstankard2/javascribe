package net.sf.javascribe.api;

public interface SourceFile {

	/**
	 * Returns the source code for this file.  The framework will not invoke 
	 * this until all components have been processed.
	 * @return Source as it will be put into the file.
	 */
	public StringBuilder getSource();
	
	/**
	 * Query the file for its path relative to the output directory.
	 * @return Path relative to the output directory.
	 */
	public String getPath();
	
}

