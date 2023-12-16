package net.sf.javascribe.patterns.java.domain;

import java.util.List;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.domain.HandwrittenRule;

@Plugin
public class HandwrittenRuleProcessor implements ComponentProcessor<HandwrittenRule> {

	@Override
	public void process(HandwrittenRule comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		JavaClassSourceFile src = DomainRuleUtils.getServiceSourceFile(comp, ctx);
		JavaServiceType serviceType = DomainRuleUtils.getServiceType(comp, ctx);

		String returnTypeName = comp.getReturnType();
		String params = comp.getParams();
		String ruleName = comp.getRule();

		if (ruleName.trim().length()==0) {
			throw new JavascribeException("No rule specified for handwritten rule");
		}

		if (!src.getSrc().isAbstract()) {
			throw new JavascribeException("Handwritten rule requires the domain service to be an abstract class - you must specify configuration 'java.domain.implClass'");
		}
		
		JavaVariableType returnType = null;
		if (returnTypeName.trim().length()>0) {
			returnType = JavascribeUtils.getType(JavaVariableType.class, returnTypeName, ctx);
		}
		
		ServiceOperation op = new ServiceOperation(ruleName);
		MethodSource<JavaClassSource> methodSrc = src.getSrc().addMethod();

		methodSrc.setPublic().setAbstract(true).setName(ruleName);
		if (returnType!=null) {
			src.addImport(returnType);
			methodSrc.setReturnType(returnType.getClassName());
			op.returnType(returnTypeName);
		}
		
		List<PropertyEntry> ruleParams = JavascribeUtils.readParametersAsList(params, ctx);
		for(PropertyEntry param : ruleParams) {
			JavaVariableType paramType = (JavaVariableType)param.getType();
			String paramName = param.getName();
			src.addImport(paramType);
			methodSrc.addParameter(paramType.getClassName(), paramName);
			op.addParam(paramName, paramType.getName());
		}
		serviceType.addOperation(op);
		ctx.modifyVariableType(serviceType);
	}
	
}

