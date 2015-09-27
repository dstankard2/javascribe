package net.sf.javascribe.engine;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.TypeResolver;
import net.sf.javascribe.api.VariableType;

public class TypeResolverImpl implements TypeResolver {
	Map<String,VariableType> types = new HashMap<String,VariableType>();
	
	public void addType(VariableType type) throws JavascribeException {
		if (types.get(type.getName())!=null) {
			throw new JavascribeException("Type '"+type.getName()+"' is already defined");
		}
		types.put(type.getName(), type);
	}
	
	public void addOrReplaceType(VariableType type) {
		types.put(type.getName(), type);
	}

	public VariableType getType(String name) {
		if (name.startsWith("list/")) return getType("list");
		VariableType ret = types.get(name);
		return ret;
	}

}

