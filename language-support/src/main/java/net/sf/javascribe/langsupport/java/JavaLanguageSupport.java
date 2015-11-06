package net.sf.javascribe.langsupport.java;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.LanguageSupport;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Scannable;

@Scannable
public class JavaLanguageSupport implements LanguageSupport {

	@Override
	public String languageName() {
		return "Java";
	}

	@Override
	public List<VariableType> getBaseVariableTypes() {
		ArrayList<VariableType> ret = new ArrayList<VariableType>();
		
		ret.add(new Java5BooleanType());
		ret.add(new Java5DateType());
		ret.add(new Java5IntegerType());
		ret.add(new Java5FloatType());
		ret.add(new Java5ListType());
		ret.add(new Java5LongIntType());
		ret.add(new Java5MapType());
		ret.add(new Java5ObjectType());
		ret.add(new Java5StringType());
		ret.add(new Java5TimestampType());
		
		return ret;
	}

}
