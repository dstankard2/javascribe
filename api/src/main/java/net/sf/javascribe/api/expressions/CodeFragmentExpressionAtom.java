/*
 * Created on Oct 22, 2006
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
public class CodeFragmentExpressionAtom implements ExpressionAtom {
    String text = null;
    String type = null;
    
    public String getType() { return type; }
    public String getText() { return text; }

    public CodeFragmentExpressionAtom(String text,String type) {
        this.text = text;
        this.type = type;
    }

	public String getAtomType() {
		return type;
	}

	public static ValueExpression createCodeSnippetExpression(String codeSnippet,String type) {
		ValueExpression ret = new ValueExpression();
		CodeFragmentExpressionAtom atom = new CodeFragmentExpressionAtom(codeSnippet,type);
		ret.setType(type);
		ret.addAtom(atom);
		return ret;
	}
	
}
