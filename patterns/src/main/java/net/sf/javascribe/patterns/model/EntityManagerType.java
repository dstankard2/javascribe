package net.sf.javascribe.patterns.model;

import java.util.List;

import net.sf.javascribe.langsupport.java.JavaVariableTypeBase;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class EntityManagerType extends JavaVariableTypeBase implements Java5Type {

	private List<String> entityNames = null;
	
	public List<String> getEntityNames() { return entityNames; }
	
	public EntityManagerType(List<String> entityNames,String name) {
		super(name,"javax.persistence","EntityManager");
		this.entityNames = entityNames;
	}
	
	@Override
	public CodeSnippet instantiate(String varName, String value)
			throws CodeGenerationException {
		throw new CodeGenerationException("EntityManager cannot be instantiated.");
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		JsomUtils.merge(ret, super.declare(varName,null));
		return ret;
	}
	/*
	@Override
	public Code instantiate(String name, String value,CodeExecutionContext execCtx) 
	throws JavascribeException {
		// TODO Auto-generated method stub
		throw new JavascribeException("EntityManager cannot be instantiated.");
	}
	@Override
	public Code declare(String name, CodeExecutionContext execCtx) {
		return new JsomJavaCode(declare(name));
	}
	*/

}

