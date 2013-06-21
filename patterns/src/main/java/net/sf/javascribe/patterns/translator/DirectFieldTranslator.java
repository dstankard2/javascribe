package net.sf.javascribe.patterns.translator;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.java5.Java5CodeSnippet;

@Scannable
public class DirectFieldTranslator implements FieldTranslator {

	@Override
	public String translatorName() {
		return "MapFields";
	}

	@Override
	public JavaCode translateFields(String targetType,
			String targetVarName, CodeExecutionContext execCtx,
			List<String> fieldsToTranslate) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		return new JsomJavaCode(ret);
	}

}
