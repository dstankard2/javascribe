package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HtmlParser {
	CodeExecutionContext execCtx = null;
	String html = null;
	List<String> args = new ArrayList<String>();
	
	public HtmlParser(String html,CodeExecutionContext execCtx) {
		this.html = html;
		this.execCtx = execCtx;
	}
	
	public String getSource() throws JavascribeException {
		Document doc = Jsoup.parse(html);

		Node htmlNode = doc.childNode(0);
		if (!htmlNode.nodeName().equals("html")) {
			throw new JavascribeException("huh?");
		}
		if (htmlNode.childNodes().size()<2) {
			throw new JavascribeException("hi");
		}
		Node bodyNode = htmlNode.childNode(1);

		List<Node> nodeList = bodyNode.childNodes();
		StringBuilder build = new StringBuilder();
		build.append("var _d = document;\n");
		build.append("var _ce = document.createElement;\n");
		build.append("var _ctn = document.createTextNode;\n");
		build.append("var _returnValue = _ce('div');\n");
		String var = "_ret";
		execCtx.addVariable(var, "div");
		for(Node n : nodeList) {
			build.append(parseNode(n,var));
		}
		build.append("return _ret.childNodes;\n");
		return build.toString();
	}
	
	protected String parseNode(Node n,String containerVar) {
		StringBuilder build = new StringBuilder();
		
		if (n instanceof Element) {
			build.append(processElement((Element)n,containerVar));
		} else if (n instanceof TextNode) {
			build.append(processTextNode((TextNode)n,containerVar));
		}
		
		return build.toString();
	}
	
	protected String var() {
		for(int i=0;i<10000;i++) {
			if (execCtx.getVariableType("_v"+i)==null) {
				execCtx.addVariable("_v"+i, "div");
				return "_v"+i;
			}
		}
		return null;
	}
	
	protected String processTextNode(TextNode n,String containerVar) {
		StringBuilder b = new StringBuilder();
		String text = n.text();
		int previousEnd = 0;
		int i = text.indexOf("{{");
		
		if (text.trim().length()==0) return "";
		
		String var = var();
		b.append("var "+var+" = _ctn('");
		while(i>=0) {
			String append = text.substring(previousEnd, i);
			int end = text.indexOf("}}", i+2);
			b.append(append);
			String add = text.substring(i+2, end).trim();
			add = evalJavascript(add);
			b.append("'+"+add+"+'");
			previousEnd = end + 2;
			i = text.indexOf("{{", previousEnd);
		}
		if (previousEnd < text.length()) {
			b.append(text.substring(previousEnd));
		}
		b.append("');\n");
		b.append(containerVar+".appendChild("+var+");\n");
		
		return b.toString();
	}
	
	protected String processElement(Element elt,String containerVar) {
		StringBuilder b = new StringBuilder();
		
		String var = var();
		b.append("var "+var+" = _ce('"+elt.nodeName()+"');\n");

		boolean test = false;
		if (elt.hasAttr("js-if")) {
			test = true;
			String v = elt.attr("js-if");
			v = evalJavascript(v);
			b.append("if ("+v+") {\n");
		}
		boolean loop = false;
		if (elt.hasAttr("js-loop")) {
			loop = true;
			String a = elt.attr("js-loop");
			int i = a.indexOf(" in ");
			String eltVar = a.substring(0, i).trim();
			String list = a.substring(i+4).trim();
			String in = var();
			b.append("for("+in+"=0;"+in+"<"+list+".length;"+in+"++) {\n");
			b.append("var "+eltVar+" = "+list+"["+in+"];\n");
		}
		processElementAttributes(elt,b,var,containerVar);

		
		for(Node n : elt.childNodes()) {
			b.append(parseNode(n,var));
		}
		if (loop) {
			b.append("}\n");
		}

		if (test) {
			b.append("}\n");
		}
		
		return b.toString();
	}

	protected String evalJavascript(String js) {
		int i = js.indexOf('.');
		if (i<0) {
			if (execCtx.getTypeForVariable(js)!=null) {
				return js;
			} else {
				return js+"()";
			}
		}
		String name = js.substring(0,i);
		VariableType t = execCtx.getTypeForVariable(name);
		if (t==null) return js+"()";
		if (t instanceof PageType) {
			
		}
		
		return js;
	}
	
	protected void processElementAttributes(Element elt,StringBuilder b,String var,String containerVar) {
		b.append(containerVar+".appendChild("+var+");\n");

		List<Attribute> attrs = elt.attributes().asList();
		for(Attribute a : attrs) {
			if (a.getKey().equals("js-show")) continue;
			else if (a.getKey().equals("js-loop")) continue;
			else if (processDomEvent(b,var,a.getKey(),a.getValue())) continue;
			else {
				b.append(var+".setAttribute('"+a.getKey()+"','"+a.getValue()+"');\n");
			}
		}
		if ((elt.id()!=null) && (elt.id().length()>0)) {
			b.append(var+".id = '"+elt.id().trim()+"';\n");
		}
		
	}
	
	protected String resolveFunctionCall(String val,CodeExecutionContext execCtx) {
		String ret = null;
		
		
		return ret;
	}
	
	protected boolean processDomEvent(StringBuilder b,String var,String attr,String val) {
		attr = attr.toLowerCase();
		String fnCall = null;
		String event = null;

		fnCall = resolveFunctionCall(val,null);
		fnCall = val + "("+var+");";
		if (attr.equals("mouseover")) {
			event = "onmouseover";
		} else if (attr.equals("click")) {
			event = "onclick";
		} else if (attr.equals("contextmenu")) {
			event = "oncontextmenu";
		} else if (attr.equals("dblclick")) {
			event = "ondblclick";
		} else if (attr.equals("mousedown")) {
			event = "onmousedown";
		} else if (attr.equals("mouseenter")) {
			event = "onmouseenter";
		} else if (attr.equals("mouseleave")) {
			event = "onmouseleave";
		} else if (attr.equals("mousemove")) {
			event = "onmousemove";
		} else if (attr.equals("mouseout")) {
			event = "onmouseout";
		} else if (attr.equals("mouseup")) {
			event = "onmouseup";
		} else return false;

		b.append(var+"."+event+" = function() {"+fnCall+"};\n");
		
		return true;
	}
	
}
