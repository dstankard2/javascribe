package net.sf.javascribe.patterns.js.page;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.langsupport.javascript.JavascriptConstants;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;
import net.sf.javascribe.patterns.CorePatternConstants;

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

		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		src.getSource().append("var "+page.getPageName()+"={ };\n");
		JavascriptVariableType type = new JavascriptVariableType(JavascriptConstants.JS_TYPE+page.getPageName());
		ctx.getTypes().addType(type);
		
		StringBuilder init = PageUtils.getInitFunction(ctx, page.getPageName());
		init.append("this.view.page = $('#"+page.getPageName()+"');\n");

		ctx.addComponent(new PageFinalizer(page.getPageName(),src.getPath()));
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

