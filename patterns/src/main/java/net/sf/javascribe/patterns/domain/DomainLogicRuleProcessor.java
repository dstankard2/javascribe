package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5MethodSignature;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class DomainLogicRuleProcessor {

	private static final Logger log = Logger.getLogger(DomainLogicRuleProcessor.class);

	@ProcessorMethod(componentClass=DomainLogicRule.class)
	public void processDomainLogicRule(DomainLogicRule comp,ProcessorContext ctx) throws JavascribeException {
		// Set language to Java
		ctx.setLanguageSupport("Java");

		DomainLogicCommon.ensureFinalizer(ctx);

		String domainSrv = DomainLogicCommon.getServiceObj(comp, ctx);

		if (domainSrv==null) {
			throw new JavascribeException("Couldn't find domain service class name");
		}

		// Read rule name
		String ruleName = comp.getRule();
		if (ruleName.trim().length()==0) {
			throw new JavascribeException("Attribute 'ruleName' is required on component Retrieve Data Rule");
		}

		log.info("Processing domain logic rule "+domainSrv+"."+ruleName);
		
		// Read parameters as attributes
		List<Attribute> params = DomainLogicCommon.getParams(comp, ctx);

		String returnType = comp.getReturnType();

		// Read rule dependencies
		ArrayList<String> deps = new ArrayList<String>();
		if (ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES)!=null) {
			String depNames = ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES);
			StringTokenizer tok = new StringTokenizer(depNames,",");
			while(tok.hasMoreTokens()) {
				deps.add(tok.nextToken());
			}
		}



		DomainLogicFile file = DomainLogicCommon.getDomainObjectFile(domainSrv, ctx);
		LocatedJavaServiceObjectType type = DomainLogicCommon.getDomainObjectType(domainSrv, ctx);

		file.getPublicClass().setAbstract(true);

		try {

			Java5MethodSignature sig = new Java5MethodSignature(new JavascribeVariableTypeResolver(ctx));
			sig.setAccessLevel("public");
			sig.setName(ruleName);
			if (returnType.trim().length()>0) {
				sig.setType(returnType);
			}
			for(Attribute param : params) {
				sig.addArg(param.getType(), param.getName());
			}
			file.getPublicClass().addMethod(sig);
			type.addMethod(JsomUtils.createJavaOperation(sig));
			
			// Add dependencies to the type and source file
			for(String dep : deps) {
				DomainLogicCommon.addDependency(dep, type, file, ctx);
/*
				if (!type.getDependancyNames().contains(dep)) {
					type.addDependancy(dep);
					String upperCamel = JavascribeUtils.getUpperCamelName(dep);
					String typeName = ctx.getAttributeType(dep);
					JavaVariableType t = (JavaVariableType)ctx.getType(typeName);
					Java5DeclaredMethod setter = JsomUtils.createMethod(ctx);
					setter.setName("set"+upperCamel);
					setter.addArg(typeName, "_d");
					file.getPublicClass().addMemberVariable(dep, typeName, null);
					Java5CodeSnippet code = new Java5CodeSnippet();
					setter.setMethodBody(code);
					code.append("this."+dep+" = _d;");
					file.getPublicClass().addMethod(setter);
				}
				*/
			}

		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while building business object.",e);
		}

	}

}

