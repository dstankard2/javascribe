package net.sf.javascribe.patterns.translator;

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
import net.sf.javascribe.langsupport.java.JavaBeanType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.javascribe.patterns.xml.translator.DataObjectTranslator;
import net.sf.javascribe.patterns.xml.translator.TranslationStrategy;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class DataObjectTranslatorProcessor {

	private static final Logger log = Logger.getLogger(DataObjectTranslatorProcessor.class);

	public static final String DATA_OBJECT_TRANSLATOR_PKG = "net.sf.javascribe.patterns.translator.DataObjectTranslator.pkg";

	// Process translation strategies.  They must be cached in the GeneratorContext
	@ProcessorMethod(componentClass=TranslationStrategy.class)
	public void process(TranslationStrategy comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		if (comp.getName().trim().length()==0) {
			throw new JavascribeException("A translation strategy must have a 'name' attribute");
		}
		DataObjectTranslatorUtils.storeTranslationStrategy(comp, ctx);
	}
	
	@ProcessorMethod(componentClass=DataObjectTranslator.class)
	public void process(DataObjectTranslator comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java");
		
		String returnType = comp.getReturnType();
		JavaBeanType resultType = null;
		Java5SourceFile src = null;
		JavaServiceObjectType type = null;

		log.info("Generating translator named "+comp.getObject()+'.'+comp.getName());
		
		if (returnType==null) throw new JavascribeException("Data Translator requires a return type");
		VariableType var = ctx.getTypes().getType(returnType);
		if (var==null) {
			throw new JavascribeException("Data Translator type '"+returnType+"' is not recognized");
		}
		if (!(var instanceof JavaBeanType)) {
			throw new JavascribeException("Result '"+returnType+"' is not a data object");
		}
		if (comp.getStrategy().trim().length()==0) {
			throw new JavascribeException("Found no translation strategy for data translator");
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
				ctx.getTypes().addType(type);
				ctx.addAttribute(JavascribeUtils.getLowerCamelName(className), className);
			} else {
				type = (JavaServiceObjectType)ctx.getType(className);
			}

			resultType = (JavaBeanType)var;

			List<Attribute> inputs = JavascribeUtils.readAttributes(ctx, comp.getParams());

			CodeExecutionContext execCtx = new CodeExecutionContext(null, ctx.getTypes());
			Java5DeclaredMethod method = new Java5DeclaredMethod(new JavascribeVariableTypeResolver(ctx.getTypes()));
			Java5CodeSnippet body = new Java5CodeSnippet();
			method.setName(comp.getName());
			method.setType(resultType.getName());
			method.setMethodBody(body);
			src.getPublicClass().addMethod(method);
			Map<String,String> inputMap = new HashMap<String,String>();
			for(Attribute i : inputs) {
				execCtx.addVariable(i.getName(), i.getType());
				method.addArg(i.getType(), i.getName());
				inputMap.put(i.getName(), i.getType());
			}
			type.addMethod(JsomUtils.createJavaOperation(method));

			//execCtx.addVariable("_ret", resultType.getName());
			JsomUtils.merge(body, (JavaCode)resultType.declare("_ret", execCtx));
			JsomUtils.merge(body, (JavaCode)resultType.instantiate("_ret", null, execCtx));

			List<FieldTranslator> trans = DataObjectTranslatorUtils.geTranslationStrategy(comp.getStrategy(), ctx);
			
			FieldTranslatorContextImpl translatorContext = new FieldTranslatorContextImpl(ctx,execCtx,inputMap);
			List<String> toTranslate = resultType.getAttributeNames();
			while(toTranslate.size()>0) {
				List<String> toRemove = new ArrayList<String>();
				for(String field : toTranslate) {
					String fieldTypeName = resultType.getAttributeType(field);
					if (execCtx.getVariableType(field)==null) {
						// The field isn't in the execCtx, try to translate it.
						JavaCode code = null;
						for(FieldTranslator tr : trans) {
							code = tr.getAttribute(field, fieldTypeName, field, translatorContext);
							if (code!=null) {
								break;
							}
						}
						if (code!=null) {
							JsomUtils.merge(body, JavaUtils.declare(field, fieldTypeName, ctx, execCtx));
							// Add the field to the execCtx
							execCtx.addVariable(field, fieldTypeName);
							JsomUtils.merge(body, code);
							String setter = resultType.getCodeToSetAttribute("_ret", field, field, execCtx);
							body.append(setter+";\n");
							toRemove.add(field);
						}
					} else {
						// We already have the field in the execCtx, translate it to the target.
						String merge = resultType.getCodeToSetAttribute("_ret", field, field, execCtx)+";\n";
						body.append(merge);
						toRemove.add(field);
					}
				}
				if (toRemove.size()==0) {
					throw new JavascribeException("Translator was unable to translate field '"+toTranslate.get(0)+"'");
				} else {
					toTranslate.removeAll(toRemove);
				}
			}
			/*
			for(FieldTranslator tr : trans) {
				JavaCode append = tr.translateFields(resultType, "_ret", execCtx, ctx,attribs);
				JsomUtils.merge(body, append);
				if (attribs.size()==0) break;
			}
			if (attribs.size()>0) {
				throw new JavascribeException("Could not translate field '"+attribs.get(0)+"'");
			}
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
			*/
			// Return the result
			body.append("return _ret;\n");
		} catch(CodeGenerationException e) {
			throw new JavascribeException("CodeGenerationException while processing data object translator",e);
		}
	}

}

