package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
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
		List<Attribute> deps = null;
		String depNames = null;
		if (comp.getDependencies().trim().length()>0) {
			depNames = comp.getDependencies();
			deps = JavascribeUtils.readAttributes(ctx, depNames);
		} else if (ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES)!=null) {
			depNames = ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES);
			deps = JavascribeUtils.readAttributes(ctx, depNames);
		} else {
			deps = new ArrayList<Attribute>();
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
			for(Attribute s : deps) {
				VariableType type = ctx.getType(s.getType());
				if (type instanceof LocatedJavaServiceObjectType) {
					LocatedJavaServiceObjectType obj = (LocatedJavaServiceObjectType)type;
					if (!objDeps.contains(s.getName())) {
						objDeps.add(s.getName());
					}
					execCtx.addVariable(s.getName(), s.getType());
					dependencyRefs.put(s.getName(), obj);
				} else if (type instanceof JavaServiceObjectType) {
					JavaServiceObjectType obj = (JavaServiceObjectType)type;
					if (!objDeps.contains(s.getName())) {
						objDeps.add(s.getName());
					}
					execCtx.addVariable(s.getName(), s.getType());
					dependencyRefs.put(s.getName(), obj);
				} else if (type instanceof ServiceLocator) {
					ServiceLocator loc = (ServiceLocator)type;
					if (!objDeps.contains(s.getName())) {
						objDeps.add(s.getName());
					}
					execCtx.addVariable(s.getName(), s.getType());
					for(String srv : loc.getAvailableServices()) {
						String ref = loc.getService(s.getName(), srv, execCtx);
						JavaServiceObjectType t = (JavaServiceObjectType)ctx.getType(srv);
						dependencyRefs.put(ref, t);
					}
				} else {
					throw new JavascribeException("Found a dependency that is not a service object or service locator");
				}
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
				throw new JavascribeException("Couldn't resolve retrieve data rule.");
			}
			
			resolveCode.appendCodeText("return "+returnAttribute+";\n");
			log.debug("Found code as: \n"+resolveCode.getCodeText());
			JsomUtils.merge(code, resolveCode);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("Got a JSOM exception",e);
		}

	}

}

