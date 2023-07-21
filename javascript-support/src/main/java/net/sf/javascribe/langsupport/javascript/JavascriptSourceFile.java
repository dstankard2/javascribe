package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;

public class JavascriptSourceFile implements SourceFile {

	@Getter
	private StringBuilder source = new StringBuilder();

	@Getter
	@Setter
	protected String path = null;
	protected List<Pair<String,String>> importedModules = new ArrayList<>();
	
	public JavascriptSourceFile() {
	}
	
	public void importModule(Pair<String,String> module) {
		this.importedModules.add(module);
	}
	public void importModule(ModuleType type) {
		importedModules.add(Pair.of(type.getName(), type.getWebPath()));
	}

}

