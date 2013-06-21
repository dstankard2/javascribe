package net.sf.javascribe.patterns.model;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaUtils;

public class ModelUtils {
	
	public static final String MODEL_PKG_PROPERTY = "net.sf.javascribe.patterns.model.pkg";
	
	public static String getDaoFactoryTypeName(String entityManagerName) {
		return entityManagerName+"DaoFactory";
	}

	public static String getDaoPackage(GeneratorContext ctx) throws JavascribeException {
		return JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(JpaDaoFactoryProcessor.DAO_PACKAGE_PROPERTY));
	}
	
	public static EntityManagerLocator getDefaultEntityManagerLocator(String pu,GeneratorContext ctx) throws JavascribeException {
		String name = ctx.getRequiredProperty("net.sf.javascribe.patterns.model.EntityManagerComponent.defaultEntityManagerLocator."+pu);
		EntityManagerLocator loc = (EntityManagerLocator)ctx.getType(name);
		
		return loc;
	}

}
