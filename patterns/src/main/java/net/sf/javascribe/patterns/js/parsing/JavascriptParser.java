package net.sf.javascribe.patterns.js.parsing;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;

// TODO: Should there be more to this or is it not necessary?
public class JavascriptParser {

	private String code = null;
	private List<String> impliedVariables = new ArrayList<>();
	
	public JavascriptParser(String code,CodeExecutionContext execCtx) {
		this.code = code;
	}
	
	public JavascriptParser addImpliedVariable(String name) {
		impliedVariables.add(name);
		return this;
	}
	
	public JavascriptParsingResult evalExpression() {
		JavascriptParsingResult ret = new JavascriptParsingResult();
		
		ret.setCode(code);
		
		return ret;
	}
	
	public JavascriptParsingResult evalCodeBlock() {
		JavascriptParsingResult ret = new JavascriptParsingResult();
		// Insert \n after ;, { and }
		String c = code.replaceAll(";", ";\n").replaceAll("}", "}\n").replaceAll("\\{", "{\n");
		ret.setCode(c);
		
		return ret;
	}
	
}
