package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.patterns.model.EntityManagerLocator;
import net.sf.javascribe.patterns.model.types.EntityManagerType;
import net.sf.javascribe.patterns.xml.java.service.JpaTxOperation;

public class JpaTxRenderer extends OperationRenderer {
	JpaTxOperation op = null;

	public JpaTxRenderer(JpaTxOperation op) {
		this.op = op;
	}

	@Override
	public void render(RendererContext ctx) throws JavascribeException {
		CodeExecutionContext execCtx = ctx.execCtx();
		JavaCode code = ctx.getCode();
		
		String locator = op.getLocator();
		boolean commit = op.getCommit();
		String txRef = op.getRef();
		EntityManagerLocator locatorType = null;

		if (locator.trim().length()==0) {
			throw new JavascribeException("Attribute 'locator' is required for jpaTx Service Operation");
		}
		if (txRef.trim().length()==0) {
			throw new JavascribeException("Attribute 'ref' is required for jpaTx Service Operation");
		}

		locatorType = JavascribeUtils.getType(EntityManagerLocator.class, locator, ctx.ctx());
		EntityManagerType emType = locatorType.getEntityManagerType();

		
		// Create the TX ref
		if (execCtx.getVariableType(txRef)==null) {
			code.addImport("javax.persistence.EntityManager");
			code.appendCodeText("EntityManager "+txRef+" = null;\n");
			execCtx.addVariable(txRef, emType.getName());
		}

		code.appendCodeText("try {\n");
		JavaUtils.append(code, locatorType.getEntityManager(txRef, execCtx));
		CodeExecutionContext newExecCtx = new CodeExecutionContext(execCtx);
		ctx.handleNesting(newExecCtx);

		// First, commit the transaction if applicable
		if (commit) {
			code.appendCodeText(txRef+".getTransaction().commit();\n");
		}
		code.appendCodeText("}\ncatch(Exception e) {\ne.printStackTrace();\n}\n");
		code.appendCodeText("finally {\ntry {\n"+txRef+".getTransaction().rollback();\n} catch(Exception e) { }\n");

		JavaUtils.append(code, locatorType.releaseEntityManager(txRef, execCtx));
		code.appendCodeText("}\n");
		
	}

}
