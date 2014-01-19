package net.sf.javascribe.patterns.service;

import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;

public class CallValidationRuleRenderer implements NestingServiceOperationRenderer {
	CallValidationRuleOperation op = null;
	ProcessorContext ctx = null;

	public void setGeneratorContext(ProcessorContext ctx) { 
		this.ctx = ctx;
	}

	public CallValidationRuleRenderer(CallValidationRuleOperation op) {
		this.op = op;
	}

	@Override
	public Java5CodeSnippet getCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JavaServiceObjectType type = null;
		String typeName = JavascribeUtils.getObjectName(op.getService());
		String ruleName = JavascribeUtils.getRuleName(op.getService());
		String varName = JavascribeUtils.getLowerCamelName(typeName);

		try {
			type = (JavaServiceObjectType)execCtx.getType(typeName);
			if (type==null) {
				throw new CodeGenerationException("Could not find type '"+typeName+"'");
			}

			List<JavaOperation> ops = type.getMethods(ruleName);
			if (ops.size()==0) {
				throw new CodeGenerationException("Could not find validation rule '"+ruleName+"'");
			} else if (ops.size()>1) {
				throw new CodeGenerationException("Validation rule does not support overloaded method '"+ruleName+"' as it cannot determine which method to invoke");
			}
			JavaOperation operation = ops.get(0);
			HashMap<String,String> explicitParams = new HashMap<String,String>();
			if (op.getParams()!=null) {
				explicitParams = JavascribeUtils.readParameters(ctx, op.getParams());
			}
			if ((operation.getReturnType()==null) || (!operation.getReturnType().equals("list/string"))) {
				throw new CodeGenerationException("A validation rule must return list of String");
			}

			if (execCtx.getVariableType(varName)==null) {
				ret.merge(JsomUtils.toJsomCode(type.declare(varName)));
				if (type instanceof LocatedJavaServiceObjectType) {
					LocatedJavaServiceObjectType locatedServiceType = (LocatedJavaServiceObjectType)type;
					ret.merge(JsomUtils.toJsomCode(locatedServiceType.locateService(varName, execCtx)));
				} else {
					ret.merge(JsomUtils.toJsomCode(type.instantiate(varName, null)));
				}
				execCtx.addVariable(varName, typeName);
			}

			ret.append(JavaUtils.callJavaOperation("returnValue.validationMessages", varName, operation, execCtx, explicitParams));
			ret.append("if (returnValue.getValidationMessages()==null) returnValue.setValidationMessages(new java.util.ArrayList<String>());\n");
			ret.append("if (returnValue.getValidationMessages().size()>0) {\n");
			ret.append("returnValue.setStatus(1);\n}\n");
			ret.append("else {\n");
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception rendering a business rule call",e);
		}

		return ret;
	}

	@Override
	public Java5CodeSnippet endingCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		ret.append("}\n");

		return ret;
	}

}
