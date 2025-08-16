package net.sf.javascribe.patterns.js.template.directives;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.patterns.js.template.parsing.AttributeDirectiveBase;
import net.sf.javascribe.patterns.js.template.parsing.DirectiveContext;

@Plugin
public class ModelAttributeDirective extends AttributeDirectiveBase {

	@Override
	public String getAttributeName() {
		return "js-model";
	}

	@Override
	public void generateCode(DirectiveContext ctx) throws JavascribeException {
		String model = ctx.getTemplateAttribute("js-model");
		StringBuilder b = ctx.getCode();
		String eltName = ctx.getElementName();
		String setAfterFn = null;
		String changeFn = ctx.newVarName("cfn", "function", ctx.getExecCtx());

		String var = ctx.getElementVarName();
		String template = null;
		Map<String,String> templateParams = new HashMap<>();
		templateParams.put("ELTVAR", var);
		String remFn = ctx.newVarName("_rm", "function", ctx.getExecCtx());
		templateParams.put("REM_FN", remFn);
		templateParams.put("MODEL_REF", "_model."+model);
		
		String attrName = model;
		if ((model==null) || (model.trim().length()==0)) {
			throw new JavascribeException("HTML template directive js-model requires a value");
		}
		if (model.indexOf('.')>=0) {
			attrName = model.substring(0, model.indexOf('.'));
		} else {
			// If the model is a local model property, then no event is required because updating the model property will trigger the event.
			// Therefore, use a dummy change event.
			attrName = "_dummyEvent_doNotUse";
		}
		templateParams.put("CHANGE_EVENT", attrName+"Changed");

		if (eltName.equals("select")) {
			template = SELECT_MODEL;
			String fn = ctx.newVarName("_f", "function", ctx.getExecCtx());
			templateParams.put("FN", fn);
			setAfterFn = fn;
		} else if (eltName.equals("input")) {
			String type = ctx.getDomAttribute("type");
			String changeEvent = "input";
			template = INPUT_MODEL_TEXT;
			templateParams.put("DOMEVENT", changeEvent);
			templateParams.put("CHANGEFN", changeFn);
		} else if (eltName.equals("textarea")) {
			template = INPUT_MODEL_TEXT;
			templateParams.put("DOMEVENT", "input");
		}
		
		String code = template;
		for(Entry<String,String> entry : templateParams.entrySet()) {
			code = code.replaceAll("aaa"+entry.getKey(), entry.getValue());
		}
		b.append(code);
		ctx.continueRenderElement();
		if (setAfterFn != null) {
			b.append(setAfterFn+"();\r\n");
		}
	}
	
	private static final String INPUT_MODEL_TEXT = """
			aaaELTVAR.addEventListener('aaaDOMEVENT', function() {
			const val = aaaELTVAR.value;
			try {
			aaaMODEL_REF = val;
			_dis('aaaCHANGE_EVENT');
			} catch(err) { }
			});
			const aaaCHANGEFN = function() {
				try {
				var val = aaaMODEL_REF;
				if ((val===null) || (val===undefined)) val = '';
				aaaELTVAR.value = val;
				} catch(err) {}
			};
			aaaCHANGEFN();
			const aaaREM_FN = _dis('aaaCHANGE_EVENT',aaaCHANGEFN);
			aaaELTVAR.$$remove.push(aaaREM_FN);
			""";

	/*
	private static final String INPUT_MODEL_TEXT = "aaaELTVAR.addEventListener('aaaDOMEVENT', function() {\n"
			+ "var val = aaaELTVAR.value;\n"
			+ "try {\n"
			+ "aaaMODEL_REF = val;\n"
			+ "_dis('aaaCHANGE_EVENT');\n"
			+ "} catch(err) { }\n"
			+ "});\n"
			+ "var aaaREM_FN = _dis('aaaCHANGE_EVENT', function() {\n"
			+ "try {\n"
			+ "var val = aaaMODEL_REF;\n"
			+ "if ((val===null) || (val===undefined)) val = '';\n"
			+ "aaaELTVAR.value = val;\n"
			+ "} catch(err) {}\n"
			+ "});\n"
			+ "aaaELTVAR.$$remove.push(aaaREM_FN);\n";
*/
	
	private static final String SELECT_MODEL = "aaaELTVAR.onchange = function() {\r\n" + 
			"var val = aaaELTVAR.value;\r\n" + 
			"try {\r\n" + 
			"aaaMODEL_REF = val;\r\n" + 
			"_dis('aaaCHANGE_EVENT');\n" +
			"} catch(_err) { }\r\n" + 
			"}\r\n" + 
			"var aaaFN = function() {\r\n" + 
			"	var val = null;\r\n" + 
			"	try {\r\n" + 
			"		val = aaaMODEL_REF;\r\n" + 
			"	} catch(_err) { }\r\n" + 
			"	for(var _i=0;_i<aaaELTVAR.options.length;_i++) {\r\n" + 
			"		if (aaaELTVAR.options[_i].value == val) {\r\n" + 
			"			aaaELTVAR.selectedIndex = _i;\r\n" + 
			"			break;\r\n" + 
			"		}\r\n" + 
			"	}\r\n" + 
			"};\r\n" + 
			"var aaaREM_FN = _dis('aaaCHANGE_EVENT',aaaFN);\r\n" + 
			"aaaFN();\r\n" + 
			"aaaELTVAR.$$remove.push(aaaREM_FN);\r\n";
}

