package net.sf.javascribe.langsupport.java.jsom;

import java.util.List;

import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;

public class JsomJavaCode implements JavaCode {

	Java5CompatibleCodeSnippet jsomCode = null;
	
	public JsomJavaCode(Java5CompatibleCodeSnippet code) {
		this.jsomCode = code;
	}

	@Override
	public List<String> getImports() {
		return jsomCode.getRequiredImports();
	}
	
	@Override
	public void addImport(String s) {
		jsomCode.addImport(s);
	}
	
	@Override
	public String getCodeText() {
		String ret = null;
		try {
			ret = jsomCode.getSource();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public void appendCodeText(String s) {
		jsomCode.append(s);
	}

}

