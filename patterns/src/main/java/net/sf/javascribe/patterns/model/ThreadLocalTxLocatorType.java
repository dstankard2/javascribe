package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.patterns.model.types.EntityManagerType;

public class ThreadLocalTxLocatorType implements EntityManagerLocator {
	private String pkg;
	private String className;
	private BuildContext buildCtx;
	private EntityManagerType emType;
	
	public ThreadLocalTxLocatorType(String pkg,String className,BuildContext buildCtx,EntityManagerType emType) {
		this.pkg = pkg;
		this.className = className;
		this.buildCtx = buildCtx;
		this.emType = emType;
	}
	
	public EntityManagerType getEntityManagerType() {
		return emType;
	}

	@Override
	public String getImport() {
		return pkg + '.' + className;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("You cannot declare an instance of an EntityManager locator type");
	}

	@Override
	public String getName() {
		return className;
	}

	@Override
	public BuildContext getBuildContext() {
		return buildCtx;
	}

	@Override
	public JavaCode getEntityManager(String var, CodeExecutionContext execCtx) {
		if (var!=null) {
			return new JavaCode(var + " = "+getClassName()+".getEntityManager();\n",getImport());
		} else {
			return new JavaCode(getClassName()+".getEntityManager()", getImport());
		}
	}

	@Override
	public JavaCode releaseEntityManager(String var, CodeExecutionContext execCtx) {
		return new JavaCode(getClassName()+".releaseEntityManager();\n",getImport());
	}

}
