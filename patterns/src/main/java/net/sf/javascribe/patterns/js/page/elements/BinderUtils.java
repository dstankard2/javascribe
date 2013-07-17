package net.sf.javascribe.patterns.js.page.elements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.js.page.ElementBinderEntry;


public class BinderUtils {

	private static final String ELEMENT_BINDERS = "net.sf.javascribe.patterns.js.page.Binding.binders";

	// If target is a model attribute or an attribute of a model attribute ("obj.property")
	// then this method returns the string to access it.  Otherwise, it is assumed that 
	// target is a function and code to call the function is called.
	public static String getTargetAccessString(String target,ElementBinderContext ctx,boolean functionOnly) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		String modelObj = null;
		String objAttrib = null;
		
		int index = target.indexOf('.');
		if (index>=0) {
			modelObj = target.substring(0, index);
			objAttrib = target.substring(index+1);
		} else {
			modelObj = target;
		}
		
		if ((!functionOnly) && (ctx.getModelAttributeType(modelObj)!=null)) {
			String upper = Character.toUpperCase(modelObj.charAt(0))+modelObj.substring(1);
			if (objAttrib!=null) {
				build.append("this.model.get"+upper+"()."+objAttrib);
			} else {
				build.append("this.model.get"+upper+"()");
			}
		} else {
			if (ctx.getPageType().getAttributeType(target)!=null) {
				build.append("this."+target+"()");
			} else {
				build.append(target+"()");
			}
		}
		
		return build.toString();
	}
	
	public static String getAttributeToBindTo(String ref) {
		String ret = ref;
		
		if (ret.indexOf('.')>=0) {
			ret = ret.substring(0, ret.indexOf('.'));
		}
		
		return ret;
	}
	
	private static String getModelAttributeFromTarget(String target,ElementBinderContext ctx) {
		String ret = target;
		int index = target.indexOf('.');
		
		if (index>=0) {
			ret = ret.substring(0, index);
		}
		if (ctx.getModelAttributeType(ret)==null) ret = null;
		
		return ret;
	}
	
	public static String getEventToTrigger(String target,String bindingEvent,ElementBinderContext ctx, boolean functionOnly) throws JavascribeException {
		String ret = null;
		String attrib = null;

		attrib = getModelAttributeFromTarget(target, ctx);
		
		if ((functionOnly) && (attrib!=null)) {
			throw new JavascribeException("This type of binding may not have target as a model attribute");
		} else if (attrib!=null) {
			ret = target+"Changed";
		} else {
			if ((functionOnly) && (bindingEvent.trim().length()==0)) {
				throw new JavascribeException("This type of binding must have an event specified");
			}
			ret = bindingEvent;
		}
		
		return ret;
	}
	
	public static String getSetExpression(String field,String value) {
		String ret = null;
		
		if (field.indexOf('.')>0) {
			int index = field.indexOf('.');
			ret = "get"+Character.toUpperCase(field.charAt(0))+field.substring(1, index)
					+"()."+field.substring(index+1)+" = "+value;
		} else {
			ret = "set"+Character.toUpperCase(field.charAt(0))+field.substring(1)+"("+value+")";
		}
		return ret;
	}
	
	public static String getGetter(String value) {
		String ret = null;
		
		if (value.indexOf('.')>0) {
			int index = value.indexOf('.');
			ret = "get"+Character.toUpperCase(value.charAt(0))+value.substring(1, index)
					+"()."+value.substring(index+1);
		} else {
			ret = "get"+Character.toUpperCase(value.charAt(0))+value.substring(1)+"()";
		}
		return ret;
	}
	
	public static Map<String,ElementBinderEntry> getElementBinders(GeneratorContext ctx) throws JavascribeException {
		Map<String,ElementBinderEntry> ret = null;
		
		ret = (Map<String,ElementBinderEntry>)ctx.getObject(ELEMENT_BINDERS);
		
		if (ret==null) {
			ret = new HashMap<String,ElementBinderEntry>();
			ctx.putObject(ELEMENT_BINDERS, ret);
			List<Class<?>> classes = ctx.getEngineProperties().getScannedClassesOfAnnotation(ElementBinder.class);
			for(Class<?> cl : classes) {
				ElementBinder b = cl.getAnnotation(ElementBinder.class);
				ElementBinderEntry e = ret.get(b.elementType());
				if (e==null) {
					e = new ElementBinderEntry(cl);
					ret.put(b.elementType(), e);
				} else {
					throw new JavascribeException("Found multiple element binders for element type '"+b.elementType()+"'");
				}
				Method[] methods = cl.getMethods();
				for(Method m : methods) {
					if (m.isAnnotationPresent(ElementBinding.class)) {
						ElementBinding eb = m.getAnnotation(ElementBinding.class);
						if (e.getBindings().get(eb.bindingType())!=null) {
							m = findCorrectBindingMethod(cl,m,e.getBindings().get(eb.bindingType()));
						}
						e.getBindings().put(eb.bindingType(), m);
					} else if (m.isAnnotationPresent(BindToPage.class)) {
						e.setBindToPage(m);
					}
				}
			}
		}
		
		return ret;
	}
	
	private static Method findCorrectBindingMethod(Class<?> cl,Method method1,Method method2) throws JavascribeException {
		if (method1.getDeclaringClass()==method2.getDeclaringClass()) {
			throw new JavascribeException("Cannot define 2 binding methods that generate the same binding type, as is done in "+cl.getCanonicalName());
		}
		else if (method1.getDeclaringClass()==cl) {
			return method1;
		} else if (method2.getDeclaringClass()==cl) {
			return method2;
		}
		return findCorrectBindingMethod(cl.getSuperclass(),method1,method2);
	}
	
}

