package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;

@Scannable
@Processor
public class PageFuncProcessor {

	@ProcessorMethod(componentClass=PageFunc.class)
	public void process(PageFunc func,ProcessorContext ctx) throws JavascribeException {

		if ((func.getPageName()==null) || (func.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no pageName");
		}
		if ((func.getName()==null) || (func.getName().trim().length()==0)) {
			throw new JavascribeException("Found an invalid page func with no name");
		}
		
		System.out.println("Processing fn '"+func.getPageName()+"'");
		
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
		JavascriptVariableType type = PageUtils.getPageType(ctx, func.getPageName());
		type.addFunctionAttribute(func.getName());
	}

}
