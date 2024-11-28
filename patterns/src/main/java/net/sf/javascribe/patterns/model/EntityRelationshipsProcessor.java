package net.sf.javascribe.patterns.model;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.langsupport.java.types.impl.ServiceLocatorImpl;
import net.sf.javascribe.patterns.xml.model.EntityRelationships;
import net.sf.javascribe.patterns.xml.model.SingleRelationship;

// TODO: Not really ready for general usage.
@Plugin
public class EntityRelationshipsProcessor implements ComponentProcessor<EntityRelationships> {

	@Override
	public void process(EntityRelationships component, ProcessorContext ctx) throws JavascribeException {
		String ref = component.getJpaDaoFactoryRef();
		String pu = component.getPersistenceUnit();

		ctx.setLanguageSupport("Java8");
		
		ServiceLocatorImpl factoryType = JavascribeUtils.getTypeForSystemAttribute(ServiceLocatorImpl.class, ref, ctx);
		for(SingleRelationship rel : component.getRel()) {
			handleRelationship(rel, factoryType, ctx);
		}
	}

	private void handleRelationship(SingleRelationship rel, ServiceLocatorImpl factoryType, ProcessorContext ctx) throws JavascribeException {
		String relStr = rel.getValue();
		// List<String> rels = Arrays.asList(" has one ", "has many");
		
		if (relStr.indexOf(" has many ") > 0) {
			handleOneToMany(relStr, factoryType, ctx);
		} else {
			throw new JavascribeException("Found unsupported relationship string '"+relStr+"'");
		}
	}

	private void handleOneToMany(String rel, ServiceLocatorImpl factoryType, ProcessorContext ctx) throws JavascribeException {
		String owner = null;
		String owned = null;
		
		int i = rel.indexOf(" has many ");
		owner = rel.substring(0, i);
		owned = rel.substring(i + 10);
		String ownerDao = owner+"Dao";
		String ownedDao = owned+"Dao";
		
		String upperCamelIdField = owner+"Id";
		String ownerIdField = JavascribeUtils.getLowerCamelName(owner)+"Id";
		String ownedIdField = JavascribeUtils.getLowerCamelName(owner)+"Id";
		JavaServiceType ownedDaoType = JavascribeUtils.getType(JavaServiceType.class, ownedDao, ctx);
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(ownedDaoType.getImport(), ctx, false);
		
		ctx.modifyVariableType(ownedDaoType);
		
		String opName = "getFor"+upperCamelIdField;

		ServiceOperation op = new ServiceOperation(opName);
		op.returnType("list/"+owned);
		JavaCode code = new JavaCode();

		String query = "from "+owned+" where "+ownerIdField+" = :"+ownerIdField;
		String resultStr = "getResultList()";

		code.appendCodeText("return getEntityManager().createQuery(\""+query+"\","+owned+".class)");
		op.addParam(ownerIdField, "integer");
		code.appendCodeText(".setParameter(\""+ownerIdField+"\", "+ownerIdField+")");
		code.appendCodeText("."+resultStr+";\n");
		ownedDaoType.addOperation(op);
		
		JavaClassSource cl = src.getSrc();
		JavaUtils.addServiceOperation(op, code, cl, ctx);
	}

}

