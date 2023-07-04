package net.sf.javascribe.patterns.java.service;

import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.service.CallValidationRuleOperation;

public class CallValidationRuleRenderer extends NestingOperationRenderer {
	CallValidationRuleOperation op = null;
	ProcessorContext ctx = null;

	public CallValidationRuleRenderer(ProcessorContext ctx,CallValidationRuleOperation op) {
		super(ctx);
		this.op = op;
		this.ctx = ctx;
	}

	@Override
	public JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = null;
		JavaServiceType type = null;
		String service = op.getRule();
		
		String objName = JavascribeUtils.getObjectName(service);
		String ruleName = JavascribeUtils.getRuleName(service);

		ret = JavaUtils.addServiceToExecutionContext(objName, execCtx, ctx);
		
		String typeName = ctx.getSystemAttribute(objName);
		type = JavascribeUtils.getType(JavaServiceType.class, typeName, ctx);
		
		List<ServiceOperation> ops = type.getOperations(ruleName);
		if (ops.size()==0) {
			throw new JavascribeException("Could not find validation rule '"+service+"'");
		} else if (ops.size()>1) {
			throw new JavascribeException("Validation rule does not support overloaded method '"+service+"' as it cannot determine which method to invoke");
		}
		ServiceOperation operation = ops.get(0);
		HashMap<String,String> explicitParams = new HashMap<String,String>();
		if (op.getParams().trim().length()>0) {
			explicitParams = JavascribeUtils.readParametersAsMap(op.getParams(), ctx);
		}
		if ((operation.getReturnType()==null) || (!operation.getReturnType().equals("string"))) {
			throw new JavascribeException("A validation rule must return a String");
		}

		JavaUtils.append(ret, JavaUtils.callJavaOperation("returnValue.validationError"
				+ "", objName, operation, execCtx, explicitParams));

		ret.appendCodeText("if (returnValue.getValidationError()==null) {\n");

		return ret;
	}

	@Override
	public JavaCode endingCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCode("}\n");
		return ret;
	}

}
