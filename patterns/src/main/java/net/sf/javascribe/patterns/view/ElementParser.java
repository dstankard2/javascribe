package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.view.impl.IfDirective;
import net.sf.javascribe.patterns.view.impl.JaEval2;
import net.sf.javascribe.patterns.view.impl.JaEvalResult;
import net.sf.javascribe.patterns.view.impl.JavascriptEvaluator;
import net.sf.javascribe.patterns.view.impl.LoopDirective;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class ElementParser {
	HashMap<String,ElementDirective> elementDirectives = null;
	List<AttributeDirective> attributeDirectives = null;
	HashMap<String,String> attributes = new HashMap<String,String>();
	ProcessorContext ctx = null;
	Element elt = null;
	String containerVar = null;
	StringBuilder code = null;
	String templateObj = null;
	JavascriptFunctionType function = null;
	String eltVar = null;
	boolean elementDirectiveCalled = false;
	List<String> previousEltVars = null;

	public ElementParser(Element elt,ProcessorContext ctx,String containerVar,StringBuilder code,String templateObj,JavascriptFunctionType fn,List<String> previousElementVars) {
		this.elt = elt;
		this.ctx = ctx;
		this.containerVar =containerVar;
		this.code = code;
		this.templateObj = templateObj;
		this.function = fn;
		this.previousEltVars = previousElementVars;
	}

	public String parseElement(CodeExecutionContext execCtx) throws JavascribeException {
		List<Directive> directives = getDirectives(ctx);
		
		attributeDirectives = new ArrayList<AttributeDirective>();
		elementDirectives = new HashMap<String,ElementDirective>();
		
		for(Directive d : directives) {
			if (d instanceof ElementDirective) {
				ElementDirective el = (ElementDirective)d;
				elementDirectives.put(el.getElementName(), el);
			} else if (d instanceof AttributeDirective) {
				attributeDirectives.add((AttributeDirective)d);
			}
		}

		for(Attribute att : elt.attributes().asList()) {
			attributes.put(att.getKey(), att.getValue());
		}
		
		eltVar = newVarName("_e", "DOMElement", execCtx);
		DirectiveContextImpl rctx = createDirectiveContext(execCtx);
		
		code.append("var "+eltVar+";\n");
		rctx.setElementVarName(eltVar);

		// When the element render is triggered by an event, the element is 
		// processed first and renderer code is processed in a callback function 
		// that is registered with the controller.
		// The callback function is then called.
		if (attributes.get("js-event")!=null) {
			// This is going to be evaluated as a function and bound as a 
			// callback to a page controller.
			// This is only valid on a page template, as the js-event is an event
			// to be bound on the page's controller.
			if (rctx.getExecCtx().getVariableType(DirectiveUtils.PAGE_VAR)==null) {
				throw new JavascribeException("The js-event directive is only valid on a template that has used the page directive.");
			}

			String fnVar = newVarName("_f","object",execCtx);
			String event = attributes.get("js-event");
			
			code.append("var "+fnVar+" = function() {\n");
			CodeExecutionContext newCtx = new CodeExecutionContext(execCtx);
			String iter = newVarName("_i", "integer", newCtx);
			code.append("if (!"+containerVar+") return;\n");
			code.append("for(var "+iter+"=0;"+iter+"<"+containerVar+".childNodes.length;"+iter+"++)\n");
			code.append("if ("+containerVar+".childNodes["+iter+"]._elt == '"+eltVar+"') {\n");
			code.append(containerVar+".removeChild("+containerVar+".childNodes["+iter+"]);"+iter+"--;}\n");
			code.append(eltVar+" = null;\n");

			continueParsing(newCtx,rctx);
			code.append("}.bind("+DirectiveUtils.PAGE_VAR+");\n");

			StringTokenizer tok = new StringTokenizer(event,",");
			while(tok.hasMoreTokens()) {
				String s = tok.nextToken();
				String ref = DirectiveUtils.parsePartialExpression(s, execCtx);
				code.append(DirectiveUtils.PAGE_VAR+".event("+ref+","+fnVar+","+eltVar+");\n");
			}
			/*
			if (event!=null) {
				StringTokenizer tok = new StringTokenizer(event,",");
				while(tok.hasMoreTokens()) {
					String s = tok.nextToken();
					code.append(DirectiveUtils.PAGE_VAR+".controller.addEventListener('"+s+"',"+fnVar+","+eltVar+");\n");
				}
			} else {
				code.append(DirectiveUtils.PAGE_VAR+".controller.addEventListener("+eventRef+","+fnVar+","+eltVar+");\n");
			}
			*/
			
			code.append(fnVar+"();\n");
		} else {
			continueParsing(execCtx,rctx);
		}

		return eltVar;
	}

	public void continueParsing(CodeExecutionContext execCtx,DirectiveContextImpl rctx) throws JavascribeException {
		callNextDirective(execCtx,rctx);
	}
	
	private void addDomAttributes(String eltVar,CodeExecutionContext execCtx,DirectiveContextImpl rctx) throws JavascribeException {
		for(String s : rctx.getDomAttributes().keySet()) {
			if (s.startsWith("js-")) continue;
			if (s.equals("class")) continue;
			String val = rctx.getDomAttributes().get(s).trim();
			s = DirectiveUtils.getLowerCamelFromHtml(s);
			if (s.equals("style")) code.append(addStyle(eltVar,val));
			else {
				String ref = DirectiveUtils.parsePartialExpression(val, execCtx);
				if (ref==null) {
					throw new JavascribeException("Couldn't evaluate attribute '"+s+"' with value '"+val+"'");
				}
				code.append(eltVar+"."+s+" = "+ref+";\n");
				/*
				if ((val.startsWith("{{")) && (val.endsWith("}}"))) {
					String ref = val.substring(2, val.length()-2).trim();
					ref = DirectiveUtils.getValidReference(ref, execCtx);
					String refType = DirectiveUtils.getReferenceType(ref, execCtx);
					if ((refType.equals("string")) || (refType.equals("integer")))
						code.append(eltVar+".setAttribute('"+s+"',"+ref+");\n");
					else
						code.append(eltVar+"."+s+" = "+ref+";\n");
				} else {
					code.append(eltVar+".setAttribute('"+s+"','"+val+"');\n");
				}
				*/
			}
		}
		if ((elt.id()!=null) && (elt.id().trim().length()>0)) {
			code.append(eltVar+".id = '"+elt.id()+"';\n");
		}
		if ((elt.className()!=null) && (elt.className().trim().length()>0)) {
			StringTokenizer tok = new StringTokenizer(elt.className()," ");
			while(tok.hasMoreTokens()) {
				String t = tok.nextToken().trim();
				code.append(eltVar+".classList.add('"+t+"');\n");
			}
		}
	}

	protected String processTemplateCall(String elementName,CodeExecutionContext execCtx,DirectiveContext dctx) throws JavascribeException {
		// Take the element name and remove '-', insert capital letter
		int i = elementName.indexOf('-');
		String temp = elementName;
		while(i>0) {
			if (i==elementName.length()-1) {
				throw new JavascribeException("A HTML element node name cannot end with '.' ("+temp+")");
			} else if (i==0) {
				throw new JavascribeException("A HTML element node name cannot start with '.' ("+temp+")");
			}
			if (!Character.isLetter(elementName.charAt(i+1))) {
				throw new JavascribeException("HTML template parser found an invalid node name '"+temp+"'");
			}
			elementName = elementName.substring(0,i)+Character.toUpperCase(elementName.charAt(i+1))+elementName.substring(i+2);
			i = elementName.indexOf('-');
		}
		i = elementName.lastIndexOf('.');
		if ((i==elementName.length()-1) || (i==0)) {
			throw new JavascribeException("Found an invalid template reference '"+elementName+"'");
		}
		String objRef = elementName.substring(0,i);
		String ruleName = elementName.substring(i+1);
		VariableType type = execCtx.evaluateVariableTypeForExpression(objRef);
		if ((type==null) || (!(type instanceof JavascriptBaseObjectType))) {
			throw new JavascribeException("Couldn't find template '"+elementName+"'");
		}
		JavascriptBaseObjectType srv = (JavascriptBaseObjectType)type;
		List<JavascriptFunctionType> fns = srv.getOperations(ruleName);
		if ((fns==null) || (fns.size()==0)) {
			throw new JavascribeException("Couldn't find template '"+elementName+"'");
		} else if (fns.size()>1) {
			throw new JavascribeException("Found multiple templates called '"+elementName+"' - couldn't determine which to invoke");
		}
		JavascriptFunctionType fn = fns.get(0);
		Map<String,String> atts = dctx.getDomAttributes();
		HashMap<String,String> params = new HashMap<String,String>();
		Iterator<String> names = atts.keySet().iterator();
		while(names.hasNext()) {
			String htmlAttr = names.next();
			String n = findLowerCamelFromHtml(htmlAttr);
			String val = atts.get(htmlAttr);
			JaEval2 eval = new JaEval2(val,execCtx);
			eval.addImpliedVariable(DirectiveUtils.PAGE_VAR).addImpliedVariable(DirectiveUtils.PAGE_VAR+".model");
			JaEvalResult result = eval.parseExpression();
			if (result.getErrorMessage()!=null) {
				throw new JavascribeException("Couldn't build template call - "+result.getErrorMessage());
			}
			String p = "(function() {try {return "+result.getResult().toString()+";}catch(err) { return undefined; } })()";
			params.put(n, p);
		}
		return fn.invoke(dctx.getElementVarName(), objRef, params, execCtx);
	}
	protected static String findLowerCamelFromHtml(String n) {
		int i = n.indexOf('-');
		while(i>=0) {
			if (i==n.length()-1) n = n.substring(0, i-1);
			else if (i==n.length()-2) {
				n = n.substring(0, i) + Character.toUpperCase(n.charAt(i+1));
			}
			else if (i==0) {
				n = "" + Character.toUpperCase(n.charAt(1)) + n.substring(2);
			} else {
				n = n.substring(0, i) + Character.toUpperCase(n.charAt(i+1)) + n.substring(i+2);
			}
			i = n.indexOf('-');
		}
		return n;
	}
	
	protected void callNextDirective(CodeExecutionContext execCtx,DirectiveContextImpl dctx) throws JavascribeException {

		if (attributeDirectives.size()>0) {
			while(attributeDirectives.size()>0) {
				AttributeDirective d = attributeDirectives.get(0);
				attributeDirectives.remove(0);
				if (dctx.getTemplateAttributes().containsKey(d.getAttributeName())) {
					CodeExecutionContext orig = dctx.getExecCtx();
					dctx.setExecCtx(execCtx);;
					d.generateCode(dctx);
					dctx.setExecCtx(orig);
					return;
				}
			}
		}
		
		if (elementDirectives.get(dctx.getElementName())!=null) {
			Directive d = elementDirectives.get(dctx.getElementName());
			elementDirectiveCalled = true;
			elementDirectives.remove(dctx.getElementName());
			d.generateCode(dctx);
			//code.append(dctx.getCode());
			return;
		}
		
		boolean isTemplateCall = false;
		String elementName = dctx.getElementName();
		if (elementName.indexOf('.')>0) {
			String append = processTemplateCall(elementName,execCtx,dctx);
			code.append(append);
			isTemplateCall = true;
		}

		if ((!elementDirectiveCalled) && (!isTemplateCall)) {
			code.append(eltVar+" = "+DirectiveUtils.DOCUMENT_REF+".createElement('"+dctx.getElementName()+"');\n");
		}
		// Add DOM atttibutes, if it was not a template invocation
		if (!isTemplateCall) {
			addDomAttributes(dctx.getElementVarName(),execCtx,dctx);
		}
		// Add a marker property on the element so it can be removed from the DOM later.
		code.append(eltVar+"._elt = '"+eltVar+"';\n");

		if (attributes.get("js-event")!=null) {
			String elList = newVarName("_x","list/object",execCtx);

			code.append("var "+elList+" = [");
			for(String s : previousEltVars) {
				code.append('\''+s+"',");
			}
			code.append('\''+eltVar+'\'');
			code.append("];\n");
			code.append("window._ins("+containerVar+","+eltVar+","+elList+");\n");
		} else {
			code.append(containerVar+".appendChild("+eltVar+");\n");
		}
		
		List<String> childEltVars = new ArrayList<String>();
		for(Node node : elt.childNodes()) {
			String name = TemplateParser.parseNode(dctx.getElementVarName(), node, execCtx, code, ctx,templateObj,function,childEltVars);
			if (name!=null) childEltVars.add(name);
		}
	}

	protected String addStyle(String elt,String value) {
		StringBuilder b = new StringBuilder();
		StringTokenizer tok = new StringTokenizer(value,";");
		
		while(tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if ((token.trim().length()==0)) continue;
			int i = token.indexOf(':');
			if (i>0) {
				String prop = token.substring(0, i).trim();
				while(prop.indexOf('-')>=0) {
					int x = prop.indexOf('-');
					prop = prop.substring(0, x) + Character.toUpperCase(prop.charAt(x+1))
							+ prop.substring(x+2);
				}
				String val = token.substring(i+1).trim();
				b.append(elt+".style."+prop+" = '"+val+"';\n");
			}
		}
		
		return b.toString();
	}

	protected void invokeDirective(Directive r,CodeExecutionContext execCtx,DirectiveContextImpl rctx) throws JavascribeException {
		CodeExecutionContext origCtx = rctx.getExecCtx();
		rctx.setExecCtx(execCtx);
		r.generateCode(rctx);
		rctx.setExecCtx(origCtx);
	}
	
	public String newVarName(String baseName,String type,CodeExecutionContext execCtx) {
		for(int i=0;i<10000;i++) {
			String s = baseName+i;
			if (execCtx.getVariableType(s)==null) {
				execCtx.addVariable(s, type);
				return s;
			}
		}
		return null;
	}

	private DirectiveContextImpl createDirectiveContext(CodeExecutionContext execCtx) {
		DirectiveContextImpl rctx = new DirectiveContextImpl(ctx,elt.nodeName(),attributes,containerVar,code,this,templateObj,function,elt.html());
		rctx.setExecCtx(execCtx);
		return rctx;
	}

	protected boolean isRestriction(Restrictions type,Restrictions[] restrictions) {
		for(Restrictions r : restrictions) {
			if (r==type) return true;
		}
		return false;
	}



	// API for accessing renderers
	
	protected static List<Directive> renderers = null;
	
	protected static List<Directive> getDirectives(ProcessorContext ctx) throws JavascribeException {
		if (renderers==null) {
			renderers = new ArrayList<Directive>();
			List<Class<?>> cls = ctx.getEngineProperties().getScannedClassesOfInterface(Directive.class);
			try {
				for(Class<?> cl : cls) {
					Directive r = (Directive)cl.newInstance();
					renderers.add(r);
				}
			} catch(Exception e) {
				throw new JavascribeException("Couldn't find renderers",e);
			}

			renderers.add(0, new LoopDirective());
			renderers.add(0, new IfDirective());
		}
		ArrayList<Directive> ret = new ArrayList<Directive>();
		for(Directive r : renderers) {
			ret.add(r);
		}
		
		return ret;
	}
	
}

