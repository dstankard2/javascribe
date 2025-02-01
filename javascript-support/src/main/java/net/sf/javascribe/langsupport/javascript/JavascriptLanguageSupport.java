package net.sf.javascribe.langsupport.javascript;

import java.util.Arrays;
import java.util.List;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.langsupport.LanguageSupport;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.langsupport.javascript.types.ArrayType;
import net.sf.javascribe.langsupport.javascript.types.BooleanType;
import net.sf.javascribe.langsupport.javascript.types.DOMElementType;
import net.sf.javascribe.langsupport.javascript.types.DOMEventType;
import net.sf.javascribe.langsupport.javascript.types.DateType;
import net.sf.javascribe.langsupport.javascript.types.DoubleType;
import net.sf.javascribe.langsupport.javascript.types.FunctionType;
import net.sf.javascribe.langsupport.javascript.types.IntegerType;
import net.sf.javascribe.langsupport.javascript.types.JavascriptType;
import net.sf.javascribe.langsupport.javascript.types.LongintType;
import net.sf.javascribe.langsupport.javascript.types.NodeListType;
import net.sf.javascribe.langsupport.javascript.types.NodeType;
import net.sf.javascribe.langsupport.javascript.types.ObjectType;
import net.sf.javascribe.langsupport.javascript.types.PromiseType;
import net.sf.javascribe.langsupport.javascript.types.StringType;

@Plugin
public class JavascriptLanguageSupport implements LanguageSupport {

	@Override
	public String getLanguageName() {
		return "Javascript";
	}

	@Override
	public List<VariableType> getBaseVariableTypes() {
		return Arrays.asList(
				new ArrayType(), new DOMElementType(), new DOMEventType(), new DoubleType(), 
				new IntegerType(), new NodeListType(), new NodeType(), new ObjectType(), new StringType(),
				PromiseType.noResultPromise(), new LongintType(), new BooleanType(),
				new FunctionType(), new DateType()
		);
	}

	@Override
	public Class<? extends SourceFile> baseSourceFile() {
		return JavascriptSourceFile.class;
	}

	@Override
	public Class<? extends VariableType> baseVariableType() {
		return JavascriptType.class;
	}

}
