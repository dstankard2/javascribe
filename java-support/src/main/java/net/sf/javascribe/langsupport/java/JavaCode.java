package net.sf.javascribe.langsupport.java;

import java.util.HashSet;
import java.util.Set;

import net.sf.javascribe.api.Code;

public class JavaCode implements Code {
	Set<String> imports = new HashSet<>();
	StringBuilder code = new StringBuilder();
	
	public JavaCode() {
	}

	public JavaCode(String code,String... imports) {
		this.code.append(code);
		for(String i : imports) {
			addImport(i);
		}
	}

	public Set<String> getImports() {
		return imports;
	}
	public void addImport(String s) {
		if (!imports.contains(s)) {
			imports.add(s);
		}
	}

	@Override
	public String getCodeText() {
		return code.toString();
	}

	@Override
	public void appendCodeText(String s) {
		this.code.append(s);
	}

	@Override
	public void append(Code append) {
		if (!(append instanceof JavaCode)) {
			return;
		}
		JavaCode other = (JavaCode)append;
		this.appendCodeText(other.getCodeText());
		for(String im : other.getImports()) {
			if (im!=null) {
				addImport(im);
			}
		}
	}
	
}
