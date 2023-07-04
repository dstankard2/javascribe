package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.patterns.model.EntityManagerLocator;
import net.sf.javascribe.patterns.model.types.EntityManagerType;
import net.sf.javascribe.patterns.xml.java.service.JpaTxOperation;

public class JpaTxRenderer extends NestingOperationRenderer {
	JpaTxOperation op = null;
	ProcessorContext ctx = null;
	Boolean commit = Boolean.TRUE;
	EntityManagerLocator locatorType = null;
	String txRef = null;

	public JpaTxRenderer(ProcessorContext ctx,JpaTxOperation op) {
		super(ctx);
		this.op = op;
		this.ctx = ctx;
	}
	@Override
	public JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCode();
		String locator = op.getLocator();
		commit = op.getCommit();
		txRef = op.getRef();

		if (locator.trim().length()==0) {
			throw new JavascribeException("Attribute 'locator' is required for jpaTx Service Operation");
		}
		if (txRef.trim().length()==0) {
			throw new JavascribeException("Attribute 'ref' is required for jpaTx Service Operation");
		}
		if (commit==null) {
			commit = Boolean.TRUE;
		}
		
		if (execCtx.getVariableType(txRef)!=null) {
			throw new JavascribeException("jpaTx ref '"+txRef+"' is invalid as there is already a variable with this name in the rule");
		}

		locatorType = JavascribeUtils.getType(EntityManagerLocator.class, locator, ctx);
		EntityManagerType emType = locatorType.getEntityManagerType();

		ret.addImport("javax.persistence.EntityManager");
		ret.appendCodeText("EntityManager "+txRef+" = null;\n");

		ret.appendCodeText("try {\n");
		JavaUtils.append(ret, locatorType.getEntityManager(txRef, execCtx));
		execCtx.addVariable(txRef, emType.getName());

		return ret;
	}

	@Override
	public JavaCode endingCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCode();

		// First, commit the transaction if applicable
		if (commit) {
			ret.appendCodeText(txRef+".getTransaction().commit();\n");
		}
		ret.appendCodeText("}\ncatch(Exception e) {\ne.printStackTrace();\n}\n");
		ret.appendCodeText("finally {\ntry {\n"+txRef+".getTransaction().rollback();\n} catch(Exception e) { }\n");
		//ret.appendCodeText("try {"+txRef+".close();\n} catch(Exception e) { }\n");

		JavaUtils.append(ret, locatorType.releaseEntityManager(txRef, execCtx));
		ret.appendCodeText("}\n");

		return ret;
	}

}
