package net.sf.javascribe.langsupport.java.jsom;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.TypeResolver;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.jsom.VariableType;
import net.sf.jsom.VariableTypeResolver;


// A wrapper around the Javascribe type resolver that JSOM can access.
public class JavascribeVariableTypeResolver extends VariableTypeResolver {
	TypeResolver types = null;
	
	public JavascribeVariableTypeResolver(TypeResolver types) {
		this.types = types;
	}
	
	public JavascribeVariableTypeResolver(GeneratorContext ctx) {
		this.types = ctx.getTypes();
	}
	
	@Override
	public VariableType getVariableType(String name) {
		JavaVariableType var = (JavaVariableType)types.getType(name);
		if (var==null) return null;
		return new JavascribeVariableType(var);
	}

	@Override
	public void addVariableType(VariableType type) {
	}
	
}

