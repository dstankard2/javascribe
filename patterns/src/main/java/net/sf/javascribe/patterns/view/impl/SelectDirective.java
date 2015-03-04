package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;
import net.sf.javascribe.patterns.view.ElementDirective;

@Scannable
public class SelectDirective implements ElementDirective {

	@Override
	public String getElementName() {
		return "select";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder b = ctx.getCode();
		String list = ctx.getTemplateAttributes().get("js-options");
		CodeExecutionContext execCtx = ctx.getExecCtx();
		String eltVar = ctx.getElementVarName();
		String model = ctx.getTemplateAttributes().get("js-model");
		String modelRef = DirectiveUtils.getValidReference(model, execCtx);

		b.append(eltVar+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('select');\n");
		
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
			String listRef = DirectiveUtils.getValidReference(listName, execCtx);
			if (listRef==null) {
				throw new JavascribeException("Option directive Couldn't find a list called '"+listRef+"'");
			}
			
			String listTypeName = ctx.getProcessorContext().getAttributeType(listName);
			if (listTypeName==null) listTypeName = ctx.getExecCtx().getVariableType(listName);
			if (listTypeName==null) {
				throw new JavascribeException("Found unrecognized list reference '"+listName+"'");
			}
			if (listTypeName.indexOf("list/")>0) {
				throw new JavascribeException("Variable '"+listName+"' specified for a list of select options is not a list");
			}
			String eltTypeName = listTypeName.substring(5);
			
			CodeExecutionContext newCtx = new CodeExecutionContext(ctx.getExecCtx());
			String indexVar = ctx.newVarName("_i", "integer", newCtx);
			String nodeVar = ctx.newVarName("_n", "Node", newCtx);
			newCtx.addVariable(elt, eltTypeName);
			String descRef = DirectiveUtils.getValidReference(desc, newCtx);
			if (descRef==null) {
				throw new JavascribeException("In select, couldn't understand option text param '"+desc+"'");
			}
			String idRef = DirectiveUtils.getValidReference(id, newCtx);
			if (idRef==null) {
				throw new JavascribeException("In select, couldn't understand option value param '"+id+"'");
			}
			b.append("if ("+listRef+") {\n");
			b.append("for(var "+indexVar+"=0;"+indexVar+"<"+listRef+".length;"+indexVar+"++) {\n");
			b.append("var "+elt+" = "+listRef+"["+indexVar+"];\n");
			b.append("var "+nodeVar+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('option');\n");
			b.append(nodeVar+".innerHTML = "+descRef+";\n");
			b.append(nodeVar+".value = "+idRef+";\n");
			b.append(eltVar+".appendChild("+nodeVar+");\n");
			b.append("}\n");
			b.append("}\n");
		}

		if (modelRef!=null) {
			String modelVar = ctx.newVarName("_m", "object", execCtx);
			b.append("var "+modelVar+";\n");
			b.append("try {"+modelVar+" = "+modelRef+";\n}catch(_err) {}\n");
			CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
			String indexVar = ctx.newVarName("_i", "integer", newCtx);
			b.append("for(var "+indexVar+"=0;"+indexVar+"<"+eltVar+".options.length;"+indexVar+"++) {\n");
			b.append("if ("+eltVar+".options["+indexVar+"].value=="+modelVar+") {\n");
			b.append(eltVar+".options.selectedIndex = "+indexVar+"; break;\n");
			b.append("}\n}\n");
		}
	}

}
