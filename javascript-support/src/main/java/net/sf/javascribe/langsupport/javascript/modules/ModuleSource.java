package net.sf.javascribe.langsupport.javascript.modules;

import net.sf.javascribe.langsupport.javascript.types.ModuleExportType;

public interface ModuleSource {

	public String getName();
	public String getSource();
	public ModuleExportType getExportType();

}

