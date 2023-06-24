package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.xml.js.page.Page;

@Plugin
public class PageProcessor implements ComponentProcessor<Page> {

	@Override
	public void process(Page page, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		String name = page.getName();

		if (name.trim().length()==0) {
			throw new JavascribeException("Page has no name");
		}
		
		PageInfo pageInfo = PageUtils.getPageInfo(page.getName(), ctx);

		if (pageInfo!=null) {
			throw new JavascribeException("Page '"+page.getName()+"' is already defined");
		}
		pageInfo = new PageInfo();
		pageInfo.setName(name);
		PageUtils.addPageInfo(pageInfo, ctx);
	}

}
