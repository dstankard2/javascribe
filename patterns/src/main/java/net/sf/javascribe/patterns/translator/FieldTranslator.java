package net.sf.javascribe.patterns.translator;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface FieldTranslator {

	public String name();
	
	public JavaCode getAttribute(String attributeName,String attributeType,String targetVariable,FieldTranslatorContext translatorCtx) throws JavascribeException;

}

