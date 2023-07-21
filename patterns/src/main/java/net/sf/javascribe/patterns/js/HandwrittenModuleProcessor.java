package net.sf.javascribe.patterns.js;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.types.ModuleExportType;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;
import net.sf.javascribe.patterns.xml.js.HandwrittenModule;

@Plugin
public class HandwrittenModuleProcessor implements ComponentProcessor<HandwrittenModule> {
	HandwrittenModule comp = null;
	ProcessorContext ctx = null;

	@Override
	public void process(HandwrittenModule comp, ProcessorContext ctx) throws JavascribeException {
		String name = comp.getName();
		String exportTypeName = comp.getExportType();
		String ref = comp.getRef();
		String path = comp.getWebPath();
		ModuleExportType exportType = null;
		String typeName = null;
		
		ctx.setLanguageSupport("Javascript");
		
		if (exportTypeName.equalsIgnoreCase("const")) {
			exportType = ModuleExportType.CONST;
		} else if (exportTypeName.equalsIgnoreCase("function")) {
			exportType = ModuleExportType.CONSTRUCTOR;
		} else {
			throw new JavascribeException("A handwritten Javascript module must have export type of either 'const' or 'function'");
		}

		if (name.trim().length()>0) {
			typeName = name;
		} else if (ref.trim().length()>0) {
			typeName = "Module_"+ref;
		} else {
			throw new JavascribeException("A handwritten Javascript module must have either 'name' or 'ref'");
		}

		if (path.trim().length()==0) {
			throw new JavascribeException("A handwritten Javascript module must have a 'path'");
		} else if (!path.endsWith(".js")) {
			throw new JavascribeException("A handwritten Javascript module must have a 'path' ending in '.js'");
		}
		String webPath = JavascriptUtils.getModulePath(path, ctx);

		ModuleType type = new ModuleType(typeName, webPath, exportType);
		ctx.addVariableType(type);

		if (ref.trim().length()>0) {
			ctx.addSystemAttribute(ref, typeName);
		}
	}

}

