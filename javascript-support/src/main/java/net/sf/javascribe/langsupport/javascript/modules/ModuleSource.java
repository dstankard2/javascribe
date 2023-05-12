package net.sf.javascribe.langsupport.javascript.modules;

import net.sf.javascribe.langsupport.javascript.types.ExportedModuleType;

public interface ModuleSource {

	public String getName();
	public String getSource();
	// For use by the engine when managing source files.
	// The returned ModuleSource should be a copy of this one.
	public ModuleSource copy();
	public ExportedModuleType getExportType();

}

