package net.sf.javascribe.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.TypeResolver;
import net.sf.javascribe.api.VariableType;

public class TypeResolverImpl implements TypeResolver {
	Map<String,VariableType> types = new HashMap<String,VariableType>();
	Map<String,VariableType> baseTypes = new HashMap<String,VariableType>();
	List<Supp> supports = new ArrayList<Supp>();
	
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
		if (ret==null) {
			ret = baseTypes.get(name);
		}
		return ret;
	}
	
	public void setLanguageSupport(String language,String name) {
		for(Supp s : supports) {
			if ((s.lang.equals(language)) && (s.name.equals(name))) {
				baseTypes = s.baseTypes;
			}
		}
		Supp s = new Supp();
		s.lang = language;
		s.name = name;
		baseTypes = s.baseTypes;
		supports.add(s);
	}
	
	public void addBaseType(VariableType type) {
		baseTypes.put(type.getName(), type);
	}
	
}

class Supp {
	String lang = null;
	String name = null;
	Map<String,VariableType> baseTypes = new HashMap<String,VariableType>();
}

