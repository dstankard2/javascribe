package net.sf.javascribe.patterns.service;

import java.util.HashMap;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JavascribeJavaCodeSnippet;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;

public class CallBusinessRuleRenderer implements ServiceOperationRenderer {
	CallBusinessRuleOperation op = null;
	ProcessorContext ctx = null;

	public void setGeneratorContext(ProcessorContext ctx) {
		this.ctx = ctx;
	}

	public CallBusinessRuleRenderer(CallBusinessRuleOperation op) {
		this.op = op;
	}

	@Override
	public Java5CodeSnippet getCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JavaServiceObjectType type = null;
		String typeName = JavascribeUtils.getObjectName(op.getRule());
		String ruleName = JavascribeUtils.getRuleName(op.getRule());
		String varName = JavascribeUtils.getLowerCamelName(typeName);

		try {
			type = (JavaServiceObjectType)execCtx.getType(typeName);
			if (type==null) {
				throw new JavascribeException("Could not find type '"+typeName+"'");
			}
			JavaOperation operation = type.getMethod(ruleName);

			HashMap<String,String> explicitParams = new HashMap<String,String>();
			if (op.getParams()!=null) {
				explicitParams = JavascribeUtils.readParameters(ctx, op.getParams());
			}

			if (execCtx.getVariableType(varName)==null) {
				ret.merge(new JavascribeJavaCodeSnippet(type.declare(varName)));
				if (type instanceof LocatedJavaServiceObjectType) {
					LocatedJavaServiceObjectType locatedServiceType = (LocatedJavaServiceObjectType)type;
					ret.merge(new JavascribeJavaCodeSnippet(locatedServiceType.getInstance(varName, execCtx)));
				} else {
					ret.merge(new JavascribeJavaCodeSnippet(type.instantiate(varName, null)));
				}
				execCtx.addVariable(varName, typeName);
			}

			String res = op.getResult();
			if ((res!=null) && (!res.startsWith("returnValue.")) && (operation.getReturnType()!=null)) {
				if (res.indexOf(".")<0) {
					if (execCtx.getTypeForVariable(res)==null) {
						execCtx.addVariable(res, operation.getReturnType());
						ret.merge(JsomUtils.declare(execCtx, res, operation.getReturnType()));
					}
				}
			}
			Java5CodeSnippet code = new Java5CodeSnippet();
			code.append(JavaUtils.callJavaOperation(res, varName, operation, execCtx, explicitParams));
			ret.merge(code);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception rendering a business rule call",e);
		}

		return ret;
	}

}

