package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.view.impl.JavascriptEvaluator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class TemplateParser {

	private static final String INS_FUNC = 
			"if (!window._ins) {\n" +
			"window._ins = function(parent,elt,prev) {\n" +
			"for(var i=0;i<parent.childNodes.length;i++) {\n" +
			"var done = true;\n" +
			"var n = parent.childNodes[i];\n" +
			"for(var i2=0;i2<prev.length;i2++) {\n" +
			"if (n._elt==prev[i2]) {\n"+
			"done = false;\nbreak;\n" +
			"}\n" +
			"}\n" +
			"if (done) {\n" +
			"parent.insertBefore(elt,n);\n" +
			"return;\n"+
			"}\n" +
			"}\n" +
			"parent.appendChild(elt);\n"+
			"};\n" +
			"}\n";
	
	public static String parseJavascriptCode(String template,
			ProcessorContext ctx,String obj,JavascriptFunctionType fn) throws JavascribeException {
		CodeExecutionContext execCtx = null;
		StringBuilder b = new StringBuilder();
		
		execCtx = new CodeExecutionContext(null,ctx.getTypes());
		addParams(execCtx,fn);
		b.append(generateJavascriptCode(template,ctx,obj,fn,execCtx));
		
		return b.toString();
	}
	
	protected static String generateJavascriptCode(String template,ProcessorContext ctx,String obj,JavascriptFunctionType fn,CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder b = new StringBuilder();
		Document doc = Jsoup.parse(template.trim());
		
		Node htmlNode = doc.childNode(0);
		if (!htmlNode.nodeName().equals("html")) {
			throw new JavascribeException("huh?");
		}
		if (htmlNode.childNodes().size()<2) {
			throw new JavascribeException("hi");
		}
		Node bodyNode = htmlNode.childNode(1);
		
		List<Node> nodeList = bodyNode.childNodes();
		if (nodeList.size()>1) {
			throw new JavascribeException("TemplateParser doesn't support a template with multiple HTML elements at the root.  Perhaps use a container 'div' element");
		}
		b.append(INS_FUNC);
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
		int previousEnd = 0;
		int i = text.indexOf("{{");

		if (text.trim().length()==0) return null;
		
		String var = var(execCtx);
		code.append("try {\n");
		code.append("var "+var+" = document.createTextNode('");
		while(i>=0) {
			String append = text.substring(previousEnd, i);
			int end = text.indexOf("}}", i+2);
			code.append(append.replace("'", "\\'"));
			String add = text.substring(i+2, end).trim();
			JavascriptEvaluator eval = new JavascriptEvaluator(add,execCtx);
			eval.parseExpression();
			if (eval.getError()!=null) {
				throw new JavascribeException(eval.getError());
			}
			String ref = eval.getResult();
			//String ref = DirectiveUtils.getValidReference(add, execCtx);
			code.append("'+((function(){try{return "+ref+"?"+ref+":'';}catch(_e){return '';}}.bind("+containerVar+"))())+'");
			previousEnd = end + 2;
			i = text.indexOf("{{", previousEnd);
		}
		if (previousEnd < text.length()) {
			code.append(text.substring(previousEnd).replace("'","\\'"));
		}
		code.append("');\n");
		code.append(containerVar+".appendChild("+var+");\n");
		code.append("}catch(_err){}\n");
		return var;
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

