package net.sf.javascribe.patterns.js;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.navigation.PageNavigationProcessor;
import net.sf.javascribe.patterns.servlet.SingleUrlService;
import net.sf.javascribe.patterns.servlet.UrlWebServiceType;
import net.sf.javascribe.patterns.xml.javascript.WebServiceClient;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class WebServiceClientProcessor {

	private static final Logger log = Logger.getLogger(PageNavigationProcessor.class);
	
	@ProcessorMethod(componentClass=WebServiceClient.class)
	public void process(WebServiceClient comp,ProcessorContext ctx) throws JavascribeException {
		if (comp.getWebServiceName().equals("")) {
			throw new JavascribeException("WebServiceClient must have a 'webServiceName' attribute");
		}
		if (comp.getPath().equals("")) {
			throw new JavascribeException("WebServiceClient must have a 'path' attribute");
		}
		if (comp.getPath().equals("")) {
			throw new JavascribeException("WebServiceClient must have a 'path' attribute");
		}
		if (comp.getObj().equals("")) {
			throw new JavascribeException("WebServiceClient must have a 'obj' attribute");
		}
		if (comp.getServiceName().equals("")) {
			throw new JavascribeException("WebServiceClient must have a 'serviceName' attribute");
		}
		
		log.info("Creating a standalone web service client for '"+comp.getWebServiceName()+"/"+comp.getPath()+"'");
		
		String webServiceName = comp.getWebServiceName();
		String path = comp.getPath();
		UrlWebServiceType srv = (UrlWebServiceType)ctx.getTypes().getType(webServiceName);
		if (srv==null) {
			throw new JavascribeException("Could not find web service object '"+webServiceName+"'");
		}
		SingleUrlService single = srv.getServices().get(path);
		if (single==null) {
			throw new JavascribeException("Could not find web service with path '"+comp.getWebServiceName()+"/"+comp.getPath()+"'");
		}
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		String returnType = single.getReturnType();
		
	}
	
}

