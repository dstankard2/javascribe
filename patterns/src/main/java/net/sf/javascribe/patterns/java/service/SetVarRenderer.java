package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.patterns.xml.java.service.SetVarOperation;

public class SetVarRenderer extends OperationRenderer {

	private SetVarOperation op = null;
	
	public SetVarRenderer(SetVarOperation op) {
		this.op = op;
	}

	@Override
	public void render(RendererContext ctx) throws JavascribeException {
		JavaCode code = ctx.getCode();
		String name = op.getName();
		String type = op.getType();
		String value = op.getValue();
		CodeExecutionContext execCtx = ctx.execCtx();

		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Service operation setVar requires an attribute 'name'");
		}
		if (name.indexOf('.')>=0) {
			throw new JavascribeException("Service operation setVar requires a variable name to declare");
		}
		
		if (type==null) {
			type = ctx.ctx().getSystemAttribute(name);
			if (type==null) {
				throw new JavascribeException("Could not determine type for variable '"+name+"'");
			}
		} else {
			if ((ctx.ctx().getSystemAttribute(name)!=null) && (!type.equals(ctx.ctx().getSystemAttribute(name)))) {
				throw new JavascribeException("Could not create variable '"+name+"' because its declared type is different from a pre-existing system attribute");
			}
		}

		if (name.startsWith(ctx.getResultVar()+'.')) {
			String attrName = name.substring(ctx.getResultVar().length() + 1);
			ctx.addResultProperty(attrName, type);
		}
		
		if (execCtx.getTypeForVariable(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' because there is already one with that name in this rule");
		}

		JavaVariableType varType = JavascribeUtils.getType(JavaVariableType.class, type, ctx.ctx());
		if (varType==null) {
			throw new JavascribeException("Did not recognize type '"+type+"' as a valid Java type");
		}
		String val = null;
		if (value!=null) {
			val = JavascribeUtils.evaluateReference(value, execCtx);
		}
		execCtx.addVariable(name, type);
		JavaUtils.append(code, varType.declare(name, execCtx));
		if (val!=null) {
			code.appendCodeText(name+" = "+val+";\n");
		}
	}
	
	public JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException {
		JavaCode ret = new JavaCode();
		
		String name = op.getName();
		String type = op.getType();
		String value = op.getValue();
		
		if ((name==null) || (name.trim().length()==0)) {
			throw new JavascribeException("Service operation setVar requires an attribute 'name'");
		}
		if (name.indexOf('.')>=0) {
			throw new JavascribeException("Service operation setVar requires a variable name to declare");
		}
		
		if (type==null) {
			type = ctx.getSystemAttribute(name);
			if (type==null) {
				throw new JavascribeException("Could not determine type for variable '"+name+"'");
			}
		} else {
			if ((ctx.getSystemAttribute(name)!=null) && (!type.equals(ctx.getSystemAttribute(name)))) {
				throw new JavascribeException("Could not create variable '"+name+"' because its declared type is different from a pre-existing system attribute");
			}
		}
		
		if (execCtx.getTypeForVariable(name)!=null) {
			throw new JavascribeException("Cannot declare a variable named '"+name+"' because there is already one with that name in this rule");
		}

		JavaVariableType varType = JavascribeUtils.getType(JavaVariableType.class, type, ctx);
		if (varType==null) {
			throw new JavascribeException("Did not recognize type '"+type+"' as a valid Java type");
		}
		String val = null;
		if (value!=null) {
			val = JavascribeUtils.evaluateReference(value, execCtx);
		}
		execCtx.addVariable(name, type);
		JavaUtils.append(ret, varType.declare(name, execCtx));
		if (val!=null) {
			ret.appendCodeText(name+" = "+val+";\n");
		}

		return ret;
	}
	
}
