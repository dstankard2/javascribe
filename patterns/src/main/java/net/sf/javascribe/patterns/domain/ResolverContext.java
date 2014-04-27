package net.sf.javascribe.patterns.domain;

import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;

public interface ResolverContext {

	public List<String> getDependencyNames();
	public JavaCode resolveForAttribute(String attributeName) throws JavascribeException;
	public CodeExecutionContext getExecCtx();
	public Map<String, JavaServiceObjectType> getDependencyRefs();
	public String getSystemAttributeType(String attribute);

}

