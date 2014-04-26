package net.sf.javascribe.patterns.model;

import net.sf.javascribe.langsupport.java.JavaServiceObjectType;

public class DataAccessJavaServiceObjectType extends JavaServiceObjectType implements DataAccessService {

	public DataAccessJavaServiceObjectType(String name,String pkg,String className) {
		super(name,pkg,className);
	}

}
