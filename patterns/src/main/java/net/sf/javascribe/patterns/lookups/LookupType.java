package net.sf.javascribe.patterns.lookups;

import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.patterns.xml.lookups.FieldValue;

public class LookupType extends LocatedJavaServiceObjectType {
	private HashMap<String,List<FieldValue>> fields = new HashMap<String,List<FieldValue>>();
	
	public LookupType(String locatorClass,String pkg,String lookupClass) {
		super(locatorClass,lookupClass,pkg,lookupClass);
	}
	
	public void addField(String name,List<FieldValue> values) {
		fields.put(name, values);
	}
	
	public List<FieldValue> getFieldValues(String name) {
		return fields.get(name);
	}
	
}

