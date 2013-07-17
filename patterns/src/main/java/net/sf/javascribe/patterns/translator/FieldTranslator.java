package net.sf.javascribe.patterns.translator;

import java.util.List;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface FieldTranslator {

	public String name();
	
	public JavaCode translateFields(AttributeHolder targetType,String targetVarName,
			CodeExecutionContext execCtx,List<String> fieldsToTranslate) throws JavascribeException;

}

