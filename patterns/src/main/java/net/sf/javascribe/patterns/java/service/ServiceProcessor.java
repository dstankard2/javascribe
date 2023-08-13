package net.sf.javascribe.patterns.java.service;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.PropertyEntry;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.service.NestingOperation;
import net.sf.javascribe.patterns.xml.java.service.Operation;
import net.sf.javascribe.patterns.xml.java.service.Service;

@Plugin
public class ServiceProcessor implements ComponentProcessor<Service>,RendererContext {

	private JavaCode serviceCode = null;
	private CodeExecutionContext currentExecCtx = null;
	private String resultVar = "returnValue";
	private JavaDataObjectType resultType = null;
	private JavaClassSourceFile resultSrc = null;
	private Operation currentOperation = null;
	private ProcessorContext ctx = null;

	@Override
	public JavaCode getCode() {
		return serviceCode;
	}

	@Override
	public CodeExecutionContext execCtx() {
		return currentExecCtx;
	}

	@Override
	public void handleNesting(CodeExecutionContext execCtx) throws JavascribeException {
		CodeExecutionContext stored = currentExecCtx;
		Operation storedOp = currentOperation;

		currentExecCtx = execCtx;

		if (currentOperation instanceof NestingOperation) {
			NestingOperation nop = (NestingOperation)currentOperation;
			this.readOperations(nop.getOperation());
		}

		currentExecCtx = stored;
		currentOperation = storedOp;
	}

	@Override
	public void addResultProperty(String name, String type) throws JavascribeException {
		if (resultType.getAttributeType(name)!=null) {
			if (!resultType.getAttributeType(name).equals(type)) {
				throw new JavascribeException("Cannot add property '"+name+"' to service result type because that property is already on the result as a different type");
			}
		} else {
			resultType.addProperty(name, type);
			JavaUtils.addProperty(resultSrc, name, type, ctx);
		}
	}

	@Override
	public ProcessorContext ctx() {
		return ctx;
	}

	@Override
	public String getResultVar() {
		return resultVar;
	}

	@Override
	public void process(Service comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		this.ctx = ctx;
		String pkg = null;
		String className = comp.getModule()+"Service";
		pkg = JavaUtils.getJavaPackage(comp, ctx);
		String resultName = getResultTypeName(comp, ctx);
		String paramString = comp.getParams();
		
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(pkg+'.'+className, ctx);
		JavaServiceType type = null;
		
		if (ctx.getVariableType(className)!=null) {
			type = JavascribeUtils.getType(JavaServiceType.class, className, ctx);
			ctx.modifyVariableType(type);
		} else {
			type = new JavaServiceType(className,pkg+'.'+className,ctx.getBuildContext());
			ctx.addVariableType(type);
			String lowerCamel = JavascribeUtils.getLowerCamelName(type.getName());
			ctx.addSystemAttribute(lowerCamel, type.getName());
		}

		resultSrc = new JavaClassSourceFile(ctx);
		resultType = new JavaDataObjectType(resultName,pkg+'.'+resultName,ctx.getBuildContext());

		ctx.getLog().info("Creating service result type '"+resultName+"'");
		ctx.addVariableType(resultType);

		resultSrc.getSrc().setPackage(pkg);
		resultSrc.getSrc().setName(resultName);
		
		currentExecCtx = new CodeExecutionContext(ctx);
		serviceCode = new JavaCode();
		JavaUtils.append(serviceCode, resultType.declare("returnValue", currentExecCtx));
		//JavaCode code = resultType.declare("returnValue", execCtx);
		JavaUtils.append(serviceCode, resultType.instantiate("returnValue"));
		currentExecCtx.addVariable("returnValue", resultName);
		
		List<Operation> ops = comp.getServiceOperation();

		List<PropertyEntry> params = JavascribeUtils.readParametersAsList(paramString, ctx);
		ServiceOperation op = new ServiceOperation(comp.getName());
		type.addOperation(op);
		op.returnType(resultName);
		for(PropertyEntry param : params) {
			String n = param.getName();
			String t = param.getType().getName();
			op.addParam(n, t);
			currentExecCtx.addVariable(n, t);
		}

		readOperations(ops);
		
		this.serviceCode.appendCodeText("return "+this.resultVar+";\n");

		JavaUtils.addServiceOperation(op, serviceCode, src.getSrc(), ctx);

		ctx.addSourceFile(resultSrc);
	}
	
	protected void readOperations(List<Operation> operations) throws JavascribeException {
		for(Operation op : operations) {
			currentOperation = op;
			OperationRenderer renderer = op.getRenderer();
			renderer.render(this);
		}
	}

	private String getResultTypeName(Service comp, ProcessorContext ctx) throws JavascribeException {
		String resultName = null;

		resultName = comp.getName();
		resultName = Character.toUpperCase(comp.getName().charAt(0))+comp.getName().substring(1);
		resultName = resultName + "ServiceResult";
		if (ctx.getVariableType(resultName)!=null) {
			throw new JavascribeException("Couldn't create service result class as a type named '"+resultName+"' already exists - please override the default");
		}
		return resultName;
	}

	/*
	public void _process(Service comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		String pkg = null;
		String className = comp.getModule()+"Service";
		pkg = JavaUtils.getJavaPackage(comp, ctx);
		
		JavaClassSourceFile src = JavaUtils.getClassSourceFile(pkg+'.'+className, ctx);
		JavaServiceType type = null;
		
		if (ctx.getVariableType(className)!=null) {
			type = JavascribeUtils.getType(JavaServiceType.class, className, ctx);
			ctx.modifyVariableType(type);
		} else {
			type = new JavaServiceType(className,pkg+'.'+className,ctx.getBuildContext());
			ctx.addVariableType(type);
			String lowerCamel = JavascribeUtils.getLowerCamelName(type.getName());
			ctx.addSystemAttribute(lowerCamel, type.getName());
		}

		handleServiceAndResult(comp, ctx, type,src, pkg);
	}
	*/

	/*
	private void handleServiceAndResult(Service comp, ProcessorContext ctx, JavaServiceType type, JavaClassSourceFile src, String pkg) throws JavascribeException {
		ServiceOperation op = new ServiceOperation(comp.getName());
		String paramString = comp.getParams();
		String resultName = getResultTypeName(comp, ctx);
		
		type.addOperation(op);
		ctx.modifyVariableType(type);
		List<PropertyEntry> params = JavascribeUtils.readParametersAsList(paramString, ctx);
		CodeExecutionContext execCtx = new CodeExecutionContext(ctx);
		op.returnType(resultName);
		for(PropertyEntry param : params) {
			String n = param.getName();
			String t = param.getType().getName();
			op.addParam(n, t);
			execCtx.addVariable(n, t);
		}

		JavaClassSourceFile resultSrc = new JavaClassSourceFile(ctx);
		JavaDataObjectType resultType = new JavaDataObjectType(resultName,pkg+'.'+resultName,ctx.getBuildContext());

		ctx.getLog().info("Creating service result type '"+resultName+"'");
		ctx.addVariableType(resultType);

		resultSrc.getSrc().setPackage(pkg);
		resultSrc.getSrc().setName(resultName);
		
		JavaCode code = resultType.declare("returnValue", execCtx);
		JavaUtils.append(code, resultType.instantiate("returnValue"));
		execCtx.addVariable("returnValue", resultName);
		
		//JavaCode bodyCode = new JavaCode();
		processOperations(comp.getServiceOperation(),code, execCtx, resultType, resultSrc, ctx);
		//JavaUtils.append(code, bodyCode);

		code.appendCodeText("return returnValue;\n");
		JavaUtils.addServiceOperation(op, code, src.getSrc(), ctx);
		ctx.addSourceFile(resultSrc);
	}
*/

	/*
	private void processOperations(List<Operation> ops,JavaCode currentCode,
			CodeExecutionContext execCtx, JavaDataObjectType resultType, 
			JavaClassSourceFile resultSrc, ProcessorContext ctx) throws JavascribeException {
		for(Operation op : ops) {
			// Take care of result type/file
			if (op instanceof ResultOperation) {
				String resultTypeName,resultName;
				ResultOperation res = (ResultOperation)op;
				resultTypeName = res.getResultType(ctx, execCtx);
				if (resultTypeName!=null) {
					resultName = res.getResultName(ctx, execCtx);
					if ((resultName!=null) && (resultName.startsWith("returnValue."))) {
						resultName = resultName.substring(12);
						if (resultType.getAttributeType(resultName)==null) {
							if ((ctx.getSystemAttribute(resultName)!=null) && 
									(!ctx.getSystemAttribute(resultName).equals(resultTypeName))) {
								throw new JavascribeException("Found inconsistent types '"+resultTypeName+"' and '"+ctx.getSystemAttribute(resultName)+"' for attribute '"+resultName+"'");
							}
							ctx.addSystemAttribute(resultName, resultTypeName);
							resultType.addProperty(resultName, resultTypeName);
							JavaUtils.addProperty(resultSrc, resultName, resultTypeName, ctx);
						}
					}
				}
			}
			
			// Take care of operation processing
			if (op instanceof NestingOperation) {
				NestingOperation n = (NestingOperation)op;
				NestingOperationRenderer renderer = (NestingOperationRenderer)op.getRenderer(ctx);
				JavaUtils.append(currentCode, renderer.getCode(execCtx));
				List<Operation> nestedOps = n.getOperation();
				processOperations(nestedOps, currentCode, execCtx, resultType, resultSrc, ctx);
				JavaUtils.append(currentCode, renderer.endingCode(execCtx));
			} else {
				OperationRenderer renderer = op.getRenderer(ctx);
				JavaUtils.append(currentCode, renderer.getCode(execCtx));
			}
		}
	}
*/
	
	/*
	private JavaDataObjectType handleResult(String servicePkg, CodeExecutionContext execCtx) throws JavascribeException {
		JavaDataObjectType objType = null;
		JavaClassSourceFile src = new JavaClassSourceFile(ctx);
		String resultName = null;

		resultName = comp.getName();
		resultName = Character.toUpperCase(comp.getName().charAt(0))+comp.getName().substring(1);
		resultName = resultName + "ServiceResult";
		if (ctx.getVariableType(resultName)!=null) {
			throw new JavascribeException("Couldn't create service result class as a type named '"+resultName+"' already exists - please override the default");
		}
		ctx.getLog().info("Creating service result type '"+resultName+"'");
		objType = new JavaDataObjectType(resultName,servicePkg+'.'+resultName,ctx.getBuildContext());
		ctx.addVariableType(objType);

		JavaClassSource cl = src.getSrc();
		cl.setPackage(servicePkg);
		cl.setName(resultName);
		ctx.addSourceFile(src);

		List<Operation> ops = comp.getServiceOperation();
		readOperationsForResult(objType, src, ops, execCtx);

		return objType;
	}
	*/

	/*
	private void readOperationsForResult(JavaDataObjectType objType, JavaClassSourceFile src, 
			List<Operation> ops, CodeExecutionContext execCtx) throws JavascribeException {
		for(Operation op : ops) {
			if (op instanceof ResultOperation) {
				String resultType,resultName;
				ResultOperation res = (ResultOperation)op;
				resultType = res.getResultType(ctx, execCtx);
				if (resultType!=null) {
					resultName = res.getResultName(ctx, execCtx);
					if ((resultName!=null) && (resultName.startsWith("returnValue."))) {
						resultName = resultName.substring(12);
						if (objType.getAttributeType(resultName)==null) {
							if ((ctx.getSystemAttribute(resultName)!=null) && 
									(!ctx.getSystemAttribute(resultName).equals(resultType))) {
								throw new JavascribeException("Found inconsistent types '"+resultType+"' and '"+ctx.getSystemAttribute(resultName)+"' for attribute '"+resultName+"'");
							}
							ctx.addSystemAttribute(resultName, resultType);
							objType.addProperty(resultName, resultType);
							JavaUtils.addProperty(src, resultName, resultType, ctx);
						}
					}
				}
			}
			if (op instanceof NestingOperation) {
				NestingOperation nes = (NestingOperation)op;
				readOperationsForResult(objType,src,nes.getOperation(), execCtx);
			}
		}

	}
*/
	
}
