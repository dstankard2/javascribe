package net.sf.javascribe.patterns.translator.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.patterns.translator.FieldTranslator;
import net.sf.javascribe.patterns.translator.FieldTranslatorContext;

@Scannable
public class DependencyScanTranslator implements FieldTranslator {

	public static final String DATA_OBJECT_TRANSLATOR_DEPENDENCIES = "net.sf.javascribe.patterns.translator.DataObjectTranslator.dependencies";

	@Override
	public String name() {
		return "dependencyScan";
	}

	@Override
	public JavaCode getAttribute(String attributeName, String attributeType, String targetVariable, FieldTranslatorContext translatorCtx) throws JavascribeException {
		ProcessorContext ctx = translatorCtx.getCtx();
		CodeExecutionContext execCtx = translatorCtx.getExecCtx();

		String depString = ctx.getRequiredProperty(DATA_OBJECT_TRANSLATOR_DEPENDENCIES);
		
		HashMap<String,JavaServiceObjectType> refs = new HashMap<String,JavaServiceObjectType>();
		StringTokenizer tok = new StringTokenizer(depString,",");
		
		while(tok.hasMoreTokens()) {
			String s = tok.nextToken();
			String typeName = ctx.getAttributeType(s);
			if (typeName==null) {
				throw new JavascribeException("Found an invalid dependency for a translator: '"+s+"'");
			}
			JavaVariableType type = (JavaVariableType)execCtx.getType(typeName);
			if (type==null) {
				throw new JavascribeException("Found an invalid dependency for a translator: '"+s+"'");
			}
			
			if (type instanceof ServiceLocator) {
				ServiceLocator locator = (ServiceLocator)type;
				String inst = locator.instantiate();
				
				for(String service : locator.getAvailableServices()) {
					String ref = locator.getService(inst, service, execCtx);
					JavaServiceObjectType srv = (JavaServiceObjectType)ctx.getObject(service);
					refs.put(ref, srv);
				}
			} else if (type instanceof LocatedJavaServiceObjectType) {
				LocatedJavaServiceObjectType srv = (LocatedJavaServiceObjectType)type;
				String ref = srv.getAnonymousInstance();
				refs.put(ref,srv);
			} else if (type instanceof JavaServiceObjectType) {
				JavaServiceObjectType srv = (JavaServiceObjectType)type;
				String ref = srv.instantiate();
				refs.put(ref, srv);
			}

		}
		
		for(String ref : refs.keySet()) {
			JavaServiceObjectType srv = refs.get(ref);
			JavaCode result = tryServiceObject(targetVariable,srv,attributeName,attributeType,ref,execCtx);
			if (result!=null) {
				return result;
			}
		}

		return null;
	}

	private JavaCode tryServiceObject(String resultVariable,JavaServiceObjectType srv,String attribute,String attributeType,String serviceRef,CodeExecutionContext execCtx) throws JavascribeException {
		String name = "get"+Character.toUpperCase(attribute.charAt(0))+attribute.substring(1);
		
		for(JavaOperation op : srv.getMethods()) {
			if (!op.getName().equals(name)) {
				continue;
			}
			if (!op.getReturnType().equals(attributeType)) continue;
			JavaCode result = attemptInvoke(serviceRef,op,resultVariable,execCtx);
			if (result!=null) {
				return result;
			}
		}
		
		return null;
	}
	
	private JavaCode attemptInvoke(String serviceRef,JavaOperation op,String resultVar,CodeExecutionContext execCtx) {
		JavaCode ret = null;
		try {
			ret = JavaUtils.callJavaOperation(resultVar, serviceRef, op, execCtx, null);
		} catch(Exception e) {
			return null;
		}
		return ret;
	}

}

