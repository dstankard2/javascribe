package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.SourceFile;

public interface JavaSourceFile extends SourceFile {

	public void setPackage(String s);
	public String getPackage();
	public void setClassName(String s);
	public String getClassName();
	public void setSourceRootPath(String s);
	public String getSourceRootPath();
	
}
