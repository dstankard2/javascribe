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

		if ((func.getPageName()==null) || (func.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no pageName");
		}
		if ((func.getName()==null) || (func.getName().trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no name");
		}
		
		log.info("Processing fn '"+func.getPageName()+"."+func.getName()+"'");
		
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		
		src.getSource().append(func.getPageName()+'.'+func.getName()+" = function(");
		if (func.getParams()!=null) {
			src.getSource().append(func.getParams());
		}
		src.getSource().append(") {\n");
		if ((func.getCode()!=null) && (func.getCode().getValue()!=null)) {
			src.getSource().append(func.getCode().getValue().trim()).append('\n');
		}
		src.getSource().append("}.bind("+func.getPageName()+");\n");
		PageType type = PageUtils.getPageType(ctx, func.getPageName());
		if (type==null) {
			throw new JavascribeException("Tried to add a function to a page which was not found: '"+func.getPageName()+"'");
		}
		JavascriptFunctionType fn = new JavascriptFunctionType(func.getName());

		type.addOperation(fn);
	}

}
