package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaSourceFile;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5SourceFile;

public class JsomJavaSourceFile implements JavaSourceFile {
	Java5SourceFile src = null;
	
	public JsomJavaSourceFile(Java5SourceFile src) {
		this.src = src;
	}
	
	public Java5SourceFile getSrc() {
		return src;
	}
	
	@Override
	public StringBuilder getSource() throws JavascribeException {
		try {
			return src.getSource();
		} catch(CodeGenerationException e) {
			throw new JavascribeException("Couldn't get source for JSOM file",e);
		}
	}

	@Override
	public String getPath() {
		return src.getPath();
	}

	@Override
	public void setPath(String s) { }

	@Override
	public void setPackage(String s) {
		src.setPackageName(s);
	}

	@Override
	public String getPackage() {
		return src.getPackageName();
	}

	@Override
	public void setClassName(String s) {
		src.getPublicClass().setClassName(s);
	}

	@Override
	public String getClassName() {
		return src.getPublicClass().getClassName();
	}

	@Override
	public void setSourceRootPath(String s) {
		src.setSourceRootPath(s);
	}

	@Override
	public String getSourceRootPath() {
		return null;
	}

}

