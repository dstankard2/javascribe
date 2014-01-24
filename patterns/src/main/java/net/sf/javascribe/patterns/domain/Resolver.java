package net.sf.javascribe.patterns.domain;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface Resolver {

	public String name();
	public JavaCode resolve(String attribute,ResolverContext ctx) throws JavascribeException;
	
}

