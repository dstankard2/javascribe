package net.sf.javascribe.api.types;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.expressions.ValueExpression;

public interface StringType extends VariableType,AttributeHolder {

	public static final String STRING_LENGTH = "length";
	public static final String TRIMMED_VALUE = "trimmedValue";

	/**
	 * Given the specified value expression, find the code that represents it.
	 * The given expression evaluates to integer.  It has one atom, which 
	 * is an arithmetic atom, (validated) constant ref atom or (validated) var 
	 * ref atom.
	 * @param expr The expression to find the code expression for.
	 * @param execCtx Current code execution context.
	 * @return String code for the given expression.
	 * @throws ProcessingException If there is a problem.
	 */
	public String evaluateExpression(ValueExpression expr,CodeExecutionContext execCtx) throws JavascribeException;

}
