package net.sf.javascribe.patterns.model.types;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.patterns.model.TableSet;

public class EntityManagerType implements DatabaseManagerType {
	private TableSet tableSet = null;
	private String puName = null;
	private BuildContext buildCtx = null;

	public boolean getIsInterface() {
		return true;
	}

	public EntityManagerType(String puName,TableSet tableSet,BuildContext buildCtx) {
		this.puName = puName;
		this.tableSet = tableSet;
		this.buildCtx = buildCtx;
	}
	
	public String getPersistenceUnitName() {
		return puName;
	}
	
	public TableSet getTableSet() {
		return tableSet;
	}
	
	@Override
	public BuildContext getBuildContext() {
		return buildCtx;
	}

	@Override
	public String getImport() {
		return "javax.persistence.EntityManager";
	}

	@Override
	public String getClassName() {
		return "EntityManager";
	}

	@Override
	public JavaCode declare(String name, CodeExecutionContext execCtx) throws JavascribeException {
		return new JavaCode("EntityManager "+name+";\n",getImport());
	}

	@Override
	public String getName() {
		return puName + "EntityManager";
	}

	@Override
	public JavaCode rollback(String dbVarRef, CodeExecutionContext execCtx) {
		return new JavaCode(dbVarRef+".getTransaction().rollback();\n");
	}

	@Override
	public JavaCode commit(String dbVarRef, CodeExecutionContext execCtx) {
		return new JavaCode(dbVarRef+".getTransaction().commit();\n");
	}

	@Override
	public JavaCode close(String dbVarRef, CodeExecutionContext execCtx) {
		return new JavaCode(dbVarRef+".close();\n");
	}

	@Override
	public JavaCode setReadOnly(String dbVarRef,CodeExecutionContext execCtx) {
		return new JavaCode(dbVarRef+".getTransaction().setRollbackOnly();\n");
	}

}
