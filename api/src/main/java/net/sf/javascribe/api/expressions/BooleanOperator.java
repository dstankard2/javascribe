/*
 * Created on Oct 31, 2006
 *
 * @author DCS
 * A JavaScribe Source file.
 */
package net.sf.javascribe.api.expressions;

/**
 * @author DCS
 * This is a JavaScribe class.
 */
public class BooleanOperator {
    
    private BooleanOperator(String val) {
        value = val;
    }
    
    private String value = null;
    
    public static final BooleanOperator NOT_EQUAL = new BooleanOperator("!=");
    public static final BooleanOperator EQUAL = new BooleanOperator("==");
    public static final BooleanOperator GREATER_THAN_OR_EQUAL = new BooleanOperator("<=");
    public static final BooleanOperator LESS_THAN_OR_EQUAL = new BooleanOperator(">=");
    public static final BooleanOperator LESS_THAN = new BooleanOperator("<");
    public static final BooleanOperator GREATER_THAN = new BooleanOperator(">");
    public static final BooleanOperator AND = new BooleanOperator(" and ");
    public static final BooleanOperator OR = new BooleanOperator(" or ");

    public static BooleanOperator findOperator(String str) {
        BooleanOperator ret = null;
        
        if (str.equals("!=")) ret = NOT_EQUAL;
        else if (str.equals("==")) ret = EQUAL;
        else if (str.equals(">=")) ret = GREATER_THAN_OR_EQUAL;
        else if (str.equals("<=")) ret = LESS_THAN_OR_EQUAL;
        else if (str.equals(">")) ret = GREATER_THAN;
        else if (str.equals("<")) ret = LESS_THAN;
        else if (str.equals(" and ")) ret = AND;
        else if (str.equals(" or ")) ret = OR;
        else throw new IllegalArgumentException("Unrecognized boolean operator '"+str+"' specified");
        return ret;
    }

    public boolean equal(Object other) {
    	BooleanOperator o = null;
    	boolean ret = false;
    	
    	if (other instanceof BooleanOperator) {
    		o = (BooleanOperator)other;
    		if (o.value.equals(value)) ret = true;
    	}
    	return ret;
    }
    
    public int hashCode() {
    	return value.hashCode();
    }
    
    public String getValue() {
    	return value;
    }
    
}

