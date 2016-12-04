package net.sf.javascribe.patterns.lookups;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaVariableTypeBase;
import net.sf.javascribe.langsupport.java.ServiceLocator;

public class LookupsLocator extends JavaVariableTypeBase implements ServiceLocator {
	List<String> lookups = new ArrayList<String>();
	
	public List<String> getLookups() {
		return lookups;
	}
	
	public LookupsLocator(String pkg,String className) {
		super(className,pkg,className);
	}

	@Override
	public List<String> getAvailableServices() {
		return lookups;
	}

	@Override
	public String getService(String factoryInstanceRef, String serviceName,
			CodeExecutionContext execCtx) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		
		build.append(factoryInstanceRef+".get"+serviceName+"();\n");

		return build.toString();
	}

}
