package net.sf.javascribe.api.langsupport;

import java.util.List;

import net.sf.javascribe.api.SourceFile;
import net.sf.javascribe.api.types.VariableType;

public interface LanguageSupport {

	public String getLanguageName();

	public List<VariableType> getBaseVariableTypes();

	public Class<? extends SourceFile> baseSourceFile();

	public Class<? extends VariableType> baseVariableType();

}
