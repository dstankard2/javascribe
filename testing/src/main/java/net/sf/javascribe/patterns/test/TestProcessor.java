package net.sf.javascribe.patterns.test;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaVariableTypeImpl;

@Plugin
public class TestProcessor implements ComponentProcessor<TestPattern> {

	@Override
	public void process(TestPattern component, ProcessorContext ctx) throws JavascribeException {
		// TODO Auto-generated method stub
		ctx.getLog().info("Processing TestPattern");
		
		ctx.setLanguageSupport("Java8");

		JavaClassSourceFile src = new JavaClassSourceFile(ctx);
		JavaClassSource cl = src.getSrc();
		cl.setPackage("test");
		cl.setName(component.getName());
		ctx.addSourceFile(src);
		
		ctx.getObject("test");
		
		ctx.addSystemAttribute("password", "string");
		ctx.getSystemAttribute("username");
		
		JavaVariableType t = (JavaVariableType)ctx.getVariableType("TestClass");
		if (t==null) {
			t = new JavaVariableTypeImpl("TestClass", "net.sf.test.TestClass", ctx.getBuildContext());
			ctx.addVariableType(t);
		}
	}

}
