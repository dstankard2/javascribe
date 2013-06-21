package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.VariableType;

public interface JavaVariableType extends VariableType {

	public String getImport();
	public String getClassName();

}
