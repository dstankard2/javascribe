package net.sf.javascribe.langsupport.javascript;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.LanguageSupport;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class JavascriptLanguageSupport implements LanguageSupport {

	@Override
	public String languageName() {
		return "Javascript";
	}

	@Override
	public List<VariableType> getBaseVariableTypes() {
		ArrayList<VariableType> ret = new ArrayList<VariableType>();
		
		ret.add(new ObjectVariableType());
		ret.add(new StringVariableType());
		ret.add(new ListVariableType());
		ret.add(new IntegerVariableType());
		ret.add(new BooleanVariableType());
		ret.add(new NodeListVariableType());
		ret.add(new NodeVariableType());
		
		return ret;
	}

}
