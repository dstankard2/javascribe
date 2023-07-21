package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.sf.javascribe.api.types.ServiceOperation;

@Getter
@Setter
public class PageInfo {

	private String name = null;
	private String pageRendererObj = null;
	private ServiceOperation pageRendererRule = null;
	// TODO: Consider replacing page functions with a more specific web service client object
	private List<PageFnDef> functions = new ArrayList<>();
	private String modelTypeName = null;
	private String pageTypeName = null;
	private Set<String> importedRefs = new HashSet<>();

}
