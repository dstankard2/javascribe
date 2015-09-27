package net.sf.javascribe.patterns.custom;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.Injectable;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;
import net.sf.javascribe.langsupport.java.LocatedJavaServiceObjectType;
import net.sf.javascribe.langsupport.java.ServiceLocator;
import net.sf.javascribe.langsupport.java.ServiceLocatorImpl;
import net.sf.javascribe.langsupport.java.jsom.JsomJavaBeanType;
import net.sf.javascribe.langsupport.java.jsom.JsomUtils;
import net.sf.javascribe.patterns.servlet.WebServletFilter;
import net.sf.javascribe.patterns.servlet.WebUtils;
import net.sf.javascribe.patterns.servlet.WebXmlFile;
import net.sf.jsom.CodeGenerationException;
import net.sf.jsom.java5.Java5CodeSnippet;
import net.sf.jsom.java5.Java5DeclaredMethod;
import net.sf.jsom.java5.Java5SourceFile;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class HandwrittenCodeProcessor {
	static Logger log = Logger.getLogger(HandwrittenCodeProcessor.class);

	@ProcessorMethod(componentClass=HandwrittenCode.class)
	public void process(HandwrittenCode comp,ProcessorContext ctx) throws JavascribeException {
		String srcRoot = comp.getSrcRoot();

		ctx.setLanguageSupport("Java");

		if (srcRoot.trim().length()==0) {
			throw new JavascribeException("Component handwrittenCode requires attribute srcRoot");
		}

		log.info("Processing handwritten code from root directory '"+srcRoot+"'");
		try {
			File dir = new File(srcRoot);
			if (!dir.exists()) {
				throw new JavascribeException("srcRoot '"+dir.getAbsolutePath()+"' is not valid");
			}
			if (!dir.isDirectory()) {
				throw new JavascribeException("srcRoot '"+dir.getAbsolutePath()+"' is not a directory");
			}

			processDir(dir,ctx);
		} catch(IOException e) {
			throw new JavascribeException("Exception while parsing hand-written code",e);
		} catch(ParseException e) {
			throw new JavascribeException("Exception while parsing hand-written code",e);
		}
	}

	private void processDir(File dir,ProcessorContext ctx) throws IOException,ParseException,JavascribeException {

		File[] contents = dir.listFiles();
		for(File f : contents) {
			if (f.isDirectory()) {
				processDir(f,ctx);
			} else if (f.getName().endsWith(".java")) {
				processFile(f,ctx);
			}
		}
	}

	private void processFile(File f,ProcessorContext ctx) throws ParseException,IOException,JavascribeException {
		CompilationUnit unit = JavaParser.parse(f);

		String pkg = unit.getPackage().getName().toString();
		List<TypeDeclaration> types = unit.getTypes();
		if (types==null) return;
		for(TypeDeclaration dec : types) {
			List<AnnotationExpr> ans = dec.getAnnotations();
			if ((ans==null) || (ans.size()==0)) continue;

			handleServletFilter(dec,pkg,ctx);
			handleControllerServlet(dec,pkg,ctx);
			handleBusinessObject(dec,pkg,ctx);
			handleDataObject(dec,pkg,ctx);
			handleWebContextListener(dec,pkg,ctx);
		}
	}

	private void handleWebContextListener(TypeDeclaration dec,String pkg,ProcessorContext ctx) throws JavascribeException {
		List<AnnotationExpr> ans = dec.getAnnotations();
		boolean isListener = false;
		
		for(AnnotationExpr an : ans) {
			if (an.getName().getName().equals("WebContextListener")) {
				isListener = true;
			}
		}
		
		if (!isListener) return;
		WebXmlFile webXml = WebUtils.getWebXml(ctx);
		String cl = pkg+'.'+dec.getName();
		webXml.addContextListener(cl);
	}
	
	private void handleServletFilter(TypeDeclaration dec,String pkg,ProcessorContext ctx) throws JavascribeException {
		List<AnnotationExpr> ans = dec.getAnnotations();
		String name = null;

		for(AnnotationExpr an : ans) {
			if (an.getName().getName().equals("ServletFilter")) {
				if (!(an instanceof NormalAnnotationExpr)) {
					continue;
				}
				NormalAnnotationExpr expr = (NormalAnnotationExpr)an;
				List<MemberValuePair> pairs = expr.getPairs();
				for(MemberValuePair pair : pairs) {
					if (pair.getName().equals("name")) {
						name = pair.getValue().toString();
						break;
					}
				}
			}
		}

		if (name==null) return;
		if (name.trim().length()==0) return;

		if (name.startsWith("\"")) name = name.substring(1);
		if (name.endsWith("\"")) name = name.substring(0, name.length()-1);

		String cl = pkg+'.'+dec.getName();

		log.debug("Found handwritten servlet filter with name '"+name+"' and class '"+cl+"'");

		WebServletFilter filterComp = new WebServletFilter();
		filterComp.setClassName(cl);
		filterComp.setName(dec.getName());
		ctx.addComponent(filterComp);
	}

	private void handleControllerServlet(TypeDeclaration dec,String pkg,ProcessorContext ctx) throws JavascribeException {
		List<AnnotationExpr> ans = dec.getAnnotations();
		String uriPath = null;
		String filters = null;

		for(AnnotationExpr an : ans) {
			if (an.getName().getName().equals("ControllerServlet")) {
				NormalAnnotationExpr expr = (NormalAnnotationExpr)an;
				List<MemberValuePair> pairs = expr.getPairs();
				for(MemberValuePair pair : pairs) {
					if (pair.getName().equals("uriPath")) {
						uriPath = pair.getValue().toString();
					}
					if (pair.getName().equals("filters")) {
						filters = pair.getValue().toString();
					}
				}
			}
		}

		if (uriPath==null) return;
		if (uriPath.trim().length()==0) return;

		if (uriPath.startsWith("\"")) uriPath = uriPath.substring(1);
		if (uriPath.endsWith("\"")) uriPath = uriPath.substring(0, uriPath.length()-1);

		if (filters!=null) {
			if (filters.startsWith("\"")) filters = filters.substring(1);
			if (filters.endsWith("\"")) filters = filters.substring(0, filters.length()-1);
		}

		String cl = pkg+'.'+dec.getName();

		log.debug("Found handwritten HTTP servlet with name '"+dec.getName()+"' and uriPath '"+uriPath+"' and class '"+cl+"'");

		HandwrittenWebServlet servletComp = new HandwrittenWebServlet();
		servletComp.setFilters(filters);
		servletComp.setName(dec.getName());
		servletComp.setClassName(cl);
		servletComp.setUriPath(uriPath);
		ctx.addComponent(servletComp);
	}

	private void handleBusinessObject(TypeDeclaration dec,String pkg,ProcessorContext ctx) throws JavascribeException {
		List<AnnotationExpr> ans = dec.getAnnotations();
		String name = null;
		String group = null;

		for(AnnotationExpr an : ans) {
			if (an.getName().getName().equals("BusinessObject")) {
				NormalAnnotationExpr expr = (NormalAnnotationExpr)an;
				List<MemberValuePair> pairs = expr.getPairs();
				for(MemberValuePair pair : pairs) {
					if (pair.getName().equals("name")) {
						name = pair.getValue().toString();
					} else if (pair.getName().equals("group")) {
						group = pair.getValue().toString();
					}
				}
			}
		}

		if ((name==null) || (name.trim().length()==0)) return;
		if ((group!=null) && (group.trim().length()==0)) group = null;

		if (name.startsWith("\"")) name = name.substring(1);
		if (name.endsWith("\"")) name = name.substring(0, name.length()-1);

		if (group!=null) {
			if (group.startsWith("\"")) group = group.substring(1);
			if (group.endsWith("\"")) group = group.substring(0, group.length()-1);
		}

		if (group!=null) {
			log.debug("Found business object '"+name+"' in group '"+group+"'");
		} else {
			log.debug("Found standalone business object '"+name+"'");
		}

		List<BodyDeclaration> members = dec.getMembers();

		List<String> deps = new ArrayList<String>();
		List<BodyDeclaration> rules = new ArrayList<BodyDeclaration>();

		for(BodyDeclaration mem : members) {
			if (mem.getAnnotations()==null) continue;
			for(AnnotationExpr an : mem.getAnnotations()) {
				if (an.getName().getName().equals("Dependency")) {
					if (mem instanceof FieldDeclaration) {
						FieldDeclaration f = (FieldDeclaration)mem;
						for(VariableDeclarator d : f.getVariables()) {
							deps.add(d.getId().getName());
							d.toString();
						}
					}
				}
				if (an.getName().getName().equals("BusinessRule")) {
					rules.add(mem);
				}
			}
		}

		if ((deps.size()>0) && (group==null)) {
			throw new JavascribeException("A business object with dependencies must have a group so that a service locator can be made");
		}

		ServiceLocator locatorType = null;
		JavaServiceObjectType srvType = null;
		Java5SourceFile locatorFile = null; 

		if (group!=null) {
			String locName = group+"Locator";
			locatorType = (ServiceLocator)ctx.getType(locName);
			if (locatorType==null) {
				locatorType = new ServiceLocatorImpl(locName,pkg,locName);
				ctx.getTypes().addType(locatorType);
				locatorFile = JsomUtils.createJavaSourceFile(ctx);
				locatorFile.setPackageName(pkg);
				locatorFile.getPublicClass().setClassName(locName);
				JsomUtils.addJavaFile(locatorFile, ctx);
			} else {
				locatorFile = JsomUtils.getJavaFile(pkg+'.'+locName, ctx);
			}

			srvType = new LocatedJavaServiceObjectType(locatorType.getImport(), name, pkg, name);
			locatorType.getAvailableServices().add(name);

			// Create a locator method
			ctx.addAttribute(JavascribeUtils.getLowerCamelName(name), name);
			Java5DeclaredMethod locator = JsomUtils.createMethod(ctx);
			locator.setName("get"+name);
			locator.setType(name);
			Java5CodeSnippet code = new Java5CodeSnippet();
			locator.setMethodBody(code);
			try {
				JsomUtils.merge(code, srvType.declare("_ret"));
				code.append("_ret = new "+name+"();\n");

				for(String dep : deps) {
					String upperCamel = JavascribeUtils.getUpperCamelName(dep);
					String typeName = ctx.getAttributeType(dep);
					if (typeName==null) {
						throw new JavascribeException("Found an unrecognized dependency '"+dep+"'");
					}
					VariableType type = ctx.getType(typeName);
					if (!(type instanceof Injectable)) {
						throw new JavascribeException("Found a dependency '"+typeName+"' that is not an injectable type");
					}
					Injectable inj = (Injectable)type;
					JsomUtils.merge(code, inj.getInstance(dep, null));
					code.append("_ret.set"+upperCamel+"("+dep+");\n");
				}
				code.append("return _ret;\n");
				locatorFile.getPublicClass().addMethod(locator);
			} catch(CodeGenerationException e) {
				throw new JavascribeException("JSOM Exception",e);
			}
		} else {
			srvType = new JavaServiceObjectType(name, pkg, name);
		}
		ctx.getTypes().addType(srvType);

		// Add all business methods to the srvType
		for(BodyDeclaration rule : rules) {
			if (!(rule instanceof MethodDeclaration)) continue;
			MethodDeclaration methodDec = (MethodDeclaration)rule;
			JavaOperation op = new JavaOperation();
			if (!methodDec.getType().toString().equals("void")) {
				String returnType = findType(methodDec.getType().toString(),ctx);
				if (returnType==null) throw new JavascribeException("Couldn't find type for return type '"+methodDec.getType().toString()+"'");
				op.setReturnType(returnType);
			}
			op.setName(methodDec.getName());
			if (methodDec.getParameters()!=null) {
				for(Parameter param : methodDec.getParameters()) {
					String type = param.getType().toString();
					String n = null,t = null;
					n = param.getId().getName();
					t = findType(type,ctx);
					if (t==null) throw new JavascribeException("Couldn't find type for parameter type '"+type+"'");
					op.addParameter(n, t);
					ctx.addAttribute(n, t);
				}
			}
			srvType.addMethod(op);
		}
	}

	private void handleDataObject(TypeDeclaration dec,String pkg,ProcessorContext ctx) throws JavascribeException {
		List<AnnotationExpr> ans = dec.getAnnotations();
		boolean dataObject = false;

		for(AnnotationExpr an : ans) {
			if (an.getName().getName().equals("DataObject")) {
				dataObject = true;
				break;
			}
		}

		if (!dataObject) return;

		String objectName = dec.getName();
		JsomJavaBeanType objType = new JsomJavaBeanType(objectName,pkg,objectName);

		log.info("Found data object '"+objectName+"'");
		
		String attrName = JavascribeUtils.getLowerCamelName(objectName);
		ctx.addAttribute(attrName, objectName);
		ctx.addAttribute(attrName+"List", "list/"+objectName);

		List<BodyDeclaration> members = dec.getMembers();

		for(BodyDeclaration mem : members) {
			if (mem.getAnnotations()==null) continue;
			for(AnnotationExpr an : mem.getAnnotations()) {
				if (an.getName().getName().equals("DataObjectAttribute")) {
					if (mem instanceof FieldDeclaration) {
						FieldDeclaration f = (FieldDeclaration)mem;
						if (f.getVariables().size()!=1) {
							throw new JavascribeException("Found a data object attribute field declaration with zero or multiple variables: "+f.toString());
						}
						VariableDeclarator d = f.getVariables().get(0);
						String type = f.getType().toString();
						type = findType(type,ctx);
						String name = d.getId().toString();
						objType.addAttribute(name, type);
					}
				}
			}
		}

		ctx.getTypes().addType(objType);
	}

	private String findType(String type,ProcessorContext ctx) {
		if (type.equals("String")) return "string";
		else if (type.equals("int")) return "integer";
		else if (type.equals("Integer")) return "integer";
		else if (type.equals("Long")) return "longint";
		else if (type.equals("long")) return "longint";
		else if (type.equals("Object")) return "object";
		else if (type.equals("Date")) return "date";
		else if (type.equals("Timestamp")) return "timestamp";
		else if (type.startsWith("ArrayList<")) {
			String sub = type.substring(10,type.length()-1);
			return "list/"+findType(sub,ctx);
		}
		else if (type.startsWith("List<")) {
			String sub = type.substring(5,type.length()-1);
			return "list/"+findType(sub,ctx);
		}
		else if (ctx.getType(type)!=null) return type;

		return null;
	}

}

