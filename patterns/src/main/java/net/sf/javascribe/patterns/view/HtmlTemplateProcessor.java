package net.sf.javascribe.patterns.view;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptObjectType;
//import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.xml.view.HtmlTemplate;
import net.sf.javascribe.patterns.xml.view.TemplateText;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class HtmlTemplateProcessor {

	private static final Logger log = Logger.getLogger(HtmlTemplateProcessor.class);
	
	@ProcessorMethod(componentClass=HtmlTemplate.class)
	public void processHtmlTemplate(HtmlTemplate comp,ProcessorContext ctx) throws JavascribeException {
		String obj = comp.getObjName().trim();
		String ref = comp.getObjRef().trim();
		String name = comp.getTemplateName().trim();
		TemplateText textObj = comp.getTemplateText();
		
		if (textObj==null) throw new JavascribeException("Found an HTML template with no templateText ('"+obj+"."+name+"')");
		
		String text = textObj.getHtmlText().trim();
		
		if ((obj.trim().length()==0)) {
			throw new JavascribeException("HTML Template requires an objName");
		}
		if (name.trim().length()==0) {
			throw new JavascribeException("HTML Template requires a templateName");
		}

		ctx.setLanguageSupport("Javascript");

		log.info("Processing HTML Template '"+obj+"."+name+"'");
		
		VariableType t = ctx.getType(obj);
		JavascriptBaseObjectType jsType = null;
		DirectiveUtils.ensureJavascriptTemplatingUtilities(ctx);
		
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		
		if (t==null) {
			src.getSource().append("var "+obj+" = {};\n");
			jsType = new JavascriptObjectType(obj);
			t = jsType;
			ctx.getTypes().addType(jsType);
		} else if (!(t instanceof JavascriptBaseObjectType)) {
			throw new JavascribeException("Type '"+obj+"' is not a Javascript type");
		} else {
			jsType = (JavascriptBaseObjectType)t;
		}

		if (ref.trim().length()>0) {
			String att = ctx.getAttributeType(ref);
			if (att==null) {
				src.getSource().append("window."+ref+" = "+obj+";\n");
				ctx.addAttribute(ref, obj);
			} else if (!att.equals(obj)) {
				throw new JavascribeException("Attribute "+ref+" already defined as type '"+att+"'");
			}
		}

		JavascriptFunctionType fn = new JavascriptFunctionType(name);
		jsType.addOperation(fn);
		fn.setReturnType("DOMElement");
		
		CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());
		TemplateParser parser = new TemplateParser(text,ctx,obj,fn);
		String code = parser.generateJavascriptCode(execCtx);
		StringBuilder s = src.getSource();
		s.append(obj+'.'+name+" = function(");
		boolean first = true;
		for(String param : fn.getParamNames()) {
			if (first) first = false;
			else s.append(',');
			s.append(param);
		}
		s.append(") {\n");
		// Generate the function.
		s.append(code);
		//b.append(TemplateParser.generateJavascriptCode(tmpl.toString(), ctx, obj, fn, execCtx));
		s.append("}\n");
		
		/*
		//JavascriptObjectType type = new JavascriptObjectType(obj);
		ctx.addAttribute(obj, type.getName());
		ctx.getTypes().addType(type);
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		
		src.getSource().append("var "+obj+" = { };\n");
		for(SingleTemplate tmp : comp.getTemplate()) {
			StringBuilder b = new StringBuilder();
			JavascriptFunctionType fn = new JavascriptFunctionType(tmp.getName());
			type.addOperation(fn);
			fn.setReturnType("DOMElement");
			String path = tmp.getPath();
			InputStream in = null;
			StringBuilder tmpl = new StringBuilder();
			try {
				in = ctx.getResource(path);
				if (in==null) {
					throw new JavascribeException("Couldn't find a template at path '"+path+"'");
				}
				int v = in.read();
				while(v>=0) {
					tmpl.append((char)v);
					v = in.read();
				}
			} catch(IOException e) {
				throw new JavascribeException("Couldn't read template file '"+path+"'",e);
			} finally {
				try {
					in.close();
				} catch(Exception e) { }
			}
			log.info(" - Parsing template "+obj+"."+tmp.getName()+"()");

			CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());
			execCtx.addVariable("this", obj);
			List<net.sf.javascribe.api.Attribute> attributes = JavascribeUtils.readAttributes(ctx, tmp.getParams());
			for(net.sf.javascribe.api.Attribute a : attributes) {
				execCtx.addVariable(a.getName(), a.getType());
				fn.addParam(a.getName(), a.getType());
			}

			String code = TemplateParser.generateJavascriptCode(tmpl.toString(), ctx, obj, fn, execCtx);
			
			b.append(obj+'.'+tmp.getName()+" = function(");
			boolean first = true;
			for(String param : fn.getParamNames()) {
				if (first) first = false;
				else b.append(',');
				b.append(param);
			}
			b.append(") {\n");
			// Generate the function.
			
			b.append(code);
			//b.append(TemplateParser.generateJavascriptCode(tmpl.toString(), ctx, obj, fn, execCtx));
			b.append("}\n");
			
			src.getSource().append(b.toString());
		}
		if (comp.getRef().trim().length()>0) {
			String ref = comp.getRef();
			if (ctx.getAttributeType(ref)!=null) {
				throw new JavascribeException("Couldn't create ref '"+ref+"' to template set, because that ref is already a system attribute");
			}
			src.getSource().append("window."+ref+" = "+obj+";\n");
			ctx.addAttribute(ref, obj);
		}
		*/
	}

}

