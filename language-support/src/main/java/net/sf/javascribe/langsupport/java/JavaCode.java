package net.sf.javascribe.langsupport.java;

import java.util.List;

import net.sf.javascribe.api.Code;

public interface JavaCode extends Code {
	
	public List<String> getImports();
	public void addImport(String s);
	
}
