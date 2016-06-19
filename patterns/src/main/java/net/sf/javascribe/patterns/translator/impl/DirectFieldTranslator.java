package net.sf.javascribe.patterns.translator.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.AttributeHolder;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaCode;
import net.sf.javascribe.langsupport.java.JavaCodeImpl;
import net.sf.javascribe.patterns.translator.FieldTranslator;

@Scannable
public class DirectFieldTranslator implements FieldTranslator {

	public static final String CHECK_NULL = "net.sf.javascribe.patterns.translator.DataObjectTranslator.DirectFieldTranslator.checkNull";

	@Override
	public String name() {
		return "directSet";
	}

	@Override
	public JavaCode translateFields(AttributeHolder targetType,
			String targetVarName, CodeExecutionContext execCtx,ProcessorContext ctx,
			List<String> fieldsToTranslate) throws JavascribeException {
		JavaCodeImpl ret = new JavaCodeImpl();
		List<String> remove = new ArrayList<String>();
		
		// Do we check that the dependency is null?
		boolean checkNull = false;
		
		if (ctx.getProperty(CHECK_NULL)!=null) {
			checkNull = true;
		}
		
		for(String f : fieldsToTranslate) {
			boolean fieldTranslated = false;
			List<String> localVars = execCtx.getVariableNames();
			for(String v : localVars) {
				if (v.equals(targetVarName)) continue;
				if (v.equals(f)) {
					remove.add(f);
					ret.appendCodeText(targetType.getCodeToSetAttribute(targetVarName, f, v, execCtx));
					ret.appendCodeText(";\n");
					fieldTranslated = true;
					break;
				}
			}
			if (!fieldTranslated) {
				for(String v : localVars) {
					if (v.equals(targetVarName)) continue;
					VariableType type = execCtx.getTypeForVariable(v);
					if (type instanceof AttributeHolder) {
						AttributeHolder h = (AttributeHolder)type;
						if (h.getAttributeType(f)!=null) {
							remove.add(f);
							fieldTranslated = true;
							if (checkNull) {
								ret.appendCodeText("if ("+v+"!=null)");
							}
							String ref = h.getCodeToRetrieveAttribute(v, f, "object", execCtx);
							ret.appendCodeText(targetType.getCodeToSetAttribute(targetVarName, f, ref, execCtx));
							ret.appendCodeText(";\n");
						}
					}
				}
			}
		}
		fieldsToTranslate.removeAll(remove);
		
		return ret;
	}

}
