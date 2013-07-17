package net.sf.javascribe.patterns.model;

import java.util.List;

import net.sf.javascribe.api.Attribute;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.types.ListType;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.jsom.JavascribeVariableTypeResolver;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5ClassConstructor;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;
import net.sf.jsom.java5.Java5Type;

@Scannable
@Processor
public class EjbqlQueryProcessor {
	public static final String EJBQL_QUERY_PKG = "net.sf.javascribe.patterns.model.EjbqlQuery.pkg";
	EjbqlQuery query = null;
	ProcessorContext ctx = null;

	@ProcessorMethod(componentClass=EjbqlQuery.class)
	public void process(EjbqlQuery comp,ProcessorContext ctx) throws JavascribeException {
		this.ctx = ctx;
		query = comp;

		ctx.setLanguageSupport("Java");
		
		Java5SourceFile src = null;
		String pkg = null;
		String className = null;
		String fullName = null;
		JavaServiceObjectType objType = null;
		String pu = null;
		String querySet = null;
		EntityManagerType emType = null;

		System.out.println("Processing EJBQL Query '"+query.getName()+"'");

		try {
			pu = query.getPu();
			if (pu.trim().length()==0) {
				pu = ctx.getProperty("net.sf.javascribe.patterns.model.EjbqlQuery.pu");
			}

			if (query.getName().trim().length()==0) {
				throw new JavascribeException("Found ejbqlQuery component with no name");
			}
			if ((query.getQueryString()==null) || (query.getQueryString().getValue().trim().length()==0) 
					|| (query.getQueryString().getValue().trim().length()==0)) {
				throw new JavascribeException("No query string specified for EJBQL Query '"+query.getName()+"'");
			}
			if (pu==null) {
				throw new JavascribeException("Could not determine JPA PU for ejbqlQuery '"+query.getName()+"'");
			}
			
			querySet = query.getQuerySet();
			if (querySet.trim().length()==0) {
				querySet = ctx.getProperty("net.sf.javascribe.patterns.model.EjbqlQuery.querySet");
			}

			if (querySet==null) {
				throw new JavascribeException("Could not determine Query Set for ejbqlQuery '"+query.getName()+"'");
			}

			pkg = JavaUtils.findPackageName(ctx, ctx.getRequiredProperty(EJBQL_QUERY_PKG));
			className = query.getQuerySet();
			fullName = pkg+'.'+className;

			emType = (EntityManagerType)ctx.getType(pu);
			if (emType==null) {
				throw new JavascribeException("Specified PU '"+pu+"' is not an Entity Manager Type");
			}
			
			JavascribeVariableTypeResolver types = new JavascribeVariableTypeResolver(ctx);
			src = JsomUtils.getJavaFile(fullName, ctx);
			if (src==null) {
				src = new Java5SourceFile(types);
				src.getPublicClass().addMemberVariable("tx", pu, null);
				src.setPackageName(pkg);
				src.getPublicClass().setClassName(className);
				Java5ClassConstructor cons = new Java5ClassConstructor(types, className);
				EntityManagerLocator loc = ModelUtils.getDefaultEntityManagerLocator(pu, ctx);
				Java5CodeSnippet code = new Java5CodeSnippet();
				JsomUtils.merge(code, loc.getEntityManager("this.tx", null));
//				code.append("this.tx = tx;");
				cons.setMethodBody(code);
//				src.getPublicClass().addMemberVariable("tx", pu, null);
				src.getPublicClass().addMethod(cons);
				objType = new JavaServiceObjectType(query.getQuerySet(),pkg,query.getQuerySet());
				objType.setPkg(pkg);
				objType.setClassName(className);
				JsomUtils.addJavaFile(src, ctx);
				ctx.getTypes().addType(objType);
			} else {
				objType = (JavaServiceObjectType)ctx.getTypes().getType(query.getQuerySet());
			}

			Java5DeclaredMethod method = new Java5DeclaredMethod(types);
			Java5CodeSnippet methodCode = new Java5CodeSnippet();
			boolean multiple = false;
			String returnType = null;

			returnType = query.getReturnType();
			if ((returnType!=null) && (returnType.trim().length()==0)) {
				returnType = null;
			}
			if ((returnType!=null) && (returnType.startsWith("list/"))) {
				multiple = true;
			}

			method.setMethodName(query.getName());
			method.setReturnType(returnType);
			method.setMethodBody(methodCode);

			List<Attribute> params = JavascribeUtils.readAttributes(ctx, query.getParams());

			for(Attribute a : params) {
				method.addArg(a.getType(), a.getName());
			}
			boolean paging = false;
			if (query.getPageable().equalsIgnoreCase("true")) {
				method.addArg("integer","_start");
				method.addArg("integer","_max");
				paging = true;
			}
			String finalQueryString = getQueryString(query.getQueryString().getValue());
			CodeExecutionContext execCtx = new CodeExecutionContext(null,ctx.getTypes());

			// Type of the result, if the result is not a list.
			Java5Type resultType = null;
			if (returnType!=null) {
				execCtx.addVariable("_ret", returnType);
				if (returnType.startsWith("list/")) {
					ListType listType = (ListType)ctx.getTypes().getType(returnType);
					JsomUtils.merge(methodCode, (JavaCode)listType.declare("_ret", returnType.substring(5), execCtx));
				}
				else {
					resultType = (Java5Type)ctx.getTypes().getType(returnType);
					if (resultType==null) throw new CodeGenerationException("Unrecognized HQL query return type '"+returnType+"'");
					methodCode.merge(resultType.declare("_ret"));
				}
			}

			methodCode.addImport("javax.persistence.Query");
			methodCode.append("Query _query = tx.createQuery(\""+finalQueryString+"\");\n");
			for(Attribute a : params) {
				methodCode.append("_query.setParameter(\""+a.getName()+"\","+a.getName()+");\n");
			}
			if (paging) {
				methodCode.append("_query.setMaxResults(_max);\n");
				methodCode.append("_query.setFirstResult(_start);\n");
			}

			if (returnType!=null) {
				if (multiple) {
					methodCode.append("_ret = _query.getResultList();\n");
				} else {
					// Variable resultType will be populated.
					methodCode.append("try {\n_query.setMaxResults(1);\n");
					methodCode.append("_ret = ("+resultType.getClassName()+")_query.getSingleResult();\n");
					methodCode.append("} catch(javax.persistence.NoResultException e) { }\n");
				}
			} else {
				methodCode.append("_query.executeQuery();\n");
			}

			if (returnType!=null) {
				methodCode.append("return _ret;");
			}

			// Add method to Java object type accessor.
			objType.addMethod(JsomUtils.createJavaOperation(method));
			src.getPublicClass().addMethod(method);
		} catch(CodeGenerationException e) {
			throw new JavascribeException("JSOM exception while processing component",e);
		}
	}

	private String getQueryString(String s) {
		String ret = "";
		String lines[] = s.split("\n");

		for(String l : lines) {
			ret = ret + " "+l.trim();
		}

		return ret.trim();
	}

}

