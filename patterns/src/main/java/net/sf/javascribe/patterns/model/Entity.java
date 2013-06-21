package net.sf.javascribe.patterns.model;

import java.util.HashMap;
import java.util.Map;


public class Entity {

	public Map<String,EntityAttrib> attributes = new HashMap<String,EntityAttrib>();
	public String versionField = null;
	public String name = null;
	public String lowerCamelName = null;

}

