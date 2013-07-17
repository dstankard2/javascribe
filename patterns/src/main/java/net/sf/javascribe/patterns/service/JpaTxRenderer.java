package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.jsom.JavascribeJavaCodeSnippet;
import net.sf.javascribe.patterns.model.EntityManagerLocator;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;

public class JpaTxRenderer implements NestingServiceOperationRenderer {
	JpaTxOperation op = null;
	ProcessorContext ctx = null;
	boolean commit = false;
	EntityManagerLocator locator = null;
	String txRef = null;

	public void setGeneratorContext(ProcessorContext ctx) {
		this.ctx = ctx;
	}

	public JpaTxRenderer(JpaTxOperation op) {
		this.op = op;
	}
	@Override
	public Java5CodeSnippet getCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		if (op.getLocator()==null) {
			throw new JavascribeException("Attribute 'locator' is required for jpaTx Service Operation");
		}
		if (op.getRef()==null) {
			throw new JavascribeException("Attribute 'ref' is required for jpaTx Service Operation");
		}

		commit = false;
		if ((op.getCommit()!=null) && (op.getCommit().equalsIgnoreCase("true"))) {
			commit = true;
		}
		txRef = op.getRef();

		if (!(ctx.getTypes().getType(op.getLocator()) instanceof EntityManagerLocator)) {
			throw new JavascribeException("Could not find tx locator type '"+op.getLocator()+"' or the type was not an Entity Manager Locator");
		}
		locator = (EntityManagerLocator)ctx.getTypes().getType(op.getLocator());

		ret.addImport("javax.persistence.EntityManager");
		ret.append("EntityManager "+txRef+" = null;\n");
		ret.addImport(locator.getImport());
		ret.append("try {\n");
		try {
			ret.merge(new JavascribeJavaCodeSnippet(locator.getEntityManager(txRef, execCtx)));
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while rendering tx service operation",e);
		}
		execCtx.addVariable(txRef, ctx.getAttributeType(txRef));

		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.generator.comps.processor.service.NestingServiceOperationRenderer#endingCode(net.sf.generator.api.CodeExecutionContext)
	 */
	@Override
	public Java5CodeSnippet endingCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();

		// First, commit the transaction if applicable
		if (commit) {
			ret.append(txRef+".getTransaction().commit();\n");
		}

		ret.append("}\ncatch(Exception e) {\ne.printStackTrace();\nreturnValue.setStatus(2);\n}\n");
		ret.append("finally {\ntry {\n"+txRef+".getTransaction().rollback();\n} catch(Exception e) { }\n");
		ret.append("try {"+txRef+".close();\n} catch(Exception e) { }\n");
		try {
			ret.merge(new JavascribeJavaCodeSnippet(locator.unallocateEntityManager(txRef)));
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while ending tx operation",e);
		}
		ret.append("}\n");

		return ret;
	}


}
