/*
 * Created on Oct 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.javascribe.api.expressions;

import net.sf.javascribe.api.VariableType;

/**
 * @author DCS
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VarReferenceExpressionAtom implements ExpressionAtom {
    private String name = null;
    private VarReferenceExpressionAtom nestedProperty = null;
    private String typeName = null;

    private VariableType type = null;

    public VarReferenceExpressionAtom(String name,String typeName,VariableType type) {
    	this.name = name;
    	this.typeName = typeName;
    	this.type = type;
    }
    
    public String getFinalTypeName() {
    	String ret = null;
    	if (nestedProperty!=null) {
    		ret = nestedProperty.getFinalTypeName();
    	} else {
    		ret = typeName;
    	}
    	return ret;
    }
    
    public VariableType getFinalType() {
    	VariableType ret = null;
    	
    	if (nestedProperty!=null) {
    		ret = nestedProperty.getFinalType();
    	} else {
    		ret = type;
    	}
    	
    	return ret;
    }

	public String getAtomType() {
		return getFinalTypeName();
	}

	public String getName() {
		return name;
	}

	public VarReferenceExpressionAtom getNestedProperty() {
		return nestedProperty;
	}

	public void setNestedProperty(VarReferenceExpressionAtom nestedProperty) {
		this.nestedProperty = nestedProperty;
	}

	public VariableType getType() {
		return type;
	}
	public void setType(VariableType type) {
		this.type = type;
	}

    public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}

