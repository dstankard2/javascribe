package net.sf.javascribe.langsupport.java.types.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.BuildContext;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JasperUtils;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.DataObjectType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.types.JavaVariableTypeBase;

public class JavaDataObjectType extends JavaVariableTypeBase implements DataObjectType {
	protected List<String> attributes = new ArrayList<>();
	protected Map<String,String> attribMap = new HashMap<>();
	protected List<String> superTypes = new ArrayList<>();
	protected boolean isInterface = false;
	
	public void addProperty(String name,String type) {
		attributes.add(name);
		attribMap.put(name, type);
	}
	
	@Override
	public String getCodeToRetrieveAttribute(String varName, String attribName, String targetType,
			CodeExecutionContext execCtx) throws IllegalArgumentException, JavascribeException {
		String upperCamel = JasperUtils.getUpperCamelName(attribName);
		return varName+".get"+upperCamel+"()";
	}

	@Override
	public String getCodeToSetAttribute(String varName, String attribName, String evaluatedValue,
			CodeExecutionContext execCtx) throws JavascribeException {
		String upperCamel = JasperUtils.getUpperCamelName(attribName);
		return varName+".set"+upperCamel+"("+evaluatedValue+")";
	}

	@Override
	public String getAttributeType(String attrib) throws JavascribeException {
		return attribMap.get(attrib);
	}

	@Override
	public List<String> getAttributeNames() {
		return attributes;
	}

	public JavaDataObjectType(String cl,String im,BuildContext buildCtx) {
		super(cl,im,buildCtx);
	}
	
	@Override
	public JavaCode instantiate(String name) {
		return new JavaCode(name+" = new "+getClassName()+"();\n");
	}

	@Override
	public List<String> getSuperTypes() {
		return superTypes;
	}

	public boolean getIsInterface() {
		return isInterface;
	}

	public void setIsInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

}
