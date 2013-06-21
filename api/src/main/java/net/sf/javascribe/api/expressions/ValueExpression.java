/*
 * Created on Nov 25, 2006
 * A JavaScribe source file.
 */
package net.sf.javascribe.api.expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DCS
 * This class encapsulates a list of ExpressionEntry classes and contains 
 * convenience methods for getting atoms from the expression atom list, instead of 
 * having to reference generic objects from a java.util.List.
 */
public class ValueExpression {
    List<ExpressionAtom> atoms = null;
    String type = null;
    
    public ValueExpression() {
        atoms = new ArrayList<ExpressionAtom>();
    }
    
    /**
     * It is assumed that all elements of the atoms parameter are ExpressionEntry 
     * instances.  If they aren't a RuntimeException will be generated when accessing
     * this ValueExpression.
     * @param atoms A list of ExpressionEntry objects.
     */
    public ValueExpression(List<ExpressionAtom> atoms) {
        this.atoms = atoms;
    }
    
    /**
     * Append an atom to the list of expression atoms.
     * @param ob
     */
    public void addAtom(ExpressionAtom ob) {
        atoms.add(ob);
    }
    
    public BooleanExpressionAtom getBooleanExpressionEntry(int index) {
        BooleanExpressionAtom ret = null;
        
        ret = (BooleanExpressionAtom)atoms.get(index);
        return ret;
    }
    
    public ConstantBooleanExpressionAtom getConstantBooleanExpressionEntry(int index) {
        ConstantBooleanExpressionAtom ret = null;
        
        ret = (ConstantBooleanExpressionAtom)atoms.get(index);
        return ret;
    }

    public VarReferenceExpressionAtom getVarReferenceExpressionEntry(int index) {
        VarReferenceExpressionAtom ret = null;
        
        ret = (VarReferenceExpressionAtom)atoms.get(index);
        return ret;
    }
    
    public ConstantReferenceExpressionAtom getConstantReferenceExpressionEntry(int index) {
        ConstantReferenceExpressionAtom ret = null;
        
        ret = (ConstantReferenceExpressionAtom)atoms.get(index);
        return ret;
    }
    
    public CodeFragmentExpressionAtom getCodeFragmentExpressionEntry(int index) {
        CodeFragmentExpressionAtom ret = null;
        
        ret = (CodeFragmentExpressionAtom)atoms.get(index);
        return ret;
    }
    
    public int getSize() {
        return atoms.size();
    }
    
    public String getAtomType(int index) {
        String ret = null;
        ExpressionAtom ob = null;
        
        ob = atoms.get(index);
        if (ob instanceof VarReferenceExpressionAtom) ret = VAR_REF_EXPR_ATOM;
        else if (ob instanceof ConstantReferenceExpressionAtom) ret = CONST_REF_EXPR_ATOM;
        else if (ob instanceof ConstantBooleanExpressionAtom) ret = CONST_BOOLEAN_EXPR_ATOM;
        else if (ob instanceof BooleanExpressionAtom) ret = BOOLEAN_EXPR_ATOM;
        else if (ob instanceof CodeFragmentExpressionAtom) ret = CODE_FRAGMENT_EXPR_ATOM;
        else
            throw new IllegalStateException("Invalid element found in var expression: "+ob.toString());
        return ret;
    }

    public ExpressionAtom getAtom(int i) {
        return atoms.get(i);
    }

    public static String VAR_REF_EXPR_ATOM = "var";
    public static String CONST_REF_EXPR_ATOM = "con";
    public static String BOOLEAN_EXPR_ATOM = "boolean";
    public static String CONST_BOOLEAN_EXPR_ATOM = "boolean_constant";
    public static String ARITHMETIC_EXPR_ATOM = "arithmetic";
    public static String CODE_FRAGMENT_EXPR_ATOM = "code";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}
