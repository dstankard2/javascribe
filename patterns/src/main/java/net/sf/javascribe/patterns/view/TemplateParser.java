package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

public class TemplateParser {

	protected static String generateJavascriptCode(String template,ProcessorContext ctx,String obj,JavascriptFunctionType fn,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		Document doc = Jsoup.parse(template.trim(), "//", Parser.xmlParser());

		List<Node> nodeList = doc.childNodes();
		if (nodeList.size()>1) {
			throw new JavascribeException("TemplateParser doesn't support a template with multiple HTML elements at the root.  Perhaps use a container 'div' element");
		}

		//b.append(INS_FUNC);
		b.append("var "+DirectiveUtils.DOCUMENT_REF+" = document;\n");
		String retVar = "_r";
		b.append("var "+retVar+" = document.createElement('div');\n");
		for(Node node : nodeList) {
			if (node.toString().trim().length()>0) {
				parseNode(retVar,node,execCtx,b,ctx,obj,fn,new ArrayList<String>());
			}
		}

		b.append("return "+retVar+".childNodes[0];\n");
		
		return b.toString();
	}
	
	protected static void addParams(CodeExecutionContext execCtx,JavascriptFunctionType fn) throws JavascribeException {
		for(String s : fn.getParamNames()) {
			execCtx.addVariable(s, fn.getParamType(s));
		}
	}
	
	public static String parseNode(String containerVar,Node node,CodeExecutionContext execCtx,StringBuilder code,ProcessorContext ctx,String templateObj,JavascriptFunctionType fn,List<String> previousEltVars) throws JavascribeException {
		String ret = null;
		
		if (node instanceof Element) {
			ElementParser parser = new ElementParser((Element)node,ctx,containerVar,code,templateObj,fn,previousEltVars);
			ret = parser.parseElement(execCtx);
		} else if (node instanceof TextNode) {
			processTextNode((TextNode)node,containerVar,code,execCtx);
		}
		
		return ret;
	}

	protected static String processTextNode(TextNode n,String containerVar,StringBuilder code,CodeExecutionContext execCtx) throws JavascribeException {
		String text = n.text();
		String docRef = DirectiveUtils.DOCUMENT_REF;
		String finalRef = DirectiveUtils.parsePartialExpression(text, execCtx);
		String var = var(execCtx);

		if (text.trim().length()==0) return null;

		code.append("try {\n");
		code.append("var "+var+" = "+docRef+".createTextNode(");
		code.append(finalRef);
		code.append(");\n");

		code.append(containerVar+".appendChild("+var+");\n");
		code.append("}catch(_err){}\n");
		return var;
	}

	protected static String var(CodeExecutionContext execCtx) {
		for(int i=0;i<100;i++) {
			if (execCtx.getVariableType("_t"+i)==null) {
				execCtx.addVariable("_t"+i, "div");
				return "_t"+i;
			}
		}
		return null;
	}

}

