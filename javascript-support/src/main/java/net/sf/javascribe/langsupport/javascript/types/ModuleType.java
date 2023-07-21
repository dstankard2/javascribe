package net.sf.javascribe.langsupport.javascript.types;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;

@Getter
@Setter
public class ModuleType extends JavascriptServiceType {

	private String moduleName = null;
	private String sourceFile = null;
	private String webPath = null;
	
	protected ModuleExportType exportType = null;
	
	public ModuleType(String name,String webPath,ModuleExportType exportType) {
		super(name);
		this.moduleName = name;
		this.webPath = webPath;
		this.exportType = exportType;
	}

	public JavascriptCode declare(String name, String currentWebPath, CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode();
		
		return ret;
	}

	public Code declare(String name, CodeExecutionContext execCtx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode();
		ret.appendCodeText("import {"+moduleName+"} from '"+sourceFile+"';\n");
		ret.appendCodeText("var "+name+";\n");
		return ret;
	}

}
