package net.sf.javascribe.patterns.custom;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.Injectable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;

/**
 * A service represents a Java business logic service that is found via a service locator.
 * @author Dave
 */
public class CustomLogicObjectType extends LocatedJavaServiceObjectType implements Injectable {
	
	public CustomLogicObjectType(String locatorClass,String serviceName,String pkg,String className) {
		super(locatorClass,serviceName,pkg,className);
		this.locatorClass = locatorClass;
	}

	@Override
	public JavaCode getInstance(String varName,CodeExecutionContext execCtx) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();

		ret.addImport(getImport());
		ret.appendCodeText(varName+" = new "+locatorClass+"().get"+getName()+"();\n");

		return ret;
	}

}
