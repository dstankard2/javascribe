package net.sf.javascribe.langsupport.java.jsom;

import java.util.List;

import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;

public class JavascribeJavaCodeSnippet implements Java5CompatibleCodeSnippet {
	JavaCode code = null;
	
	public JavascribeJavaCodeSnippet(JavaCode code) {
		this.code = code;
	}
	
	@Override
	public String getSource() throws CodeGenerationException {
		try {
			return code.getCodeText();
		} catch(Exception e) {
			throw new CodeGenerationException("Couldn't get code text from Javascribe",e);
		}
	}

	@Override
	public void merge(CodeSnippet other) throws CodeGenerationException {
		
	}

	@Override
	public CodeSnippet append(String s) {
		try {
		code.appendCodeText(s);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void addImport(String s) {
		code.addImport(s);
	}

	@Override
	public List<String> getRequiredImports() {
		return code.getImports();
	}

}
