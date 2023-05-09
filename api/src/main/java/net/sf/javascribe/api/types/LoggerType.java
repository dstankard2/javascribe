package net.sf.javascribe.api.types;

import net.sf.javascribe.api.Code;

public interface LoggerType extends VariableType {

	Code error(String valueString);
	Code warn(String valueString);
	Code info(String valueString);
	Code debug(String valueString);

}
