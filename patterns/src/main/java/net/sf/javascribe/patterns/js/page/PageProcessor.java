package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.patterns.xml.page.Page;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class PageProcessor {

	private static final Logger log = Logger.getLogger(PageProcessor.class);

	@ProcessorMethod(componentClass=Page.class)
	public void process(Page page,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		if (page.getPageName()==null) {
			throw new JavascribeException("Found a page with no pageName");
		}

		log.info("Processing page '"+page.getPageName()+"'");

		String pageName = page.getPageName();
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		src.getSource().append("var "+pageName+"={ };\n");
		PageType pageType = new PageType(pageName);
		ctx.getTypes().addType(pageType);
		
		StringBuilder init = PageUtils.getInitFunction(ctx, pageName);
		init.append("this.view = { };\n");
		init.append("this.view.page = document.getElementById('"+pageName+"');\n");
		init.append("this.eventDispatcher = new EventDispatcher();\n");
		pageType.addAttribute("eventDispatcher", "EventDispatcher");
		init.append("this.event = function(event,callback) {return this.eventDispatcher.event(event,callback);}.bind("+page.getPageName()+");\n");
		//src.getSource().append(page.getPageName()+".controller = new EventDispatcher();\n");
		//src.getSource().append(page.getPageName()+".event = function(event,callback,element) {this.controller.event(event,callback,element);}.bind("+page.getPageName()+");\n");
		
		JavascriptObjectType viewType = new JavascriptObjectType(pageName+"View");
		pageType.addAttribute("view", pageName+"View");
		ctx.getTypes().addType(viewType);
		JavascriptFunctionType initFn = new JavascriptFunctionType("init");
		pageType.addOperation(initFn);
		ctx.addAttribute(pageName, pageName);

		ctx.addComponent(new PageFinalizer(pageName,src.getPath()));
	}
	
}

class PageFinalizer extends ComponentBase {
	String pageName = null;
	String jsFileName = null;
	
	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_PAGE_FINALIZER; }
	
	public PageFinalizer(String pageName,String jsFileName) {
		this.pageName = pageName;
		this.jsFileName = jsFileName;
	}
	
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getJsFileName() {
		return jsFileName;
	}

	public void setJsFileName(String jsFileName) {
		this.jsFileName = jsFileName;
	}
	
}

