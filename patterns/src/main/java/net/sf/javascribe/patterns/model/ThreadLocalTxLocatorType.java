package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;

public class ThreadLocalTxLocatorType implements EntityManagerLocator {
	String pu = null;
	String className = null;
	String pkg = null;
	String name = null;

	public ThreadLocalTxLocatorType(String pu,String pkg,String className,String name) {
		this.pu = pu;
		this.pkg = pkg;
		this.className = className;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("ThreadLocalTxLocator does not support instantiate");
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("ThreadLocalTxLocator does not support declare");
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getImport() {
		return pkg+'.'+className;
	}

	@Override
	public JavaCode getEntityManager(String varName,CodeExecutionContext execCtx) {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(varName+" = "+className+".getEntityManager();\n");
		
		return ret;
	}
	
	@Override
	public JavaCode unallocateEntityManager(String varName) {
		JavaCodeImpl ret = new JavaCodeImpl();
		
		ret.addImport(getImport());
		ret.appendCodeText(getClassName()+".releaseEntityManager();\n");
		
		return ret;
	}

}

