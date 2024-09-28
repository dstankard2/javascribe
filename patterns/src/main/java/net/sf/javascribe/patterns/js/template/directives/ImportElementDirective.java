package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.types.ModuleExportType;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;
import net.sf.javascribe.patterns.js.template.parsing.ElementDirective;

@Plugin
public class ImportElementDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "js-import";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String ref = ctx.getDomAttribute("ref");
		String typeName = ctx.getDomAttribute("type");
		StringBuilder code = ctx.getCode();
		ModuleType type = null;
		
		if (ctx.getExecCtx().getVariableType(ref)!=null) return;
		
		if ((typeName!=null) && (ref!=null)) {
			if ((ctx.getProcessorContext().getSystemAttribute(ref)!=null) 
					&& (!typeName.equals(ctx.getProcessorContext().getSystemAttribute(ref)))) {
				throw new JavascribeException("Found inconsistent types for import ref '"+ref+"'");
			}
		} else if (typeName!=null) {
			// no-op
		} else if (ref!=null) {
			typeName = ctx.getProcessorContext().getSystemAttribute(ref);
			if (typeName==null) {
				throw new JavascribeException("Attempted to import unknown ref '"+ref+"'");
			}
		} else {
			throw new JavascribeException("Import element directive must have a 'ref' or a 'type'");
		}

		type = JavascribeUtils.getType(ModuleType.class, typeName, ctx.getProcessorContext());

		if (type==null) {
			throw new JavascribeException("Could not find an import named '"+typeName+"'");
		}
		ctx.importModule(typeName, type.getWebPath());
		if (type.getExportType()==ModuleExportType.CONSTRUCTOR) {
			if (ref!=null) {
				code.append("let "+ref+" = "+typeName+"();\n");
			}
		} else if (type.getExportType()==ModuleExportType.CONST) {
			if (ref!=null) {
				code.append("let "+ref+" = "+typeName+";\n");
			}
		}
		
		if (ref!=null) {
			ctx.getExecCtx().addVariable(ref, typeName);
		}
		
	}

}

