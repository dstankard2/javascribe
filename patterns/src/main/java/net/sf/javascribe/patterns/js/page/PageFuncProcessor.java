package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class PageFuncProcessor {

	private static final Logger log = Logger.getLogger(PageFuncProcessor.class);

	@ProcessorMethod(componentClass=PageFunc.class)
	public void process(PageFunc func,ProcessorContext ctx) throws JavascribeException {

		ctx.setLanguageSupport("Javascript");

		String pageName = func.getPageName();
		if ((pageName==null) || (pageName.trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no pageName");
		}
		if ((func.getName()==null) || (func.getName().trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no name");
		}
		PageType type = PageUtils.getPageType(ctx, pageName);
		if (type==null) {
			throw new JavascribeException("Tried to add a function to a page which was not found: '"+func.getPageName()+"'");
		}
		
		log.info("Processing fn '"+pageName+"."+func.getName()+"'");
		
		StringBuilder src = PageUtils.getInitFunction(ctx, pageName);
		
		src.append("this."+func.getName()+" = function(");
		if (func.getParams()!=null) {
			src.append(func.getParams());
		}
		src.append(") {\n");
		if ((func.getCode()!=null) && (func.getCode().getValue()!=null)) {
			src.append(func.getCode().getValue().trim()).append('\n');
		}
		src.append("}.bind("+pageName+");\n");
		JavascriptFunctionType fn = new JavascriptFunctionType(func.getName());

		type.addOperation(fn);
	}

}
