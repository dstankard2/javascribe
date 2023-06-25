package net.sf.javascribe.patterns.js.template;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.resources.ApplicationFolder;
import net.sf.javascribe.api.resources.ApplicationResource;
import net.sf.javascribe.patterns.PatternPriority;
import net.sf.javascribe.patterns.xml.js.template.TemplateSet;

@Plugin
public class TemplateSetProcessor implements ComponentProcessor<TemplateSet> {

	@Override
	public void process(TemplateSet comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");

		String ref = comp.getRef();
		
		if (ctx.getSystemAttribute(ref)!=null) {
			throw new JavascribeException("Couldn't use ref '"+ref+"' for template set as that system attribute already exists");
		}
		String serviceName = "TemplateSet_"+ref;

		int priority = PatternPriority.HTML_TEMPLATE+comp.getOrder();

		ApplicationResource res = ctx.getResource(comp.getPath());
		if (res==null) {
			throw new JavascribeException("Couldn't find folder directory at path '"+comp.getPath()+"'");
		}
		if (!(res instanceof ApplicationFolder)) {
			throw new JavascribeException("Resource '"+comp.getPath()+"' is not an application resource");
		}
		
		TemplateFolderWatcher watcher = new TemplateFolderWatcher(ref,serviceName,priority,(ApplicationFolder)res);
		ctx.addFolderWatcher(res.getPath(), watcher);
	}

}

