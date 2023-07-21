package net.sf.javascribe.patterns.js.template;

import java.io.InputStreamReader;

import org.apache.commons.lang3.tuple.Pair;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFile;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.api.resources.FolderWatcher;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.modules.ModuleFunction;
import net.sf.javascribe.langsupport.javascript.modules.ModuleSourceFile;
import net.sf.javascribe.langsupport.javascript.modules.StandardModuleSource;
import net.sf.javascribe.langsupport.javascript.types.ModuleExportType;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveUtils;
import net.sf.javascribe.patterns.js.template.parsing.TemplateParser;

public class TemplateFolderWatcher implements FolderWatcher {

	private ApplicationFolder folder = null;
	int priority = 0;
	
	private String serviceRef = null;
	private String serviceName = null;
	
	public TemplateFolderWatcher(String serviceRef, String serviceName, int priority, ApplicationFolder folder) {
		this.serviceRef = serviceRef;
		this.serviceName = serviceName;
		this.folder = folder;
		this.priority = priority;
	}

	@Override
	public String getName() {
		return "TemplateFolderWatcher["+folder.getPath()+"]";
	}

	// This watcher is processed just after the template set component
	@Override
	public int getPriority() {
		return priority;
	}

	protected Pair<ModuleType,StandardModuleSource> ensureFolderInfo(ProcessorContext ctx) throws JavascribeException {
		ModuleType folderType = null;
		StandardModuleSource module = null;
		ModuleSourceFile src = JavascriptUtils.getModuleSource(ctx);

		ctx.addSystemAttribute(serviceRef, serviceName);
		folderType = JavascribeUtils.getType(ModuleType.class, serviceName, ctx);
		if (folderType!=null) {
			ctx.modifyVariableType(folderType);
			module = (StandardModuleSource)src.getModule(serviceName);
			if (module==null) {
				module = new StandardModuleSource(serviceName);
				module.setExportType(ModuleExportType.CONST);
				src.addModule(module);
				ctx.addVariableType(folderType);
				DirectiveUtils.ensureIns(src);
				DirectiveUtils.ensureRem(src);
				DirectiveUtils.ensureInvokeRem(src);
			}
		} else {
			String webPath = JavascriptUtils.getModulePath(ctx);
			folderType = new ModuleType(serviceName,webPath, ModuleExportType.CONST);
			//folderType = new JavascriptServiceType(serviceName,true,ctx);
			module = new StandardModuleSource(serviceName);
			module.setExportType(ModuleExportType.CONST);
			src.addModule(module);
			ctx.addVariableType(folderType);
			DirectiveUtils.ensureIns(src);
			DirectiveUtils.ensureRem(src);
			DirectiveUtils.ensureInvokeRem(src);
		}
		
		return Pair.of(folderType,module);
	}

	@Override
	public void process(ProcessorContext ctx, ApplicationFile changedFile) throws JavascribeException {
		String filename = changedFile.getName();
		String ruleName = null;
		
		ctx.setLanguageSupport("Javascript");

		if (changedFile.getFolder()!=folder) {
			return;
		}
		if (filename.endsWith(".html")) {
			ruleName = filename.substring(0, filename.length()-5);
		} else if (filename.endsWith(".htm")) {
			ruleName = filename.substring(0, filename.length()-4);
		} else {
			return;
		}

		ModuleSourceFile src = JavascriptUtils.getModuleSource(ctx);

		Pair<ModuleType,StandardModuleSource> folderInfo = ensureFolderInfo(ctx);

		ModuleType serviceType = folderInfo.getKey();
		StandardModuleSource module = folderInfo.getRight();

		// TODO: Figure this out
		String objRef = "templates.pages";

		handleFile(ctx, changedFile,ruleName,serviceType,module,objRef,src);

	}

	// TODO: objRef should probably come from a config property
	protected void handleFile(ProcessorContext ctx,ApplicationFile file,String ruleName, ModuleType folderType, StandardModuleSource mod, String objRef, ModuleSourceFile src) throws JavascribeException {
		int c = 0;
		StringBuilder templateString = new StringBuilder();

		try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
			while((c = reader.read())>=0) {
				templateString.append((char)c);
			}
		} catch(Exception e) {
			throw new JavascribeException("Couldn't read HTML template file",e);
		}

		ServiceOperation op = new ServiceOperation(ruleName);
		ModuleFunction fn = new ModuleFunction();
		fn.setName(ruleName);
		op.returnType("DOMElement");
		TemplateParser parser = new TemplateParser(templateString.toString(),ctx,objRef,op);
		CodeExecutionContext execCtx = new CodeExecutionContext(ctx);
		mod.addFunction(fn);
		folderType.addOperation(op);
		JavascriptCode code = parser.generateJavascriptCode(execCtx);
		fn.setCode(code);
		fn.setParameters(op);
		for(Pair<String,String> s : code.getImportedModules()) {
			if (!s.getKey().equals(folderType.getModuleName())) {
				src.importModule(s);
			}
		}
	}

}

