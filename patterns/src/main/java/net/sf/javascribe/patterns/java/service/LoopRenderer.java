package net.sf.javascribe.patterns.java.service;

import org.apache.commons.lang3.StringUtils;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaListType;
import net.sf.javascribe.patterns.xml.java.service.LoopOperation;

public class LoopRenderer extends OperationRenderer {
	LoopOperation op = null;

	public LoopRenderer(LoopOperation op) {
		this.op = op;
	}

	@Override
	public void render(RendererContext ctx) throws JavascribeException {
		JavaCode code = ctx.getCode();
		String collection = op.getCollection();
		String elementRef = op.getElementRef();
		CodeExecutionContext execCtx = ctx.execCtx();

		if (StringUtils.isEmpty(collection)) {
			throw new JavascribeException("Service operation 'loop' equires a 'collection' attribute which is the collection to loop through");
		}
		if (StringUtils.isEmpty(elementRef)) {
			throw new JavascribeException("Service operation 'loop' equires a 'elementRef' attribute which is a loop variable");
		}
		
		if (execCtx.getVariableType(elementRef)!=null) {
			throw new JavascribeException("The value for 'elementRef' in 'loop' operation is already a reference in this rule");
		}
		
		if (execCtx.getVariableType(collection)==null) {
			throw new JavascribeException("The value for 'collection' in 'loop' operation must refer to a valid list variable in this rule");
		}
		if (!execCtx.getVariableType(collection).startsWith("list/")) {
			throw new JavascribeException("The value for 'collection' in 'loop' operation must refer to a valid list variable in this rule");
		}

		JavaListType listType = JavascribeUtils.getType(JavaListType.class, execCtx.getVariableType(collection), ctx.ctx());
		String eltTypeName = listType.getElementType().getName();
		JavaVariableType eltType = (JavaVariableType)listType.getElementType();
		//JavaVariableType eltType = JavascribeUtils.getType(JavaVariableType.class, eltTypeName, ctx);
		code.addImport(eltType.getImport());
		String collectionRef = JavascribeUtils.evaluateReference(collection, execCtx);
		
		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
		code.appendCodeText("for (");
		code.appendCodeText(eltType.getClassName()+" "+elementRef+" : "+collectionRef);
		code.appendCodeText(") {\n");
		newCtx.addVariable(elementRef, eltTypeName);
		
		ctx.handleNesting(newCtx);

		code.appendCodeText("}\n");
	}
	
}

