package net.sf.javascribe.patterns.js.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.patterns.js.page.elements.BinderUtils;
import net.sf.javascribe.patterns.xml.page.Element;
import net.sf.javascribe.patterns.xml.page.ViewElements;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class ViewElementsProcessor {

	private static final Logger log = Logger.getLogger(ViewElementsProcessor.class);

	@ProcessorMethod(componentClass=ViewElements.class)
	public void process(ViewElements comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		if ((comp.getPageName()==null) || (comp.getPageName().trim().length()==0)) {
			throw new JavascribeException("ViewElements requires a pageName");
		}
		
		log.info("Processing elements for page '"+comp.getPageName()+"'");

		PageType pageType = PageUtils.getPageType(ctx, comp.getPageName());
		HashMap<String,Element> elements = PageUtils.getViewElements(ctx, comp.getPageName());

		if (pageType==null) {
			throw new JavascribeException("No page named '"+comp.getPageName()+"' has been defined");
		}

		JavascriptBaseObjectType viewType = (JavascriptBaseObjectType)ctx.getTypes().getType(comp.getPageName()+"View");

		StringBuilder initCode = PageUtils.getInitFunction(ctx, comp.getPageName());
		Map<String,ElementBinderEntry> binders = BinderUtils.getElementBinders(ctx);

		for(Element elt : comp.getElement()) {
			String type = elt.getType();
			if ((type==null) || (type.trim().length()==0)) {
				throw new JavascribeException("Found an element with no type");
			}
			String id = elt.getId();
			if ((id==null) || (id.trim().length()==0)) {
				throw new JavascribeException("Found an element with no id");
			}
			elements.put(id, elt);
			
			// TODO: HTML DOM objects should probably have real types
			// to facilitate richer patterns in the view.
			viewType.addAttribute(id, "html_"+type);

			ElementBinderEntry entry = binders.get(type);
			if (entry==null) {
				System.out.println("WARNING: No binder found for element type '"+type+"'");
				//				throw new JavascribeException("No binder found for element type '"+type+"'");
			} else {
				if (entry.getBindToPage()==null) {
					throw new JavascribeException("Element '"+type+"' does not have a method annotated with BindToPage");
				}
				String val = getBindToPage(entry,id,comp.getPageName());
				initCode.append(val);
			}
		}
	}

	private String getBindToPage(ElementBinderEntry entry,String elt,String pageName) throws JavascribeException {
		String ret = null;
		Class<?> cl = entry.getCl();
		Object binder = null;

		try {
			binder = cl.newInstance();
			Method method = entry.getBindToPage();
			Class<?>[] types = method.getParameterTypes();
			if (types.length!=2) {
				throw new JavascribeException("A BindToPage function should have two string parameters: elementName and pageName");
			}
			if ((types[0]!=String.class) || (types[1]!=String.class)) {
				throw new JavascribeException("A BindToPage function should have two string parameters: elementName and pageName");
			}
			ret = (String)method.invoke(binder, elt,pageName);
		} catch(InstantiationException e) {
			throw new JavascribeException("Couldn't invoke ElementBinder to get bindToPage code",e);
		} catch(IllegalAccessException e) {
			throw new JavascribeException("Couldn't invoke ElementBinder to get bindToPage code",e);
		} catch(InvocationTargetException e) {
			throw new JavascribeException("Couldn't invoke ElementBinder to get bindToPage code",e);
		}


		return ret;
	}

}
