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
	String template;
	ProcessorContext ctx;
	String obj;
	JavascriptFunctionType fn;
	
	public TemplateParser(String template,ProcessorContext ctx,String obj,JavascriptFunctionType fn) {
		this.template = template;
		this.ctx = ctx;
		this.obj = obj;
		this.fn = fn;
	}
	
	public String generateJavascriptCode(CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		Document doc = Jsoup.parse(template.trim(), "//", Parser.xmlParser());

		List<Node> nodeList = doc.childNodes();
		List<ElementParser> rootParsers = new ArrayList<ElementParser>();

		b.append("var "+DirectiveUtils.DOCUMENT_REF+" = document;\n");
		String retVar = DirectiveUtils.TEMPLATE_ROOT_ELEMENT_REF;
		b.append("var "+retVar+" = document.createElement('div');\n");
		execCtx.addVariable(retVar, "DOMElement");

		boolean hasElement = false;
		for(Node node : nodeList) {
			if (node instanceof Element) {
				ElementParser p = new ElementParser((Element)node,ctx,retVar,obj,fn,new ArrayList<String>(),this);
				if ((p.isDomElement()) && (hasElement)) {
					throw new JavascribeException("A HTML template can only have one element at the root.");
				}
				if (p.isDomElement()) {
					hasElement = true;
				}
				rootParsers.add(p);
			} else if (node instanceof TextNode) {
				String text = ((TextNode)node).getWholeText();
				if (text.trim().length()>0) {
					throw new JavascribeException("A HTML template may not have text in the DOM root");
				}
			}
		}
		
		for(ElementParser p : rootParsers) {
			p.parseElement(execCtx);
			b.append(p.getElementCode());
		}

		b.append("return "+retVar+".childNodes[0];\n");
		
		return b.toString();
	}
	
	public String processChildNodeList(List<Node> nodeList,String containerVar,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		List<String> children = new ArrayList<String>();
		
		b.append("(function() {\n");

		CodeExecutionContext newCtx = new CodeExecutionContext(execCtx,ctx.getTypes());
		for(Node node : nodeList) {
			if (node.toString().trim().length()>0) {
				b.append(parseNode(containerVar,node,newCtx,children));
			}
		}
		
		b.append("})();\n");
		
		return b.toString();
	}

	public String parseNode(String containerVar,Node node,CodeExecutionContext execCtx,List<String> previousEltVars) throws JavascribeException {
		String ret = null;
		
		if (node instanceof Element) {
			Element elt = (Element)node;
			ElementParser parser = new ElementParser(elt,ctx,containerVar,obj,fn,previousEltVars,this);
			parser.parseElement(execCtx);
			ret = parser.getElementCode();
			String var = parser.getEltVar();
			if (var!=null) previousEltVars.add(var);
		} else if (node instanceof TextNode) {
			ret = processTextNode((TextNode)node,containerVar,execCtx);
		} else {
			// TODO: Handle cases
			ret = "";
		}
		
		return ret;
	}

	protected static String processTextNode(TextNode n,String containerVar,CodeExecutionContext execCtx) throws JavascribeException {
		String text = n.text();
		String docRef = DirectiveUtils.DOCUMENT_REF;
		String finalRef = DirectiveUtils.parsePartialExpression(text, execCtx);
		String var = DirectiveUtils.newVarName("_t","DOMElement",execCtx);
		StringBuilder code = new StringBuilder();

		if (text.trim().length()==0) return null;

		code.append("try {\n");
		code.append("var "+var+" = "+docRef+".createTextNode(");
		code.append(finalRef);
		code.append(");\n");

		code.append(containerVar+".appendChild("+var+");\n");
		code.append("}catch(_err){}\n");
		return code.toString();
	}
	
}

