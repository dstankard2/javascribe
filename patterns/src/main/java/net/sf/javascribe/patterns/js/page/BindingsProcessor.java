package net.sf.javascribe.patterns.js.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.elements.BinderUtils;
import net.sf.javascribe.patterns.js.page.elements.ElementBinderContext;

@Scannable
@Processor
public class BindingsProcessor {

	private static final Logger log = Logger.getLogger(BindingsProcessor.class);

	@ProcessorMethod(componentClass=Bindings.class)
	public void process(Bindings comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		
		if ((comp.getPageName()==null) || (comp.getPageName().trim().length()==0)) {
			throw new JavascribeException("Found Bindings spec with no pageName");
		}

		log.info("Processing bindings for page '"+comp.getPageName()+"'");
		
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		PageType type = PageUtils.getPageType(ctx, comp.getPageName());

		StringBuilder init = PageUtils.getInitFunction(ctx, comp.getPageName());

		Map<String,ElementBinderEntry> binders = BinderUtils.getElementBinders(ctx);
		
		HashMap<String,Element> viewElements = PageUtils.getViewElements(ctx, comp.getPageName());
		
		for(Binding binding : comp.getBinding()) {
			if (binding.getType()==null) {
				throw new JavascribeException("All event bindings must have a type");
			} else if (binding.getType().equals("generic")) {
				handleGenericBinding(init,binding,type);
				continue;
			}
			Element elt = viewElements.get(binding.getElement());
			if (elt==null) {
				throw new JavascribeException("Unrecognized element '"+binding.getElement()+"' specified in binding");
			}
			String eltType = elt.getType();
			if (eltType==null) {
				throw new JavascribeException("Found invalid element type for element '"+binding.getElement()+"'");
			}
			ElementBinderEntry b = binders.get(eltType);
			if (b!=null) {
				Method method = b.getBindings().get(binding.getType());
				if (method!=null) {
					String val = null;
					Object binder = null;
					try {
						binder = b.getCl().newInstance();
					} catch(Exception e) {
						throw new JavascribeException("Couldn't instantiate element binder",e);
					}
					val = handleBinding(binder,ctx,comp.getPageName(),binding,method);
					init.append(val);
				} else {
					System.out.println("WARNING: Found no binders of type "+binding.getType()+" for element type "+eltType);
				}
			} else {
				System.out.println("WARNING: Found no binders for element type "+eltType);
			}
		}
	}

	private String handleBinding(Object binder,ProcessorContext ctx,String pageName,Binding binding,Method method) throws JavascribeException {
		ElementBinderContext bctx = ElementBinderContext.newInstance(ctx, pageName);
		String ret = null;
		
		if (method.getReturnType()!=String.class) {
			throw new JavascribeException("An element binder method must return a String");
		}
		
		Class<?>[] types = method.getParameterTypes();
		Object params[] = new Object[types.length];
		
		for(int i=0;i<types.length;i++) {
			Class<?> cl = types[i];
			if (cl==ElementBinderContext.class) {
				params[i] = bctx;
			} else if (cl==Binding.class) {
				params[i] = binding;
			} else {
				throw new JavascribeException("An element binding method may take parameters of type ElementBinderContext or Binding");
			}
		}
		try {
			ret = (String)method.invoke(binder, params);
		} catch(InvocationTargetException e) {
			throw new JavascribeException("Couldn't invoke element binder",e);
		} catch(IllegalAccessException e) {
			throw new JavascribeException("Couldn't invoke element binder",e);
		}
		
		return ret;
	}
	
	private void handleGenericBinding(StringBuilder init,Binding binding,PageType type) throws JavascribeException {
		String target = binding.getTarget();
		
		if (target==null) {
			throw new JavascribeException("Generic binding requires a target");
		}
		if (type.hasOperation(target)) {
			init.append("this.event(\""+binding.getEvent()+"\",this."+target+");\n");
		} else {
			init.append("this.event(\""+binding.getEvent()+"\","+target+");\n");
		}
	}
	
}

