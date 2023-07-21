package net.sf.javascribe.langsupport.javascript.modules;

import net.sf.javascribe.langsupport.javascript.types.ModuleExportType;

public class HandwrittenModuleSource implements ModuleSource {
	private StringBuilder codeBuild = new StringBuilder();
	private String name = null;
	private ModuleExportType exportType = null;

	public HandwrittenModuleSource(String name) {
		this.name = name;
	}

	public ModuleExportType getExportType() {
		return exportType;
	}

	public void setExportType(ModuleExportType exportType) {
		this.exportType = exportType;
	}

	public StringBuilder getCodeBuild() {
		return codeBuild;
	}
	public String getSource() {
		StringBuilder b = new StringBuilder();
		if (this.getExportType()==ModuleExportType.CONST) {
			b.append("export const "+getName()+" {\n");
			b.append(codeBuild.toString());
			b.append("\n};\n");
		} else {
			b.append("export function "+getName()+"() {\n");
			b.append(codeBuild.toString());
			b.append("\n};\n");
		}
		return b.toString();
		//return codeBuild.toString();
	}
 	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ModuleSource copy() {
		HandwrittenModuleSource ret = new HandwrittenModuleSource(name);
		ret.exportType = exportType;
		ret.codeBuild = new StringBuilder(codeBuild.toString());
		return ret;
	}

}

