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
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.JavaVariableType;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassDefinition;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class RetrieveDataRuleProcessor {

	Logger log = Logger.getLogger(RetrieveDataRuleProcessor.class);
	
	@ProcessorMethod(componentClass=RetrieveDataRule.class)
	public void process(RetrieveDataRule comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");

		// Read service locator name
		String serviceLocatorName = DomainLogicCommon.getServiceLocatorName(comp, ctx);

		// Read business object name
		String serviceObjName = DomainLogicCommon.getServiceObj(comp, ctx);

		// Read rule name
		String ruleName = DomainLogicCommon.getRule(comp, ctx);

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
		} else if (ctx.getProperty(RetrieveDataRule.DOMAIN_LOGIC_DEPENDENCIES)!=null) {
			depNames = ctx.getProperty(RetrieveDataRule.DOMAIN_LOGIC_DEPENDENCIES);
			deps = JavascribeUtils.readAttributes(ctx, depNames);
		} else {
			deps = new ArrayList<Attribute>();
		}

		// Get locator file and type
		Java5SourceFile locatorFile = DomainLogicCommon.getServiceLocatorFile(serviceLocatorName, ctx);
		Java5ClassDefinition locatorClass = locatorFile.getPublicClass();
		
		// Get package for domain logic
		String pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(DomainLogicCommon.DOMAIN_LOGIC_PKG));

		try {
			Java5SourceFile serviceFile = null;
			LocatedJavaServiceObjectType serviceType = null;
			Java5CompatibleCodeSnippet locatorCode = null;

			// Ensure service exists and get locator method in service locator
			Java5DeclaredMethod locatorMethod = null;
			serviceFile = JsomUtils.getJavaFile(pkg+'.'+serviceObjName, ctx);
			if (serviceFile==null) {
				// Add service file and create method on service locator
				serviceFile = new Java5SourceFile(new JavascribeVariableTypeResolver(ctx));
				serviceFile.setPackageName(pkg);
				serviceFile.getPublicClass().setClassName(serviceObjName);
				JsomUtils.addJavaFile(serviceFile, ctx);
				serviceType = new LocatedJavaServiceObjectType(pkg+'.'+locatorClass.getClassName(),serviceObjName,pkg,serviceObjName);
				ctx.getTypes().addType(serviceType);

				// Add locator method for service
				locatorMethod = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx));
				locatorMethod.setStatic(true);
				locatorMethod.setMethodName("get"+serviceObjName);
				locatorMethod.setReturnType(serviceObjName);
				locatorClass.addMethod(locatorMethod);
				locatorCode = new Java5CodeSnippet();
				locatorMethod.setMethodBody(locatorCode);
				locatorCode.merge(JsomUtils.toJsomCode(serviceType.declare("_service")));
				locatorCode.merge(JsomUtils.toJsomCode(serviceType.instantiate("_service", null)));
				locatorCode.append("return _service;\n");
			} else {
				serviceType = (LocatedJavaServiceObjectType)ctx.getType(serviceObjName);
				locatorMethod = (Java5DeclaredMethod)locatorClass.getDeclaredMethod("get"+serviceObjName);
				locatorCode = locatorMethod.getMethodBody();
			}

			Map<String,JavaServiceObjectType> dependencyRefs = new HashMap<String,JavaServiceObjectType>();

			String returnType = ctx.getAttributeType(returnAttribute);
			if (returnType==null) {
				throw new JavascribeException("Couldn't recognize return attribute '"+returnAttribute+"'");
			}
			Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
			method.setMethodName(comp.getRule());
			method.setReturnType(returnType);
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
					JsomUtils.merge(code, obj.declare(s.getName()));
					JsomUtils.merge(code, obj.locateService(s.getName(),execCtx));
					execCtx.addVariable(s.getName(), s.getType());
					dependencyRefs.put(s.getName(), obj);
				} else if (type instanceof JavaServiceObjectType) {
					JavaServiceObjectType obj = (JavaServiceObjectType)type;
					JsomUtils.merge(code, obj.declare(s.getName()));
					JsomUtils.merge(code, obj.instantiate(s.getName(),null));
					execCtx.addVariable(s.getName(), s.getType());
					dependencyRefs.put(s.getName(), obj);
				} else if (type instanceof ServiceLocator) {
					ServiceLocator loc = (ServiceLocator)type;
					execCtx.addVariable(s.getName(), s.getType());
					JsomUtils.merge(code, (JavaCode)loc.declare(s.getName(), execCtx));
					JsomUtils.merge(code, (JavaCode)loc.instantiate(s.getName(), null, execCtx));
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
			throw new JavascribeException("JSOM exception while processing domain rule",e);
		}
	}
}
