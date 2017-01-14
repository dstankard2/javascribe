package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.TextSourceFile;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableTypeImpl;
import net.sf.javascribe.patterns.xml.model.ThreadLocalTxLocator;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class ThreadLocalTxLocatorProcessor {
	ProcessorContext ctx = null;
	ThreadLocalTxLocator component = null;
	String pu = null;
	String name = null;
	String pkg = null;
	String className = null;

	private static final Logger log = Logger.getLogger(ThreadLocalTxLocatorProcessor.class);
	
	@ProcessorMethod(componentClass=ThreadLocalTxLocator.class)
	public void process(ThreadLocalTxLocator comp,ProcessorContext ctx) throws JavascribeException {
		this.ctx = ctx;
		ctx.setLanguageSupport("Java");
		component = comp;
		pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(ModelUtils.MODEL_PKG_PROPERTY));
		pu = component.getPu();
		name = component.getName();
		className = pu+"ThreadLocalTxLocator";
		if (pu==null)
			throw new JavascribeException("Found null PU name for ThreadLocalTxLocator");
		if (name==null) 
			throw new JavascribeException("Found null name for Tx Locator");

		log.info("Processing Thread Local TX Locator '"+name+"'");
		
		ThreadLocalTxLocatorType type = new ThreadLocalTxLocatorType(pu,pkg,className,name);
		if (ctx.getTypes().getType(type.getName())!=null) {
			throw new JavascribeException("There are multiple entity manager locators for pu "+pu);
		}
		ctx.getTypes().addType(type);
		if (ctx.getType("EntityManagerFactory")==null) {
			ctx.getTypes().addType(new JavaVariableTypeImpl("EntityManagerFactory","javax.persistence","EntityManagerFactory"));
		}

		TextSourceFile src = new TextSourceFile();
		src.setPath(JavaUtils.getJavaFilePath(ctx, pkg+'.'+className));
		src.getSource().append(TX_LOCATOR_TEMPLATE);
		replace(src.getSource(),"${pkg}",pkg);
		replace(src.getSource(),"${className}",className);
		replace(src.getSource(),"${pu}",pu);
		ctx.addSourceFile(src);
	}
	
	private void replace(StringBuilder b,String search,String replacement) {
		int index = b.indexOf(search);
		while(index>0) {
			b.replace(index, index+search.length(), replacement);
			index = b.indexOf(search);
		}
	}

	private static final String TX_LOCATOR_TEMPLATE = "package ${pkg};\n"+
			"import javax.persistence.EntityManagerFactory;\n"+
			"import javax.persistence.EntityManager;\n"+
			"public class ${className}  {\n"+
			"static EntityManagerFactory entityManagerFactory;\n"+
			"static ThreadLocal<EntityManager> currentTx = new ThreadLocal<EntityManager>();\n"+
			"public static EntityManager getEntityManager() {\n"+
			"EntityManager ret = null;\n"+
			"ret = currentTx.get();\n"+
			"if (ret==null) {\n"+
			"ret = entityManagerFactory.createEntityManager();\n"+
			"ret.getTransaction().begin();\n"+
			"currentTx.set(ret);\n"+
			"}\n"+
			"return ret;\n"+
			"}\n"+
			"static {\n"+
			"try {\n"+
			"entityManagerFactory = javax.persistence.Persistence.createEntityManagerFactory(\"${pu}\");\n"+
			"} catch(Throwable e) { e.printStackTrace(); }"+
			"}\n"+
			"public static void releaseEntityManager() {\n"+
			"currentTx.remove();\n"+
			"}\n"+
			"}";
	
}

