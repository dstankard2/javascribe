package net.sf.javascribe.api.types;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.expressions.ValueExpression;

public interface BooleanType extends VariableType {

	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException;

	public boolean isValidBooleanAtom(String atom);
	
}
