package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsom.VariableTypeResolver;
import net.sf.jsom.java5.Java5SourceFile;

public class DomainLogicFile extends Java5SourceFile {

	public DomainLogicFile(VariableTypeResolver r) { 
		super(r);
	}

	private String implementationFile = null;
	private List<String> dependencies = new ArrayList<String>();
	
	public List<String> getDependencies() {
		return dependencies;
	}

	public String getImplementationFile() {
		return implementationFile;
	}
	public void setImplementationFile(String implementationFile) {
		this.implementationFile = implementationFile;
	}
	
}
