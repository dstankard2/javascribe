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
public class RequireAttributePattern extends Component {

	private String requiredAttribute;

	@Builder.Default
	private int priority = 500;
	
}
