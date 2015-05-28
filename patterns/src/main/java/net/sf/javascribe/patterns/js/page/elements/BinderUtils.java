package net.sf.javascribe.patterns.js.page.elements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.ElementBinderEntry;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;


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
			if (ctx.getPageType().hasOperation(target)) {
				build.append("this."+target+"()");
			} else {
				build.append(target+"()");
			}
		}
		
		return build.toString();
	}
	
	public static JavascriptCode evalTarget(String target,String resultVar,ElementBinderContext ctx) throws JavascribeException {
		JavascriptCode ret = new JavascriptCode(true);
		CodeExecutionContext execCtx = createExecutionContext(ctx,ret);
		String obj = null;
		String attr = null;
		int i = target.indexOf('.');
		PageType pageType = ctx.getPageType();
		PageModelType modelType = ctx.getModelType();
		
		if (i>=0) {
			obj = target.substring(0,i);
			attr = target.substring(i+1);
		} else {
			attr = target;
		}
		boolean done = false;
		if (obj==null) {
			// Look for a model attribute if there is a result var
			if ((resultVar!=null) && (modelType!=null) 
					&& (modelType.getAttributeType(target)!=null)) {
				ret.append(resultVar+" = ");
				ret.append(modelType.getCodeToRetrieveAttribute("this.model", target, null, execCtx));
				ret.append(";\n");
				done = true;
			}
			// Look for a function on the page that has the right name
			if ((!done) && (pageType.hasOperation(target))) {
				JavascriptFunctionType fn = null;
				for(JavascriptFunctionType o : pageType.getOperations()) {
					if (o.getName().equals(target)) {
						fn = o;
						break;
					}
				}
				if (resultVar!=null) {
					ret = JavascriptUtils.invokeFunction(resultVar, "this", fn, execCtx);
					if (ret!=null) done = true;
					else ret = new JavascriptCode(true);
				} else if (resultVar==null) {
					done = true;
					ret = JavascriptUtils.invokeFunction(null, "this", fn, execCtx);
				}
			}
		} else {
			// Check if this is an attribute of the model
			if ((resultVar!=null) && (modelType!=null) 
					&& modelType.getAttributeType(obj)!=null) {
				JavascriptBaseObjectType attrType = (JavascriptBaseObjectType)ctx.getCtx().getType(modelType.getAttributeType(obj));
				if ((attrType!=null) && (attrType.getAttributeType(attr)!=null)) {
					done = true;
					String objRef = modelType.getCodeToRetrieveAttribute("this.model", obj, null, execCtx);
					ret.append("if ("+objRef+"==null) "+resultVar+" = null;\n");
					ret.append("else "+resultVar+" = ");
					ret.append(attrType.getCodeToRetrieveAttribute(objRef, attr, null, execCtx));
					ret.append(";\n");
				}
			}
			// Check if we're supposed to invoke a function on a known type
			if ((!done) && (execCtx.getVariableType(obj)!=null)) {
				JavascriptBaseObjectType srv = (JavascriptBaseObjectType)execCtx.getTypeForVariable(obj);
				for(JavascriptFunctionType fn : srv.getOperations()) {
					ret = JavascriptUtils.invokeFunction(resultVar, obj, fn, execCtx);
					if (ret!=null) {
						done = true;
						break;
					}
				}
			}
			// Check if we should invoke a function on a type on the window.
			if ((!done) && (ctx.getCtx().getAttributeType(obj)!=null)) {
				String typeName = ctx.getCtx().getAttributeType(obj);
				JavascriptBaseObjectType type = (JavascriptBaseObjectType)ctx.getCtx().getType(typeName);
				if (type.getAttributeType(attr)!=null) {
					done = true;
					ret.append(resultVar+" = ");
					ret.append(type.getCodeToRetrieveAttribute(obj, attr, "object", execCtx));
					ret.append(";\n");
				}
				if (!done) {
					for(JavascriptFunctionType op : type.getOperations()) {
						if (!op.getName().equals(attr)) continue;
						JavascriptCode c = JavascriptUtils.invokeFunction(resultVar, obj, op, execCtx);
						if (c!=null) {
							ret.merge(c);
							done = true;
							break;
						}
					}
				}
			}
		}
		// If still not done, invoke a zero-arg function on the window.
		if (!done) {
			if (resultVar!=null) {
				ret.append(resultVar+" = ");
			}
			if (obj!=null) {
				ret.append(obj+".");
			}
			ret.append(attr+"();\n");
		}
		
		return ret;
	}
	
	// Creates a code execution context with the page, model, view and page model attributes.
	protected static CodeExecutionContext createExecutionContext(ElementBinderContext ctx,JavascriptCode src) throws JavascribeException {
		CodeExecutionContext ret = new CodeExecutionContext(null,ctx.getCtx().getTypes());
		PageType page = ctx.getPageType();
		
		ret.addVariable("page", page.getName());
		src.append("var page = this;\n");
		if (page.getAttributeType("model")!=null) {
			ret.addVariable("model",page.getAttributeType("model"));
			src.append("var model = this.model;\n");
			PageModelType model = PageUtils.getModelType(ctx.getCtx(), ctx.getPageName());
			for(String n : model.getAttributeNames()) {
				if (ret.getType(n)==null) {
					ret.addVariable(n, model.getAttributeType(n));
					src.append("var "+n+" = "+model.getCodeToRetrieveAttribute("this.model", n, "object", ret)+";\n");
				}
			}
		}
		if (page.getAttributeType("view")!=null)
			ret.addVariable("this.view", page.getAttributeType("view"));
		
		return ret;
	}

	public static String getAttributeToBindTo(String ref) {
		String ret = ref;
		
		if (ret.indexOf('.')>=0) {
			ret = ret.substring(0, ret.indexOf('.'));
		}
		
		return ret;
	}
	
	private static String getModelAttributeFromTarget(String target,PageModelType modelType) {
		String ret = target;
		int index = target.indexOf('.');
		
		if (index>=0) {
			ret = ret.substring(0, index);
		}
		if (modelType.getAttributeType(ret)==null) 
			ret = null;
		
		return ret;
	}
	
	public static String getEventToTrigger(String target,String event,ElementBinderContext ctx) throws JavascribeException {
		String ret = null;
		String attrib = null;
		PageModelType modelType = ctx.getModelType();

		if ((event!=null) && (event.trim().length()>0)) { // easy!
			ret = event;
		}
		else {
			attrib = getModelAttributeFromTarget(target, modelType);
			if (attrib!=null) {
				ret = attrib+"Changed";
			}
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
	
	public static Map<String,ElementBinderEntry> getElementBinders(ProcessorContext ctx) throws JavascribeException {
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

