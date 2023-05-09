package net.sf.javascribe.langsupport.java.types.impl;

import net.sf.jaspercode.api.BuildContext;
import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class JavaEnumType extends JavaVariableTypeBase {

	public JavaEnumType(String name,String pkg,BuildContext buildCtx) {
		super(name,pkg+'.'+name,buildCtx);
	}
	
}
