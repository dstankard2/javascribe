package net.sf.javascribe.patterns.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;
import net.sf.javascribe.langsupport.javascript.JavascriptServiceObjectImpl;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class TemplateSetProcessor {

	private static final Logger log = Logger.getLogger(TemplateSetProcessor.class);
	
	@ProcessorMethod(componentClass=TemplateSet.class)
	public void processTemplateSet(TemplateSet comp,ProcessorContext ctx) throws JavascribeException {
		String obj = comp.getObjName();
		
		ctx.setLanguageSupport("Javascript");
		if ((obj.trim().length()==0)) {
			throw new JavascribeException("Template Set requires an object name");
		}
		
		log.info("Processing view template set '"+obj+"'");
		
		if (comp.getTemplate().size()==0) {
			throw new JavascribeException("A template set should not have zero templates.");
		}
		
		if (ctx.getAttributeType(obj)!=null) {
			throw new JavascribeException("There is already a Javascript object called '"+obj+"'");
		}
		if (ctx.getType(obj)!=null) {
			throw new JavascribeException("There is already a Javascript type called '"+obj+"'");
		}
		
		JavascriptServiceObjectImpl type = new JavascriptServiceObjectImpl(obj);
		ctx.addAttribute(obj, type.getName());
		ctx.getTypes().addType(type);
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		
		src.getSource().append("var "+obj+" = { };\n");
		for(SingleTemplate tmp : comp.getTemplate()) {
			StringBuilder b = new StringBuilder();
			JavascriptFunction fn = new JavascriptFunction(obj,tmp.getName());
			type.addOperation(fn);
			String path = tmp.getPath();
			InputStream in = null;
			StringBuilder tmpl = new StringBuilder();
			try {
				in = ctx.getResource(path);
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
			b.append(obj+'.'+tmp.getName()+" = function(");
			CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());
			execCtx.addVariable("this", obj);
			List<net.sf.javascribe.api.Attribute> attributes = JavascribeUtils.readAttributes(ctx, tmp.getParams());
			boolean first = true;
			for(net.sf.javascribe.api.Attribute a : attributes) {
				execCtx.addVariable(a.getName(), a.getType());
				if (first) first = false;
				else b.append(',');
				b.append(a.getName());
				fn.addParam(a.getName(), a.getType());
			}
			b.append(") {\n");
			// Generate the function.

			b.append(TemplateParser.parseJavascriptCode(tmpl.toString(), tmp.getParams(), ctx, obj, fn));
			b.append("}\n");
			
			src.getSource().append(b.toString());
		}
	}

}

