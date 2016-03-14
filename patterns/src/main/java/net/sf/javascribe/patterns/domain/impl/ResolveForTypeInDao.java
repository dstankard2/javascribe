package net.sf.javascribe.patterns.domain.impl;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.patterns.domain.Resolver;
import net.sf.javascribe.patterns.domain.ResolverContext;
import net.sf.javascribe.patterns.model.DataAccessService;

import org.apache.log4j.Logger;

@Scannable
public class ResolveForTypeInDao implements Resolver {

	public static final Logger log = Logger.getLogger(ResolveForTypeInDao.class);

	@Override
	public String name() {
		return "resolveTypeInDao";
	}

	@Override
	public JavaCode resolve(String attribute,ResolverContext ctx) throws JavascribeException {
		CodeExecutionContext execCtx = ctx.getExecCtx();
		List<String> depNames = ctx.getDependencyNames();
		String targetType = ctx.getSystemAttributeType(attribute);
		
		for(String depName : depNames) {
			JavaServiceObjectType obj = ctx.getDependencyRefs().get(depName);
			if (obj instanceof DataAccessService) {
				JavaCode code = attemptResolve(attribute,targetType,depName,obj,execCtx);
				if (code!=null) {
					return code;
				}
			}
		}
		
		return null;
	}
	
	private JavaCode attemptResolve(String attribute,String targetType,String objName,JavaServiceObjectType obj,CodeExecutionContext execCtx) throws JavascribeException {
		List<JavaOperation> ops = obj.getMethods();
		for(JavaOperation op : ops) {
			if (op.getReturnType()==null) continue;
			if (!op.getReturnType().equals(targetType)) continue;
			boolean fit = true;
			List<String> paramNames = op.getParameterNames();
			for(String s : paramNames) {
				if (execCtx.getVariableType(s)==null) {
					fit = false;
					break;
				}
			}
			if (!fit) continue;
			JavaCode code = JavaUtils.callJavaOperation(attribute, objName, op, execCtx, null);
			return code;
		}
		return null;
	}

}

