package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class RetrieveDataRuleProcessor {

	Logger log = Logger.getLogger(RetrieveDataRuleProcessor.class);
	
	@ProcessorMethod(componentClass=RetrieveDataRule.class)
	public void process(RetrieveDataRule comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");

		DomainLogicCommon.ensureFinalizer(ctx);
		
		// Read business object name
		String serviceObjName = DomainLogicCommon.getServiceObj(comp, ctx);

		// Read rule name
		String ruleName = comp.getRule();
		if (ruleName.trim().length()==0) {
			throw new JavascribeException("Attribute 'ruleName' is required on component Retrieve Data Rule");
		}

		log.info("Processing retrieve data rule '"+serviceObjName+"."+ruleName+"'");

		// Read parameters as attributes
		List<Attribute> params = DomainLogicCommon.getParams(comp, ctx);

		// Read rule return type
		String returnAttribute = comp.getReturnAttribute();
		if (returnAttribute.trim().length()==0) {
			throw new JavascribeException("Attribute 'returnAttribute' is required on component Retrieve Data Rule");
		}

		// Read rule dependencies
		List<String> deps = new ArrayList<String>();

		// Dependancies are read from the comp and then from configuration properties.

		if (comp.getDependencies().trim().length()>0) {
			StringTokenizer tok = new StringTokenizer(comp.getDependencies().trim(),",");
			while(tok.hasMoreTokens()){
				String s = tok.nextToken();
				if (!deps.contains(s)) deps.add(s);
			}
		}
		if (ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES)!=null) {
			String s = ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES);
			StringTokenizer tok = new StringTokenizer(s,",");
			while(tok.hasMoreTokens()){
				String a = tok.nextToken();
				if (!deps.contains(a)) deps.add(a);
			}
		}
		
		// Get source file for domain service
		DomainLogicFile serviceFile = DomainLogicCommon.getDomainObjectFile(serviceObjName, ctx);
		LocatedJavaServiceObjectType serviceType = DomainLogicCommon.getDomainObjectType(serviceObjName, ctx);

		List<String> objDeps = serviceFile.getDependencies();

		String returnType = ctx.getAttributeType(returnAttribute);
		if (returnType==null) {
			throw new JavascribeException("Couldn't recognize return attribute '"+returnAttribute+"'");
		}

		try {

			Map<String,JavaServiceObjectType> dependencyRefs = new HashMap<String,JavaServiceObjectType>();

			Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
			method.setName(comp.getRule());
			method.setType(returnType);
			Java5CodeSnippet code = new Java5CodeSnippet();
			method.setMethodBody(code);
			CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());
			for(Attribute p : params) {
				method.addArg(p.getType(), p.getName());
				execCtx.addVariable(p.getName(), p.getType());
			}

			serviceFile.getPublicClass().addMethod(method);
			serviceType.addMethod(JsomUtils.createJavaOperation(method));
			
			// Add dependencies
			for(String name : deps) {
				String typeName = ctx.getAttributeType(name);
				VariableType type = ctx.getType(typeName);
				DomainLogicCommon.addDependency(name, serviceType, serviceFile, ctx);
				if (type instanceof JavaServiceObjectType) {
					JavaServiceObjectType obj = (JavaServiceObjectType)type;
					if (!objDeps.contains(name)) {
						objDeps.add(name);
					}
					dependencyRefs.put(name, obj);
				} else if (type instanceof ServiceLocator) {
					ServiceLocator loc = (ServiceLocator)type;
					if (!objDeps.contains(name)) {
						objDeps.add(name);
					}
					for(String srv : loc.getAvailableServices()) {
						String ref = loc.getService(name, srv, execCtx);
						JavaServiceObjectType t = (JavaServiceObjectType)ctx.getType(srv);
						dependencyRefs.put(ref, t);
					}
				}
				execCtx.addVariable(name, typeName);
			}

			String strategyName = comp.getStrategy();
			if (strategyName.trim().length()==0) {
				strategyName = ctx.getProperty(RetrieveDataRule.RESOLVE_RULE_STRATEGY);
			}
			
			if ((strategyName==null) || (strategyName.trim().length()==0)) {
				throw new JavascribeException("Retrieve Data Rule requires a reference to a strategy in attribute 'strategy' or property '"+RetrieveDataRule.RESOLVE_RULE_STRATEGY+"'");
			}
			
			List<Resolver> strategy = RetrieveDataStrategyProcessor.findStrategy(ctx, strategyName);
			ResolverContextImpl res = new ResolverContextImpl(ctx,dependencyRefs,execCtx,strategy);
			JavaCode resolveCode = res.runResolve(returnAttribute);
			if (resolveCode==null) {
				throw new JavascribeException("Couldn't resolve retrieve data rule");
			}
			
			resolveCode.appendCodeText("return "+returnAttribute+";\n");
			log.debug("Found code as: \n"+resolveCode.getCodeText());
			JsomUtils.merge(code, resolveCode);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("Got a JSOM exception",e);
		}

	}

}

