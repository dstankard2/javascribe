package net.sf.javascribe.patterns.translator;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

@Scannable
@Processor
public class DataObjectTranslatorProcessor {

	public static final String DATA_OBJECT_TRANSLATOR_PKG = "net.sf.javascribe.patterns.translator.DataObjectTranslator.pkg";
	
	@ProcessorMethod(componentClass=DataObjectTranslator.class)
	public void process(DataObjectTranslator comp,GeneratorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		
		String returnType = comp.getReturnType();
		JavaBeanType resultType = null;
		Java5SourceFile src = null;
		JavaServiceObjectType type = null;

		System.out.println("Generating translator named "+comp.getObject()+'.'+comp.getName());
		
		if (returnType==null) throw new JavascribeException("Data Translator requires a return type");
		VariableType var = ctx.getTypes().getType(returnType);
		if (var==null) {
			throw new JavascribeException("Data Translator type '"+returnType+"' is not recognized");
		}
		if (!(var instanceof JavaBeanType)) {
			throw new JavascribeException("Result '"+returnType+"' is not a data object");
		}

		try {
			String pkg = ctx.getRequiredProperty(DATA_OBJECT_TRANSLATOR_PKG);
			pkg = JavaUtils.findPackageName(ctx, pkg);
			String className = comp.getObject();

			src = JsomUtils.getJavaFile(pkg+'.'+className, ctx);
			if (src==null) {
				src = JsomUtils.createJavaSourceFile(ctx);
				src.setPackageName(pkg);
				src.getPublicClass().setClassName(className);
				JsomUtils.addJavaFile(src, ctx);
				type = new JavaServiceObjectType(className,pkg,className);
				type.setPkg(pkg);
				ctx.getTypes().addType(type);
				ctx.addAttribute(JavascribeUtils.getLowerCamelName(className), className);
			} else {
				type = (JavaServiceObjectType)ctx.getType(className);
			}

			resultType = (JavaBeanType)var;

			List<Attribute> inputs = JavascribeUtils.readAttributes(ctx, comp.getParams());
			List<String> attribs = resultType.getAttributeNames();

			CodeExecutionContext execCtx = new CodeExecutionContext(null, ctx.getTypes());
			Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
			Java5CodeSnippet body = new Java5CodeSnippet();
			method.setMethodName(comp.getName());
			method.setReturnType(resultType.getName());
			method.setMethodBody(body);
			src.getPublicClass().addMethod(method);
			for(Attribute i : inputs) {
				execCtx.addVariable(i.getName(), i.getType());
				method.addArg(i.getType(), i.getName());
			}
			type.addMethod(JsomUtils.createJavaOperation(method));

			execCtx.addVariable("_ret", resultType.getName());
			JsomUtils.merge(body, (JavaCode)resultType.declare("_ret", execCtx));
			JsomUtils.merge(body, (JavaCode)resultType.instantiate("_ret", null, execCtx));

			for(String a : attribs) {
				boolean done = false;
				// Check inputs for a match
				for(Attribute att : inputs) {
					if (att.getName().equals(a)) {
						done = true;
						body.append(resultType.getCodeToSetAttribute("_ret", a, att.getName(), execCtx));
						body.append(";\n");
					}
				}
				// For Java bean types in inputs, check their attributes
				for(Attribute inp : inputs) {
					VariableType t = ctx.getType(inp.getType());
					AttributeHolder holder = (AttributeHolder)t;
					if (holder.getAttributeType(a)!=null) {
						done = true;
						String retrieve = holder.getCodeToRetrieveAttribute(inp.getName(), a, "object", execCtx);
						body.append(resultType.getCodeToSetAttribute("_ret", a, retrieve, execCtx));
						body.append(";\n");
					}
				}
				// No translator found, throw an exception
				if (!done)
					throw new JavascribeException("Couldn't find any translation for field '"+a+"' in result type");
			}
			// Return the result
			body.append("return _ret;\n");
		} catch(CodeGenerationException e) {
			throw new JavascribeException("CodeGenerationException while processing data object translator",e);
		}
	}

}

