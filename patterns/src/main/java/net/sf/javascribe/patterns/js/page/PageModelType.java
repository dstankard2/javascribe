package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.types.JavascriptType;

public class PageModelType extends JavascriptType {

	public PageModelType(String pageName,boolean module,ProcessorContext ctx) throws JavascribeException {
		super(PageUtils.getPageModelTypeName(pageName));
	}
	
	public JavascriptCode instantiate(String ref) {
		ctx.getLog().warn("Attempted to instantiate PageModel type with name '"+getName()+"' but this action is not supported");
		return null;
	}
	
}
