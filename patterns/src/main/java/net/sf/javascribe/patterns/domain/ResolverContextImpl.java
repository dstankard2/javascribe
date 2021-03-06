package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableType;

public class ResolverContextImpl implements ResolverContext {
	ProcessorContext ctx = null;
	Map<String,JavaServiceObjectType> dependencyRefs = null;
	CodeExecutionContext execCtx = null;
	List<Resolver> strategy = null;
	int level = 0;
	
	Logger log = Logger.getLogger(ResolverContextImpl.class);

	private ResolverContextImpl(ResolverContextImpl impl) {
		this.ctx = impl.ctx;
		this.dependencyRefs = impl.dependencyRefs;
		this.execCtx = new CodeExecutionContext(impl.execCtx,ctx.getTypes());
		this.strategy = impl.strategy;
		this.level = impl.level+1;
	}
	
	public ResolverContextImpl(ProcessorContext ctx,Map<String,JavaServiceObjectType> dependencyRefs,
			CodeExecutionContext execCtx,List<Resolver> strategy) {
		this.execCtx = execCtx;
		this.ctx = ctx;
		this.dependencyRefs = dependencyRefs;
		this.strategy = strategy;
		this.level = 1;
	}
	
	public int getLevel() {
		return level;
	}
	
	public JavaCode runResolve(String attributeName) throws JavascribeException {
		JavaCode declare = null;
		JavaCode resolvedCode = null;
		
		// If the attribute is already resolved, return empty code
		if (execCtx.getVariableType(attributeName)!=null) {
			return new JavaCodeImpl();
		}

		String typeName = ctx.getAttributeType(attributeName);
		if (typeName==null) return null;
		JavaVariableType type = (JavaVariableType)ctx.getType(typeName);
		if ((type instanceof ListType) && (typeName.startsWith("list/"))) {
			String name = typeName.substring(5);
			ListType l = (ListType)type;
			declare = (JavaCode)l.declare(attributeName, name, execCtx);
		} else {
			declare = (JavaCode)type.declare(attributeName, execCtx);
		}
		execCtx.addVariable(attributeName, typeName);
		
		for(Resolver op : strategy) {
			resolvedCode = op.resolve(attributeName, this);
			if (resolvedCode!=null) {
				JavaUtils.append(declare, resolvedCode);
				return declare;
			}
		}
		
		log.debug("Level "+level+": Couldn't resolve for attribute '"+attributeName+"'");
		
		return null;
	}
	
	public JavaCode resolveForAttribute(String attributeName) throws JavascribeException {
		if (level > 5) {
			return null;
		}
		JavaCode ret = null;
		
		ResolverContextImpl impl = new ResolverContextImpl(this);
		ret = impl.runResolve(attributeName);
		if (ret!=null) {
			this.execCtx = impl.execCtx;
		}
		return ret;
	}
	
	public List<String> getDependencyNames() {
		List<String> ret = new ArrayList<String>();
		
		for(String s : dependencyRefs.keySet()) {
			ret.add(s);
		}
		
		return ret;
	}

	public CodeExecutionContext getExecCtx() {
		return execCtx;
	}

	public Map<String, JavaServiceObjectType> getDependencyRefs() {
		return dependencyRefs;
	}
	
	public String getSystemAttributeType(String name) {
		return ctx.getAttributeType(name);
	}

}

