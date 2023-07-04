package net.sf.javascribe.patterns.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.javascribe.api.config.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequireTypePattern extends Component {

	@Builder.Default
	private String lang = "";

	private String requiredType;

	@Builder.Default
	private int priority = 500;
	
}
