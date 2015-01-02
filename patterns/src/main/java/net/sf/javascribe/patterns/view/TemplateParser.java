package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class TemplateParser {

	public static String parseJavascriptCode(String template,String params,
			ProcessorContext ctx,String obj,JavascriptFunction fn) throws JavascribeException {
		CodeExecutionContext execCtx = null;
		StringBuilder b = new StringBuilder();
		
		execCtx = new CodeExecutionContext(null,ctx.getTypes());
		addParams(execCtx,params,ctx);
		b.append(parseJavascriptCode(template,params,ctx,obj,fn,execCtx));
		
		return b.toString();
	}
	
	protected static String parseJavascriptCode(String template,String params,ProcessorContext ctx,String obj,JavascriptFunction fn,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		Document doc = Jsoup.parse(template);
		
		Node htmlNode = doc.childNode(0);
		if (!htmlNode.nodeName().equals("html")) {
			throw new JavascribeException("huh?");
		}
		if (htmlNode.childNodes().size()<2) {
			throw new JavascribeException("hi");
		}
		Node bodyNode = htmlNode.childNode(1);
		List<Node> nodeList = bodyNode.childNodes();
		b.append("var "+DirectiveUtils.DOCUMENT_REF+" = document;\n");
		String retVar = "_r";
		b.append("var "+retVar+" = document.createElement('div');\n");
		for(Node node : nodeList) {
			if (node.toString().trim().length()>0) {
				parseNode(retVar,node,execCtx,b,ctx,obj,fn,new ArrayList<String>());
			}
		}

		b.append("return "+retVar+".childNodes;\n");
		
		return b.toString();
	}
	
	protected static void addParams(CodeExecutionContext execCtx,String params,ProcessorContext ctx) throws JavascribeException {
		StringTokenizer tok = new StringTokenizer(params,",");
		while(tok.hasMoreTokens()) {
			String token = tok.nextToken();
			String type = ctx.getAttributeType(token);
			if (type==null) {
				throw new JavascribeException("Found unrecognized attribute for template parameter: '"+token+"'");
			}
			execCtx.addVariable(token, type);
		}
	}
	
	public static void parseNode(String containerVar,Node node,CodeExecutionContext execCtx,StringBuilder code,ProcessorContext ctx,String templateObj,JavascriptFunction fn,List<String> events) throws JavascribeException {
		if (node instanceof Element) {
			ElementParser parser = new ElementParser((Element)node,ctx,containerVar,code,templateObj,fn,events);
			parser.parseElement(execCtx);
		} else if (node instanceof TextNode) {
			processTextNode((TextNode)node,containerVar,code,execCtx);
		}
	}

	protected static void processTextNode(TextNode n,String containerVar,StringBuilder code,CodeExecutionContext execCtx) {
		String text = n.text();
		int previousEnd = 0;
		int i = text.indexOf("{{");

		if (text.trim().length()==0) return ;
		
		String var = var(execCtx);
		code.append("var "+var+" = document.createTextNode('");
		while(i>=0) {
			String append = text.substring(previousEnd, i);
			int end = text.indexOf("}}", i+2);
			code.append(append.replace("'", "\\'"));
			String add = text.substring(i+2, end).trim();
			String ref = DirectiveUtils.getValidReference(add, execCtx);
			code.append("'+"+ref+"+'");
			previousEnd = end + 2;
			i = text.indexOf("{{", previousEnd);
		}
		if (previousEnd < text.length()) {
			code.append(text.substring(previousEnd).replace("'","\\'"));
		}
		code.append("');\n");
		code.append(containerVar+".appendChild("+var+");\n");
	}

	protected static String var(CodeExecutionContext execCtx) {
		for(int i=0;i<10000;i++) {
			if (execCtx.getVariableType("_t"+i)==null) {
				execCtx.addVariable("_t"+i, "div");
				return "_t"+i;
			}
		}
		return null;
	}

}

