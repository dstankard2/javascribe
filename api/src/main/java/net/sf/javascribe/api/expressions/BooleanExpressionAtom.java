/*
 * Created on Oct 31, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.javascribe.api.expressions;

/**
 * @author DCS
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BooleanExpressionAtom implements ExpressionAtom {
    private BooleanOperator operand = null;
    private ValueExpression leftOperator = null;
    private ValueExpression rightOperator = null;
    
    public BooleanExpressionAtom() {
    }
    
    public BooleanOperator getOperator() { return operand; }
    
    public void setOperator(BooleanOperator op) {
        operand = op;
    }
    
	public String getAtomType() {
		return "boolean";
	}

	public ValueExpression getLeftOperand() {
        return leftOperator;
    }
    public void setLeftOperand(ValueExpression leftOperator) {
        this.leftOperator = leftOperator;
    }
    public ValueExpression getRightOperand() {
        return rightOperator;
    }
    public void setRightOperand(ValueExpression rightOperator) {
        this.rightOperator = rightOperator;
    }
}
