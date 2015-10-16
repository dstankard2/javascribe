package net.sf.javascribe.patterns.view.impl;

import java.util.Iterator;
import java.util.StringTokenizer;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.patterns.view.AttributeDirective;
import net.sf.javascribe.patterns.view.DirectiveContext;
import net.sf.javascribe.patterns.view.DirectiveUtils;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

@Scannable
public class ClassDirective implements AttributeDirective {

	@Override
	public String getAttributeName() {
		return "js-class";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		StringBuilder code = ctx.getCode();
		String classValue = ctx.getTemplateAttributes().get("js-class").trim();
		
		ctx.continueRenderElement(ctx.getExecCtx());
		
		// Attempt to read the classValue as a json string
		boolean done = false;
		JsonFactory fac = new JsonFactory(new ObjectMapper());
		try {
			JsonParser parser = fac.createJsonParser(classValue);
			JsonNode node = parser.readValueAsTree();
			done = true;
			Iterator<String> names = node.getFieldNames();
			while(names.hasNext()) {
				String bool = ctx.newVarName("_b", "boolean", ctx.getExecCtx());
				String name = names.next();
				String value = node.findValue(name).asText();
				JaEval2 eval = new JaEval2(value,ctx.getExecCtx());
				//JavascriptEvaluator eval = new JavascriptEvaluator(value,ctx.getExecCtx());
				JaEvalResult res = eval.parseExpression();
				if (res.getErrorMessage()!=null) {
					throw new JavascribeException(res.getErrorMessage());
				}
				String cond = res.getResult().toString();
				//String cond = DirectiveUtils.evaluateIf(value, ctx.getExecCtx());
				code.append("var "+bool+";\ntry {\n");
				code.append("if ("+cond+") "+bool+" = true;\n} catch(err) {}\n");
				code.append("if ("+bool+") "+ctx.getElementVarName()+".classList.add('"+name+"');\n");
			}
		} catch(Exception e) { 
			e.printStackTrace();
		}
		if (!done) {
			StringTokenizer tok = new StringTokenizer(classValue," ");
			while(tok.hasMoreTokens()) {
				String t = tok.nextToken();
				if ((t.startsWith("(")) && (t.endsWith(")"))) {
					t = t.substring(1, t.length()-1);
					code.append("try {\n");
					String expr = DirectiveUtils.getValidReference(t, ctx.getExecCtx());
					if (expr==null) {
						throw new JavascribeException("Couldn't evaluate class referenced as '"+t+"'");
					}
					code.append(ctx.getElementVarName()+".classList.add("+expr+");\n");
					code.append("} catch(_err){ }\n");
				} else {
					code.append(ctx.getElementVarName()+".classList.add('"+t+"');\n");
				}
			}
		}
	}

}
