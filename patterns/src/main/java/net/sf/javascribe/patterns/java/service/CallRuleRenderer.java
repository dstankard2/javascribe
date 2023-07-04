package net.sf.javascribe.patterns.java.service;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.patterns.xml.java.service.CallRuleOperation;

public class CallRuleRenderer extends OperationRenderer {

	private CallRuleOperation op = null;
	
	public CallRuleRenderer(ProcessorContext ctx,CallRuleOperation op) {
		super(ctx);
		this.op = op;
	}
	
	@Override
	public JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode code = new JavaCode();
		String result = op.getResult();
		String rule = op.getRule();
		String paramString = op.getParams();
		String serviceRef = null;
		ServiceOperation o = null;
		List<PropertyEntry> params = null;
		Map<String,String> paramMap = JavascribeUtils.readParametersAsMap(paramString, ctx);
		
		if ((result.trim().length()>0) && (result.indexOf('.') < 0 ) && (!execCtx.getVariableNames().contains(result))) {
			String resultTypeName = ctx.getSystemAttribute(result);
			if (resultTypeName==null) {
				throw new JavascribeException("Couldn't find a system attribute called '"+result+"' - it must be defined already");
			}
			JavaVariableType resultType = execCtx.getType(JavaVariableType.class, resultTypeName);
			JavaUtils.append(code, resultType.declare(result, execCtx));
			execCtx.addVariable(result, resultTypeName);
		}
		
		int i = rule.indexOf('.');
		if (i<0) {
			throw new JavascribeException("Couldn't invoke a rule called '"+rule+"' - it must be of format '<serviceRef>.<rule>'");
		}
		serviceRef = rule.substring(0, i);

		params = JavascribeUtils.readParametersAsList(paramString, ctx);
		o = JavaUtils.findRule(rule, params, ctx, execCtx);

		JavaUtils.append(code, JavaUtils.addServiceToExecutionContext(serviceRef, execCtx, ctx));
		JavaUtils.append(code, JavaUtils.callJavaOperation(result, serviceRef, o, execCtx, paramMap));

		return code;
	}

}
