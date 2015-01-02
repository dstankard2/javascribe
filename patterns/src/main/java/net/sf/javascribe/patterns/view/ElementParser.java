package net.sf.javascribe.patterns.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;
import net.sf.javascribe.patterns.view.impl.IfDirective;
import net.sf.javascribe.patterns.view.impl.LoopDirective;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class ElementParser {
	List<Directive> currentRenderers = null;
	HashMap<String,String> attributes = new HashMap<String,String>();
	ProcessorContext ctx = null;
	Element elt = null;
	String containerVar = null;
	StringBuilder code = null;
	String templateObj = null;
	JavascriptFunction function = null;
	List<String> activeEvents = new ArrayList<String>();
	String eltVar = null;
	
	public ElementParser(Element elt,ProcessorContext ctx,String containerVar,StringBuilder code,String templateObj,JavascriptFunction fn,List<String> events) {
		this.elt = elt;
		this.ctx = ctx;
		this.containerVar =containerVar;  
		this.code = code;
		this.templateObj = templateObj;
		this.function = fn;
		for(String s : events) {
			activeEvents.add(s);
		}
	}

	public void parseElement(CodeExecutionContext execCtx) throws JavascribeException {
		currentRenderers = getRenderers(ctx);

		for(Attribute att : elt.attributes().asList()) {
			attributes.put(att.getKey(), att.getValue());
		}
		
		eltVar = newVarName("_e", "div", execCtx);
		DirectiveContextImpl rctx = createRendererContext(execCtx);
		createNode(eltVar,execCtx,rctx);
		rctx.getCode().append("try {\n");
		// When the element render is triggered by an event, the element is 
		// processed first and renderer code is processed in a callback function 
		// that is registered with the controller.
		if (attributes.get("js-event")!=null) {
			// This is going to be evaluated as a function and bound as a 
			// callback to a page controller.
			// This is only valid on a page template, as the js-event is an event
			// to be bound on the page's controller.
			if (execCtx.getVariableType(DirectiveUtils.PAGE_VAR)==null) {
				throw new JavascribeException("The js-event directive is only valid on a template that has used the page directive.");
			}
			String fnVar = newVarName("_f","object",execCtx);
			String event = attributes.get("js-event");

			code.append("var "+fnVar+" = function() {\n");
			code.append("while("+eltVar+".childNodes.length>0){\n");
			code.append(eltVar+".removeChild("+eltVar+".childNodes[0]);\n");
			code.append("}\n");

			continueParsing(execCtx,rctx);
			code.append("}.bind("+DirectiveUtils.PAGE_VAR+");\n");
			code.append(DirectiveUtils.PAGE_VAR+".controller.addEventListener('"+event+"',"+fnVar+","+eltVar+");\n");
			
			if (activeEvents.contains(event)) {
				code.append(fnVar+"();\n");
			} else {
				activeEvents.add(event);
			}
		} else {
			continueParsing(execCtx,rctx);
		}
		code.append("}catch(_err) { }\n");
	}

	public void continueParsing(CodeExecutionContext execCtx,DirectiveContextImpl rctx) throws JavascribeException {
		callNextRenderer(execCtx,rctx);
	}
	
	private void addDomAttributes(String eltVar,CodeExecutionContext execCtx,DirectiveContextImpl rctx) {
		for(String s : rctx.getAttributes().keySet()) {
			if (s.startsWith("js-")) continue;
			String val = rctx.getAttributes().get(s).trim();
			if (s.equals("style")) code.append(addStyle(eltVar,val));
			else {
				if ((val.startsWith("{{")) && (val.endsWith("}}"))) {
					String ref = val.substring(2, val.length()-2).trim();
					ref = DirectiveUtils.getValidReference(ref, execCtx);
					code.append(eltVar+".setAttribute('"+s+"',"+ref+");\n");
				} else {
					code.append(eltVar+".setAttribute('"+s+"','"+val+"');\n");
				}
			}
		}
		if ((elt.id()!=null) && (elt.id().trim().length()>0)) {
			code.append(eltVar+".id = '"+elt.id()+"';\n");
		}
		if ((elt.className()!=null) && (elt.className().trim().length()>0)) {
			code.append(eltVar+".className = '"+elt.className()+"';\n");
		}
	}

	// Creates the element node, sets the elementVarName in the rctx.  Adds non-directive
	// attributes to the element node.
	private void createNode(String eltVar,CodeExecutionContext execCtx,DirectiveContextImpl rctx) {
		rctx.setElementVarName(eltVar);
		code.append("var "+eltVar+" = _d.createElement('"+elt.nodeName()+"');\n");
		code.append(eltVar+".classList.add('"+eltVar+"');\n");
	}

	protected void callNextRenderer(CodeExecutionContext execCtx,DirectiveContextImpl rctx) throws JavascribeException {
		boolean invoked = false;
		if (currentRenderers.size()>0) {
			while(currentRenderers.size()>0) {
				Directive r = currentRenderers.get(0);
				currentRenderers.remove(0);
				
				Restrictions[] re = r.getRestrictions();
				if (re.length==0) callNextRenderer(execCtx,rctx);
				if ((isRestriction(Restrictions.ELEMENT,re)) 
						&& (elt.nodeName().equals(r.getName()))) {
					invokeDirective(r,execCtx,rctx);
					invoked = true;
					break;
				}
				if ((isRestriction(Restrictions.ATTRIBUTE,re)) 
						&& (elt.hasAttr(r.getName()))) {
					invokeDirective(r,execCtx,rctx);
					invoked = true;
					break;
				}
			}
		}
		if (!invoked) {
			addDomAttributes(rctx.getElementVarName(),execCtx,rctx);
			code.append(containerVar+".appendChild("+rctx.getElementVarName()+");\n");
			
			// Also process the children nodes
			for(Node node : elt.childNodes()) {
				TemplateParser.parseNode(rctx.getElementVarName(), node, execCtx, code, ctx,templateObj,function,activeEvents);
			}
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

	private DirectiveContextImpl createRendererContext(CodeExecutionContext execCtx) {
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
	
	protected static List<Directive> getRenderers(ProcessorContext ctx) throws JavascribeException {
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

