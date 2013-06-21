/*
 * Created on Dec 30, 2006
 * A JavaScribe source file.
 */
package net.sf.javascribe.api.expressions;

/**
 * @author DCS
 * This is a JavaScribe Class.
 */
public class ConstantBooleanExpressionAtom extends BooleanExpressionAtom {
    String text = null;
    
    public String getText() { return text; }

    public ConstantBooleanExpressionAtom(String text) {
        this.text = text.toLowerCase();
        if ((!text.equalsIgnoreCase("true")) && (!text.equalsIgnoreCase("false"))) {
            throw new IllegalArgumentException("Invalid boolean value '"+text+"'");
        }
    }
}
