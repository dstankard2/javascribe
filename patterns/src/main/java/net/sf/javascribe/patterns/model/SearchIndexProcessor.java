package net.sf.javascribe.patterns.model;

import java.util.List;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.ServiceLocator;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.model.SearchIndex;

@Plugin
public class SearchIndexProcessor implements ComponentProcessor<SearchIndex> {

	@Override
	public void process(SearchIndex comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		ServiceLocator loc = JavascribeUtils.getTypeForSystemAttribute(ServiceLocator.class, comp.getJpaDaoFactoryRef(), ctx);
		String entity = comp.getEntity();
		String serviceName = entity+"Dao";
		
		if (!loc.getAvailableServices().contains(serviceName)) {
			throw new JavascribeException("Couldn't find DAO for entity '"+entity+"'");
		}
		
		JavaServiceType daoType = JavascribeUtils.getType(JavaServiceType.class, serviceName, ctx);
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(daoType.getImport(), ctx, false);
		String name = comp.getName();
		Boolean multiple = comp.getMultiple();
		String paramString = comp.getParams();
		String query = comp.getQueryString();

		ServiceOperation op = new ServiceOperation(name);
		JavaCode code = new JavaCode();
		if (multiple==Boolean.TRUE) {
			op.returnType("list/"+entity);
		} else {
			op.returnType(entity);
			multiple = Boolean.FALSE;
		}
		List<PropertyEntry> attribs = JavascribeUtils.readParametersAsList(paramString, ctx);
		String resultStr = (multiple) ? "getResultList()" : "getSingleResult()";
		code.appendCodeText("return getEntityManager().createQuery(\""+query+"\","+entity+".class)");
		
		for(PropertyEntry attrib : attribs) {
			op.addParam(attrib.getName(), attrib.getType().getName());
			code.appendCodeText(".setParameter(\""+attrib.getName()+"\","+attrib.getName()+")");
		}
		code.appendCodeText("."+resultStr+";\n");
		JavaClassSource cl = src.getSrc();
		JavaUtils.addServiceOperation(op, code, cl, ctx);
	}
	
}

