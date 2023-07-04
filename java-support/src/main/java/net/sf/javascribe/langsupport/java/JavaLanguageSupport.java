package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.langsupport.LanguageSupport;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.BooleanJavaType;
import net.sf.javascribe.langsupport.java.types.impl.LocalDateJavaType;
import net.sf.javascribe.langsupport.java.types.impl.IntegerJavaType;
import net.sf.javascribe.langsupport.java.types.impl.JavaListType;
import net.sf.javascribe.langsupport.java.types.impl.LongJavaType;
import net.sf.javascribe.langsupport.java.types.impl.ObjectJavaType;
import net.sf.javascribe.langsupport.java.types.impl.StringJavaType;
import net.sf.javascribe.langsupport.java.types.impl.LocalDateTimeJavaType;

@Plugin
public class JavaLanguageSupport implements LanguageSupport {

	@Override
	public String getLanguageName() {
		return "Java8";
	}

	@Override
	public List<VariableType> getBaseVariableTypes() {
		List<VariableType> ret = new ArrayList<>();
		ret.add(new IntegerJavaType());
		ret.add(new LongJavaType());
		ret.add(new StringJavaType());
		ret.add(new LocalDateTimeJavaType());
		ret.add(new LocalDateJavaType());
		ret.add(new JavaListType());
		ret.add(new BooleanJavaType());
		ret.add(new ObjectJavaType());
		return ret;
	}

	@Override
	public Class<? extends SourceFile> baseSourceFile() {
		return JavaClassSourceFile.class;
	}

	@Override
	public Class<? extends VariableType> baseVariableType() {
		return JavaVariableType.class;
	}

}
