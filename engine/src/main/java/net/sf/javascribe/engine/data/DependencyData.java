package net.sf.javascribe.engine.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class DependencyData {

	private Map<String,List<Integer>> srcDependencies = new HashMap<>();
	private Map<String,List<Integer>> attributeDependencies = new HashMap<>();
	private Map<String,List<Integer>> attributeOriginators = new HashMap<>();
	private Map<String,List<Integer>> objectDependencies = new HashMap<>();
	private Map<String, Map<String,List<Integer>>> typeDependencies = new HashMap<>();

}
