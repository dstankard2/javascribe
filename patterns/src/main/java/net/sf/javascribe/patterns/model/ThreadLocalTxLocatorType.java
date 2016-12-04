package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.JavaVariableTypeBase;

public class ThreadLocalTxLocatorType extends JavaVariableTypeBase implements EntityManagerLocator {
	String pu = null;

	public ThreadLocalTxLocatorType(String pu,String pkg,String className,String name) {
		super(name,pkg,className);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx) {
		throw new RuntimeException("ThreadLocalTxLocator does not support instantiate");
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx) {
		throw new RuntimeException("ThreadLocalTxLocator does not support declare");
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

