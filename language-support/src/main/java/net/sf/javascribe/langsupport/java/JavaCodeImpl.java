package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

public class JavaCodeImpl implements JavaCode {

	List<String> imports = new ArrayList<String>();
	StringBuilder code = new StringBuilder();

	public void addImport(String s) {
		if (s==null) return;
		if (!imports.contains(s))
			imports.add(s);
	}
	
	@Override
	public String getCodeText() {
		return code.toString();
	}

	@Override
	public void appendCodeText(String s) {
		code.append(s);
	}

	@Override
	public List<String> getImports() {
		return imports;
	}

}
