package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
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
		List<Attribute> deps = null;
		String depNames = null;
		if (ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES)!=null) {
			depNames = ctx.getProperty(DomainLogicCommon.DOMAIN_LOGIC_DEPENDENCIES);
			deps = JavascribeUtils.readAttributes(ctx, depNames);
		} else {
			deps = new ArrayList<Attribute>();
		}



		// Either the domain logic class is an interface or an abstract class
		boolean isInterface = false;

		DomainLogicFile file = DomainLogicCommon.getDomainObjectFile(domainSrv, ctx);
		LocatedJavaServiceObjectType type = (LocatedJavaServiceObjectType)ctx.getType(domainSrv);

		// If the file is already an interface it will continue to be
		if (file.getPublicClass().isInterface()) {
			isInterface = true;
		}

		// If the file didn't exist before and has no methods, it will be an interface
		if (file.getPublicClass().getMethodNames().size()==0) {
			isInterface = true;
			file.getPublicClass().setInterface(true);
		}

		// If the file is not an interface, it is abstract
		if (isInterface==false) {
			file.getPublicClass().setAbstract(true);
		}

		try {

			Java5MethodSignature sig = new Java5MethodSignature(new JavascribeVariableTypeResolver(ctx));
			sig.setAccessLevel("public");
			sig.setMethodName(ruleName);
			if (returnType.trim().length()>0) {
				sig.setReturnType(returnType);
			}
			for(Attribute param : params) {
				sig.addArg(param.getType(), param.getName());
			}
			file.getPublicClass().addMethod(sig);
			type.addMethod(JsomUtils.createJavaOperation(sig));

		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while building business object.",e);
		}

	}

}

