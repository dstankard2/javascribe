package net.sf.javascribe.patterns.js.page;

import java.util.List;

import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.xml.js.page.PageModel;

@Plugin
public class PageModelProcessor implements ComponentProcessor<PageModel> {

	@Override
	public void process(PageModel comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		String pageName = comp.getPageName();
		
		if (pageName.trim().equals("")) {
			throw new JavascribeException("PageModel requires a 'pageName' attribute");
		}
		PageInfo info = PageUtils.getPageInfo(pageName, ctx);
		if (info==null) {
			throw new JavascribeException("Couldn't find page '"+pageName+"' for pageModel component");
		}

		String modelTypeName = info.getModelTypeName();
		PageModelType modelType = JavascribeUtils.getType(PageModelType.class, modelTypeName, ctx);

		String properties = comp.getProperties();
		List<PropertyEntry> entries = JavascribeUtils.readParametersAsList(properties, ctx);
		for(PropertyEntry entry : entries) {
			String name = entry.getName();
			if (entry.getType()==null) {
				throw new JavascribeException("Couldn't find type for property "+name);
			}
			if (modelType.getAttributeType(name)!=null) {
				if (!modelType.getAttributeType(name).equals(entry.getType().getName())) {
					throw new JavascribeException("Found inconsistent types for model attribute ");
				}
			} else {
				PageUtils.addModelProperty(pageName, name, entry.getType().getName(), ctx);
			}
		}
	}

}

