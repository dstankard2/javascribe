package net.sf.javascribe.patterns.translator;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface FieldTranslator {

	public String translatorName();
	
	public JavaCode translateFields(String targetType,String targetVarName,CodeExecutionContext execCtx,List<String> fieldsToTranslate);

}

