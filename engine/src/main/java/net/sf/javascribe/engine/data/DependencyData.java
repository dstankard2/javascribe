package net.sf.javascribe.engine.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

@Getter
public class DependencyData {

	private Map<String,Set<Integer>> srcDependencies = new HashMap<>();
	private Map<String,Set<Integer>> attributeDependencies = new HashMap<>();
	private Map<String,Set<Integer>> attributeOriginators = new HashMap<>();
	private Map<String,Set<Integer>> objectDependencies = new HashMap<>();
	private Map<String, Map<String,Set<Integer>>> typeDependencies = new HashMap<>();

}
