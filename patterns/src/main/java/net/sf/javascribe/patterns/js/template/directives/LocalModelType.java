package net.sf.javascribe.patterns.js.template.directives;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.types.JavascriptType;

public class LocalModelType extends JavascriptType {
	String templateName;

	public LocalModelType(String templateName, ProcessorContext ctx) {
		super(templateName+"_Model");
		this.templateName = templateName;
	}

	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName,
			String targetType, CodeExecutionContext execCtx)
			throws IllegalArgumentException, JavascribeException {
		StringBuilder b = new StringBuilder();
		
		b.append(varName+"."+attribName);
		return b.toString();
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName,
			String evaluatedValue, CodeExecutionContext execCtx)
			throws JavascribeException {
		String ret = null;
		ret = varName+"."+attribName+" = "+evaluatedValue+";\n";
		return ret;
	}

}
