package net.sf.javascribe.patterns.domain;

import java.util.List;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.java5.Java5CompatibleCodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5MethodSignature;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class DomainServiceFinalizer {

	@ProcessorMethod(componentClass=DomainObjectFinalizer.class)
	public void process(DomainObjectFinalizer comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		
		System.out.println("Finalizing Domain Service "+comp.getServiceObjectName());
		
		LocatedJavaServiceObjectType type = 
				(LocatedJavaServiceObjectType)ctx.getType(comp.getServiceObjectName());

		Java5SourceFile src = JsomUtils.getJavaFile(type.getLocatorClass(), ctx);
		List<String> names = src.getPublicClass().getMethodNames();

		for(String n : names) {
			Java5MethodSignature sig = src.getPublicClass().getDeclaredMethod(n);
			
			if (sig.getReturnType().equals(comp.getServiceObjectName())) {
				Java5DeclaredMethod method = (Java5DeclaredMethod)sig;
				Java5CompatibleCodeSnippet code = method.getMethodBody();
				code.append("return _service;\n");
			}
		}
	}

}

