package net.sf.javascribe.patterns.model;

import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;

public class DataAccessLocatedJavaServiceObject extends LocatedJavaServiceObjectType implements DataAccessService {

	public DataAccessLocatedJavaServiceObject(String locatorClass,String serviceName,String pkg,String className) {
		super(locatorClass,serviceName,pkg,className);
	}

}

