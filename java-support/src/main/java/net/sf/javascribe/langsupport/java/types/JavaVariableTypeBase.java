package net.sf.javascribe.langsupport.java.types;

import lombok.Getter;
import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public abstract class JavaVariableTypeBase implements JavaVariableType {

	@Getter
	private String name = null;
	
	@Getter
	private String className = null;
	private String im = null;
	private BuildContext buildCtx;
	
	public JavaVariableTypeBase(String classname,String im,BuildContext buildCtx) {
		this.name = classname;
		this.className = classname;
		this.im = im;
		this.buildCtx = buildCtx;
	}
	
	public JavaVariableTypeBase(String name,String classname,String im,BuildContext buildCtx) {
		this.name = name;
		this.className = classname;
		this.im = im;
		this.buildCtx = buildCtx;
	}
	
	@Override
	public String getImport() {
		return im;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) throws JavascribeException {
		return new JavaCode(getClassName()+" "+name+" = null;\n",getImport());
	}

	@Override
	public BuildContext getBuildContext() {
		return buildCtx;
	}

}
