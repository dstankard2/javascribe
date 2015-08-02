package net.sf.javascribe.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.api.expressions.ValueExpression;

/**
 * Represents the current context while generating a particular code 
 * function.  The context contains the currently declared variables and 
 * types that are available.
 * @author DCS
 *
 */
public class CodeExecutionContext {
	private HashMap<String,String> variables = new HashMap<String,String>();
	private TypeResolver types = null;
	
	public List<String> getVariableNames() {
		List<String> ret = new ArrayList<String>();
		
		for(String k : variables.keySet()) {
			ret.add(k);
		}
		
		return ret;
	}

	/**
	 * Creates a new context with the same variables declared as the specified 
	 * parent.  Adding variables to the new context will not affect the parent 
	 * context.  This is handy for emulating new variable scopes from "{" in 
	 * some languages.
	 * @param parent
	 * @param types
	 */
	public CodeExecutionContext(CodeExecutionContext parent,TypeResolver types) {
		this.types = types;
		if (parent!=null) {
			this.types = parent.types;
			variables.putAll(parent.variables);
			
		}
	}
	
	public CodeExecutionContext(CodeExecutionContext parent) {
		this(parent,parent.getTypes());
	}
	
	/**
	 * Adds a variable with the given name and the given type.
	 * @param name
	 * @param type
	 */
	public void addVariable(String name,String type) {
		variables.put(name,type);
	}

	/**
	 * Get the currently defined variable types.
	 * @return Currently available types.
	 */
	public TypeResolver getTypes() {
		return types;
	}
	
	public VariableType getType(String name) {
		return types.getType(name);
	}
	
	public String getVariableType(String var) {
		return variables.get(var);
	}
	
	/**
	 * Returns the variable type for the given variable.
	 * @param var
	 * @return Type of the specified variable, or null.
	 */
	public VariableType getTypeForVariable(String var) {
		String type = variables.get(var);
		if (type!=null) {
			return types.getType(type);
		}

		return null;
	}
	
	public String evaluateTypeForExpression(String expr) throws JavascribeException {
		ValueExpression ex = ExpressionUtil.buildValueExpression("${"+expr+"}", null, this);
		return ex.getType();
	}
	public VariableType evaluateVariableTypeForExpression(String expr) throws JavascribeException {
		ValueExpression ex = ExpressionUtil.buildValueExpression("${"+expr+"}", null, this);
		String typeName = ex.getType();
		return getType(typeName);
	}
	
}

