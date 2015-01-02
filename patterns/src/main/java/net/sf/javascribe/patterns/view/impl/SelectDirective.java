package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.patterns.view.Directive;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.Restrictions;

@Scannable
public class SelectDirective implements Directive {

	@Override
	public Restrictions[] getRestrictions() {
		return new Restrictions[] { Restrictions.ELEMENT };
	}

	@Override
	public String getName() {
		return "select";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String list = ctx.getAttributes().get("js-options");
		CodeExecutionContext execCtx = ctx.getExecCtx();

		String n = ctx.getElementVarName();
		b.append("while("+n+".firstChild) {\n");
		b.append(n+".removeChild("+n+".firstChild);\n");
		b.append("}\n");
		
		ctx.continueRenderElement(execCtx);

		final String PARSE_ERROR = "Invalid format of js-options string on select.  Expected '<id value> as <desc> with <list element> in <list>'";
		if (list!=null) {
			list = list.trim();
			int descStart = list.indexOf(" as ")+4;
			if (descStart<1) throw new JavascribeException(PARSE_ERROR);
			int eltStart = list.indexOf(" with ",descStart)+6;
			if (eltStart<0) throw new JavascribeException(PARSE_ERROR);
			int listStart = list.indexOf(" in ", eltStart)+4;
			if (listStart<0) throw new JavascribeException(PARSE_ERROR);

			String id = list.substring(0,descStart-4).trim();
			String desc = list.substring(descStart, eltStart-6).trim();
			String elt = list.substring(eltStart,listStart-4).trim();
			String listName = list.substring(listStart);
			
			if ((id.equals("")) || (desc.equals("")) || (elt.equals("")) 
					||(listName.equals(""))) {
				throw new JavascribeException(PARSE_ERROR);
			}
			
			// Get a reference to the list
			String listRef = null;
			String eltType = null;
			String listType = null;
			if (DirectiveUtils.getPageName(ctx)!=null) {
				try {
					listRef = ExpressionUtil.evaluateValueExpression("${"+DirectiveUtils.PAGE_VAR+".model."+listName+'}', "object", execCtx);
					listType = DirectiveUtils.getPageModelType(ctx).getAttributeType(listName);
					eltType = listType.substring(5);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if (listRef==null) {
				try {
					listRef = ExpressionUtil.evaluateValueExpression("${"+listName+'}', "list/object", execCtx);
					listType = execCtx.getVariableType(listName);
					eltType = listType.substring(5);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if (listRef==null) {
				throw new JavascribeException("Could not find a list called '"+listName+"'");
			}
			String listVar = ctx.newVarName("_l", listType, execCtx);
			if (execCtx.getVariableType(elt)!=null) {
				throw new JavascribeException("Name '"+elt+"' is invalid for the list iterator as there is already a variable with this name defined on this template");
			}
			b.append("var "+listVar+" = "+listRef+";\n");
			b.append("if (("+listVar+") && ("+listVar+".length>0)) {\n");
			CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
			String idx = ctx.newVarName("_i", "integer", newCtx);
			b.append("for(var "+idx+"=0;"+idx+"<"+listVar+".length;"+idx+"++) {\n");
			b.append("var "+elt+" = "+listVar+"["+idx+"];\n");
			String optVar = ctx.newVarName("_o", "object", newCtx);
			b.append("var "+optVar+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('option');\n");
			newCtx.addVariable(elt, eltType);
			String descEval = ExpressionUtil.evaluateValueExpression("${"+desc+"}", "object", newCtx);
			String valueEval = ExpressionUtil.evaluateValueExpression("${"+id+"}", "object", newCtx);
			b.append(optVar+".innerHTML = "+descEval+";\n");
			b.append(optVar+".value = "+valueEval+";\n");
			b.append(ctx.getElementVarName()+".appendChild("+optVar+");\n");
			b.append("}\n");
			b.append("}\n");
		}
	}

}
