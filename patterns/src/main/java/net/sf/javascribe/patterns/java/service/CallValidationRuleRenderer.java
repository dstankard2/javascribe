package net.sf.javascribe.patterns.java.service;

import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.service.CallValidationRuleOperation;

public class CallValidationRuleRenderer extends OperationRenderer {
	CallValidationRuleOperation op = null;

	public CallValidationRuleRenderer(CallValidationRuleOperation op) {
		this.op = op;
	}

	@Override
	public void render(RendererContext ctx) throws JavascribeException {
		JavaCode code = ctx.getCode();
		String resultVar = ctx.getResultVar();
		
		JavaServiceType type = null;
		String service = op.getRule();
		
		String objName = JavascribeUtils.getObjectName(service);
		String ruleName = JavascribeUtils.getRuleName(service);

		code.append(JavaUtils.addServiceToExecutionContext(objName, ctx.execCtx(), ctx.ctx()));

		ctx.addResultProperty("validationError", "string");
		
		String typeName = ctx.ctx().getSystemAttribute(objName);
		type = JavascribeUtils.getType(JavaServiceType.class, typeName, ctx.ctx());
		
		List<ServiceOperation> ops = type.getOperations(ruleName);
		if (ops.size()==0) {
			throw new JavascribeException("Could not find validation rule '"+service+"'");
		} else if (ops.size()>1) {
			throw new JavascribeException("Validation rule does not support overloaded method '"+service+"' as it cannot determine which method to invoke");
		}
		ServiceOperation operation = ops.get(0);
		HashMap<String,String> explicitParams = new HashMap<String,String>();
		if (op.getParams().trim().length()>0) {
			explicitParams = JavascribeUtils.readParametersAsMap(op.getParams(), ctx.ctx());
		}
		if ((operation.getReturnType()==null) || (!operation.getReturnType().equals("string"))) {
			throw new JavascribeException("A validation rule must return a String");
		}

		JavaUtils.append(code, JavaUtils.callJavaOperation(resultVar+".validationError", objName, 
				operation, ctx.execCtx(), explicitParams));

		code.appendCodeText("if ("+resultVar+".getValidationError()==null) {\n");
		
		CodeExecutionContext newExecCtx = new CodeExecutionContext(ctx.execCtx());
		ctx.handleNesting(newExecCtx);
		code.appendCodeText("}\n");
	}

}

