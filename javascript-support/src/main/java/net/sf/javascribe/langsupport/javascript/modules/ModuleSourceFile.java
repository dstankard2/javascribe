package net.sf.javascribe.langsupport.javascript.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;

public class ModuleSourceFile extends JavascriptSourceFile {

	private List<ModuleSource> modules = new ArrayList<>();
	private String webPath = null;
	private Map<String,ModuleFunction> globalFunctions = new HashMap<>();

	public void addFunction(ModuleFunction fn) {
		globalFunctions.put(fn.getName(), fn);
	}
	public ModuleFunction getModuleFunction(String name) {
		return globalFunctions.get(name);
	}

	public ModuleSourceFile(String webPath) {
		super();
		this.webPath = webPath;
	}

	protected String getImportSource() {
		StringBuilder ret = new StringBuilder();
		List<String> imported = new ArrayList<>();
		
		for(Pair<String,String> im : importedModules) {
			String name = im.getLeft();
			String path = im.getRight();
			if (!imported.contains(name)) {
				String p = JavascriptUtils.getModuleRelativePath(this.getWebPath(), path);
				ret.append("import {").append(name).append("} from '"+p+"';\n");
				imported.add(name);
			}
		}
		return ret.toString();
	}
	
	@Override
	public StringBuilder getSource() {
		StringBuilder build = new StringBuilder();

		build.append(getImportSource());

		globalFunctions.entrySet().stream().forEach(entry-> {
			ModuleFunction fn = entry.getValue();
			build.append(JavascriptUtils.fnSource(fn, null));
		});
		
		modules.stream().forEach(src -> {
			build.append(src.getSource());
		});
		return build;
	}

	public void addModule(ModuleSource src) {
		modules.add(src);
	}

	public ModuleSource getModule(String name) {
		for(ModuleSource s : modules) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	public String getWebPath() {
		return webPath;
	}

}

