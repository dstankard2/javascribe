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
import net.sf.javascribe.langsupport.java.jsom.JsomJavaCode;
import net.sf.javascribe.patterns.translator.FieldTranslator;
import net.sf.jsom.java5.Java5CodeSnippet;

@Scannable
public class DirectFieldTranslator implements FieldTranslator {

	@Override
	public String name() {
		return "directSet";
	}

	@Override
	public JavaCode translateFields(AttributeHolder targetType,
			String targetVarName, CodeExecutionContext execCtx,ProcessorContext ctx,
			List<String> fieldsToTranslate) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		List<String> remove = new ArrayList<String>();

		
		for(String f : fieldsToTranslate) {
			boolean done = false;
			List<String> localVars = execCtx.getVariableNames();
			for(String v : localVars) {
				if (v.equals(targetVarName)) continue;
				if (v.equals(f)) {
					remove.add(f);
					ret.append(targetType.getCodeToSetAttribute(targetVarName, f, v, execCtx));
					ret.append(";\n");
					done = true;
				}
			}
			if (!done) {
				for(String v : localVars) {
					if (v.equals(targetVarName)) continue;
					VariableType type = execCtx.getTypeForVariable(v);
					if (type instanceof AttributeHolder) {
						AttributeHolder h = (AttributeHolder)type;
						if (h.getAttributeType(f)!=null) {
							remove.add(f);
							done = true;
							String ref = h.getCodeToRetrieveAttribute(v, f, "object", execCtx);
							ret.append(targetType.getCodeToSetAttribute(targetVarName, f, ref, execCtx));
							ret.append(";\n");
						}
					}
				}
			}
		}
		fieldsToTranslate.removeAll(remove);
		
		return new JsomJavaCode(ret);
	}

}
