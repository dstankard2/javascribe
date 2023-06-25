package net.sf.javascribe.patterns.js.template.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.types.DOMElementType;
import net.sf.javascribe.patterns.js.parsing.JavascriptParser;
import net.sf.javascribe.patterns.js.parsing.JavascriptParsingResult;

public class ElementParser implements DirectiveContext {
	Map<String,ElementDirective> elementDirectives = null;
	List<AttributeDirective> attributeDirectives = null;

	private Map<String,String> attributes = new HashMap<String,String>();
	private Map<String,String> domAttributes = new HashMap<String,String>();
	private Map<String,String> templateAttributes = new HashMap<String,String>();

	ProcessorContext ctx = null;
	Element elt = null;
	String containerVar = null;
	StringBuilder code = new StringBuilder();
	String templateObj = null;
	ServiceOperation serviceOperation = null;
	List<String> previousEltVars = null;
	TemplateParser caller = null;

	String eltVar = null;

	boolean isTemplateCall = false;
	boolean isElementDirective = false;
	boolean elementCreated = false;
	boolean domPropertiesAdded = false;
	List<CodeExecutionContext> contexts = new ArrayList<CodeExecutionContext>();
	List<Pair<String,String>> imports = new ArrayList<>();

	public List<Pair<String,String>> getImports() {
		return imports;
	}
	
	public void importModule(String typeName,String jsPath) {
		imports.add(Pair.of(typeName, jsPath));
	}


	public ElementParser(Element elt,ProcessorContext ctx,String containerVar,String templateObj,ServiceOperation fn,List<String> previousElementVars, TemplateParser caller) throws JavascribeException {
		attributeDirectives = DirectiveUtils.getAttributeDirectives(ctx);
		elementDirectives = DirectiveUtils.getElementDirectives(ctx);
		this.elt = elt;
		this.ctx = ctx;
		this.containerVar =containerVar;
		this.templateObj = templateObj;
		this.serviceOperation = fn;
		this.previousEltVars = previousElementVars;
		this.caller = caller;
		isTemplateCall = elt.nodeName().indexOf('.')>0;
		isElementDirective = elementDirectives.get(elt.nodeName())!=null;
	}
	
	public String getElementCode() {
		return code.toString();
	}
	public String getEltVar() {
		return eltVar;
	}

	public boolean isDomElement() {
		if (isElementDirective) return false;
		return true;
	}

	public void parseElement(CodeExecutionContext execCtx) throws JavascribeException {
		for(Attribute att : elt.attributes().asList()) {
			String key = att.getKey();
			String val = att.getValue();
			attributes.put(key, val);
			if (key.startsWith("js-")) {
				templateAttributes.put(key, val);
			} else {
				domAttributes.put(key, val);
			}
		}

		if (!isElementDirective) {
			eltVar = DirectiveUtils.newVarName("_e", "DOMElement", execCtx);
			code.append("var "+eltVar+";\n");
			this.continueParsing(execCtx);
		} else {
			this.contexts.add(execCtx);
			ElementDirective d = elementDirectives.get(elt.nodeName());
			d.generateCode(this);
			//this.contexts.remove(0);
			this.contexts.remove(execCtx);
		}
	}

	private void continueParsing(CodeExecutionContext execCtx) throws JavascribeException {
		contexts.add(execCtx);
		boolean attributeDirectiveCalled = false;
		while(attributeDirectives.size()>0) {
			AttributeDirective next = attributeDirectives.get(0);
			attributeDirectives.remove(0);
			
			if ((!elementCreated) && (next.getPriority()>=3)) {
				elementCreated = true;
				if (!isTemplateCall) {
					code.append(eltVar+" = "+DirectiveUtils.DOCUMENT_REF+
							".createElement('"+elt.nodeName()+"');\n");
					code.append(eltVar+"._elt = '"+eltVar+"';\n");
					code.append(eltVar+".$$remove = [];\n");
				} else {
					code.append(processTemplateCall(elt.nodeName(),execCtx,this));
					code.append(eltVar+"._elt = '"+eltVar+"';\n");
					if (containerVar!=null)
						code.append(containerVar+".appendChild("+eltVar+");\n");
				}
			}
			String att = next.getAttributeName();
			if (templateAttributes.get(att)!=null) {
				next.generateCode(this);
				attributeDirectiveCalled = true;
			}
		}
		if (!attributeDirectiveCalled) {
			if (isTemplateCall) {
				if (!elementCreated) {
					code.append(processTemplateCall(elt.nodeName(),execCtx,this));
					code.append(eltVar+"._elt = '"+eltVar+"';\n");
					if (containerVar!=null)
						code.append(containerVar+".appendChild("+eltVar+");\n");
				}
			} else {
				if (!elementCreated) {
					code.append(eltVar+" = "+DirectiveUtils.DOCUMENT_REF+
							".createElement('"+elt.nodeName()+"');\n");
					code.append(eltVar+"._elt = '"+eltVar+"';\n");
					code.append(eltVar+".$$remove = [];\n");
				}
				addDomProperties(execCtx);
				if (containerVar!=null)
					code.append(containerVar+".appendChild("+eltVar+");\n");
				// Process children
				if ((elt.childNodes()!=null) && (elt.childNodes().size()>0)) {
					String append = caller.processChildNodeList(elt.childNodes(), eltVar, execCtx);
					code.append(append);
				}
			}
		}
		contexts.remove(contexts.size()-1);
	}
	
	private void addDomProperties(CodeExecutionContext execCtx) throws JavascribeException {
		DOMElementType eltType = new DOMElementType();

		for(String s : domAttributes.keySet()) {
			if (s.equals("class")) continue;
			String val = domAttributes.get(s);
			if (s.equals("style")) code.append(addStyle(eltVar,val));
			else if (s.equals("disabled")) {
				code.append(eltVar+".disabled = true;\n");
			} else if (eltType.getAttributeType(s)==null) {
				ctx.getLog().warn("Found an unrecognized DOM element property '"+s+"'");
			} else if (s.equalsIgnoreCase("colspan")) {
				String ref = DirectiveUtils.parsePartialExpression(val, execCtx);
				code.append(eltType.getCodeToSetAttribute(eltVar, "colSpan", ref, execCtx));
				code.append(";\n");
			} else if (!eltType.getAttributeType(s).equals("object")) {
				// This DOM attribute from the HTML is in the DOMElement type.
				String ref = DirectiveUtils.parsePartialExpression(val, execCtx);
				code.append(eltType.getCodeToSetAttribute(eltVar, s, ref, execCtx));
				code.append(";\n");
			} else {
				String ref = DirectiveUtils.parsePartialExpression(val, execCtx);
				if (ref==null) {
					throw new JavascribeException("Couldn't evaluate attribute '"+s+"' with value '"+val+"'");
				}
				if (s.indexOf('-')>=0) {
					code.append(eltVar+".setAttribute('"+s+"',"+ref+");\n");
				} else {
					code.append(eltVar+"."+s+" = "+ref+";\n");
				}
			}
		}
		if ((elt.id()!=null) && (elt.id().trim().length()>0)) {
			code.append(eltVar+".id = '"+elt.id()+"';\n");
		}
		if ((elt.className()!=null) && (elt.className().trim().length()>0)) {
			String t = elt.className().trim();
			String val = DirectiveUtils.parsePartialExpression(t, execCtx);
			code.append(eltVar+".className = "+val+";\n");
		}
	}

	protected String processTemplateCall(String elementName,CodeExecutionContext execCtx,ElementParser dctx) throws JavascribeException {
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
		String typeName = execCtx.getVariableType(objRef);
		if (typeName==null) {
			throw new JavascribeException("Couldn't invoke template '"+elementName+"' as there was no service '"+objRef+"' in the code execution context");
		}
		//String typeName = JasperUtils.getTypeForRef(objRef, dctx.getProcessorContext());
		List<ServiceOperation> fns = JavascribeUtils.findRuleFromTypeAndRef(typeName+'.'+ruleName, ctx);
		if ((fns==null) || (fns.size()==0)) {
			throw new JavascribeException("Couldn't find template '"+elementName+"'");
		} else if (fns.size()>1) {
			throw new JavascribeException("Found multiple templates called '"+elementName+"' - couldn't determine which to invoke");
		}
		ServiceOperation fn = fns.get(0);
		Map<String,String> atts = dctx.domAttributes;
		HashMap<String,String> params = new HashMap<String,String>();
		Iterator<String> names = atts.keySet().iterator();
		while(names.hasNext()) {
			String htmlAttr = names.next();
			String n = findLowerCamelFromHtml(htmlAttr);
			String val = atts.get(htmlAttr);
			JavascriptParser eval = new JavascriptParser(val,execCtx);
			DirectiveUtils.populateImpliedVariables(eval);
			JavascriptParsingResult result = eval.evalExpression();
			String p = "(function() {try {return "+result.getCode()+";}catch(err) { return undefined; } })()";
			params.put(n, p);
		}
		JavascriptCode code = JavascriptUtils.callJavascriptOperation(dctx.getElementVarName(), objRef, fn, execCtx, params, true, false);
		return code.getCodeText();
		//return fn.invoke(dctx.getElementVarName(), objRef, params, execCtx);
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

	@Override
	public StringBuilder getCode() {
		return this.code;
	}
	@Override
	public void continueRenderElement(CodeExecutionContext execCtx) throws JavascribeException {
		this.continueParsing(execCtx);
	}
	@Override
	public void continueRenderElement() throws JavascribeException {
		this.continueParsing(this.getExecCtx());
	}
	@Override
	public List<String> getPreviousEltVars() {
		return previousEltVars;
	}
	@Override
	public String getDomAttribute(String name) {
		String val = this.domAttributes.get(name);
		if ((val==null) || (val.trim().length()==0)) val = null;
		return val;
	}
	@Override
	public String getElementVarName() {
		return this.eltVar;
	}
	@Override
	public String getElementName() {
		return elt.nodeName();
	}
	@Override
	public String getContainerVarName() {
		return this.containerVar;
	}
	@Override
	public String getTemplateAttribute(String name) {
		String val = this.templateAttributes.get(name);
		if ((val==null) || (val.trim().length()==0)) val = null;
		return val;
	}
	@Override
	public CodeExecutionContext getExecCtx() {
		if (contexts.size()==0) return null;
		return contexts.get(contexts.size()-1);
	}
	@Override
	public String newVarName(String baseName,String type,CodeExecutionContext execCtx) {
		return DirectiveUtils.newVarName(baseName, type, execCtx);
	}
	@Override
	public String getTemplateObj() {
		return this.templateObj;
	}
	@Override
	public String getInnerHtml() {
		return elt.html();
	}
	@Override
	public ServiceOperation getFunction() {
		return this.serviceOperation;
	}
	@Override
	public ProcessorContext getProcessorContext() {
		return ctx;
	}
	@Override
	public boolean isJavascriptDebug() {
		return  JavascriptUtils.isJavascriptDebug(ctx);
	}

}
