package net.sf.javascribe.patterns.model;

import java.util.List;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.CodeSnippet;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5Type;

public class EntityManagerType implements JavaVariableType,Java5Type {

	private String name = null;
	private List<String> entityNames = null;
	
	public String getEntityManagerName() { return name; }
	public List<String> getEntityNames() { return entityNames; }
	
	public EntityManagerType(List<String> entityNames,String name) {
		this.name = name;
		this.entityNames = entityNames;
	}
	
	@Override
	public String getClassName() {
		return "EntityManager";
	}

	@Override
	public String getImport() {
		return "javax.persistence.EntityManager";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CodeSnippet instantiate(String varName, String value)
			throws CodeGenerationException {
		throw new CodeGenerationException("EntityManager cannot be instantiated.");
	}

	@Override
	public Java5CodeSnippet declare(String varName) {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.addImport(getImport());
		ret.append("EntityManager "+varName+" = null;\n");
		
		return ret;
	}
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

}

