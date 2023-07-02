package net.sf.javascribe.patterns.js.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.langsupport.javascript.modules.HandwrittenModuleSource;
import net.sf.javascribe.langsupport.javascript.modules.ModuleSourceFile;
import net.sf.javascribe.langsupport.javascript.types.ModuleType;

@Plugin
public class PageBuilderProcessor implements ComponentProcessor<PageBuilderComponent> {

	@Override
	public void process(PageBuilderComponent comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		PageInfo pageInfo = comp.getPageInfo();
		ModuleSourceFile f = JavascriptUtils.getModuleSource(ctx);

		HandwrittenModuleSource mod = new HandwrittenModuleSource(pageInfo.getName());
		f.addModule(mod);
		StringBuilder code = mod.getCodeBuild();
		StringBuilder modelCode = new StringBuilder();
		List<String> fnNames = new ArrayList<>();
		
		code.append("var _eventDispatcher = EventDispatcher();\n");

		PageModelType pageModelType = PageUtils.getPageModelType(pageInfo.getName(), ctx);
		//ctx.addVariableType(pageModelType);
		modelCode.append(buildModel(pageInfo.getName(),pageModelType));
		
		// Create init function
		fnNames.add("init");
		code.append("function _init(_parent,_element) {\n");
		code.append("console.log('init page "+pageInfo.getName()+"');\n");
		code.append("if ((!_parent) && (!_element)) {\nconsole.error('No parent or element passed to init()');\nreturn;\n}\n");
		
		String renderObj = pageInfo.getPageRendererObj();
		ServiceOperation renderOp = pageInfo.getPageRendererRule();
		
		//String renderFnRef = pageInfo.getPageRenderer();
		if ((renderObj!=null) && (renderOp!=null)) {
			String templatesTypeName = ctx.getSystemAttribute(renderObj);
			
			ModuleType templatesType = JavascribeUtils.getType(ModuleType.class, templatesTypeName, ctx);
			//f.importModule(Pair.of(templatesTypeName, templatesType.getWebPath()));
			f.importModule(templatesType);
			// Page function should take one and only one argument - the page.  It should also return a DOM element
			assert renderOp.getParamNames().size()==1 : "A function that renders a page must take the page as a parameter";
			assert renderOp.getParamNames().get(0).equals("_page") : "A function that renders a page must take the page as a parameter";
			assert "DOMElement".equals(renderOp.getReturnType()) : "A Javascript function that renders a page must return a DOM Element";
			String fnName = renderObj+'.'+renderOp.getName();
			code.append("let "+renderObj+" = "+templatesType.getName()+";\n");
			code.append("var _elt = "+fnName+"(_obj);\n");
			code.append("_obj.view = _elt;\n");
			code.append("if (_element) {\n"
					+ "_parent.replaceChild(_elt,_element);\n"
					+ "} else {\n_parent.appendChild(_elt);\n}\n");
		}
		code.append("return _obj;\n}\n");
		
		/* Removed page functions
		// Create page functions
		List<PageFnDef> anonEventFunctions = new ArrayList<>();
		List<PageFnDef> eventFunctions = new ArrayList<>();
		for(PageFnDef def : pageInfo.getFunctions()) {
			String name = def.getName();
			if ((def.getEvent()!=null) && (def.getParams().size()>0)) {
				throw new JavascribeException("A page function with an event may not have parameters");
			}
			if (name!=null) {
				if (fnNames.contains(name)) {
					throw new JavascribeException("Found duplicate page functions called '"+name+"'");
				}
				fnNames.add(name);
				code.append("function _"+name+"(");
				for(int i=0;i<def.getParams().size();i++) {
					if (i>0) code.append(',');
					code.append(def.getParams().get(i));
				}
				code.append(") {\n");
				code.append(def.getCode());
				code.append("\n}\n");
				if (def.getEvent()!=null) {
					eventFunctions.add(def);
				}
			}
			else if (def.getEvent()!=null) {
				anonEventFunctions.add(def);
			}
		}
		*/

		// Add event function
		code.append("var _obj = {\nevent: function(event,callback) { return _eventDispatcher.event(event,callback); }\n");
		for(String name : fnNames) {
			code.append(", "+name+": _"+name+"\n");
		}
		code.append("};\n");
		code.append(modelCode);
		/* Page functions removed
		for(PageFnDef fn : anonEventFunctions) {
			code.append("_obj.event('"+fn.getEvent()+"',function() {"
					+ fn.getCode()
					+ "}.bind(_obj));\n");
		}
		for(PageFnDef fn : eventFunctions) {
			code.append("_obj.event('"+fn.getEvent()+"',_"+fn.getName()+");\n");
		}
		*/
		code.append("return _obj;\n");
	}
	
	private void addAttribute(String name,String type,PageModelType pageModelType, StringBuilder modelCode, StringBuilder pageCode) {
		modelCode.append("var _"+name+";\n");
		pageCode.append("set "+name+"("+name+") {\n");
		pageCode.append("if ("+name+"===_"+name+") return;\n");
		pageCode.append("_"+name+" = "+name+";\n");
		pageCode.append("_eventDispatcher.event('"+name+"Changed');\n");
		pageCode.append("}\n");
		pageCode.append(",get "+name+"() { return _"+name+";} \n");
	}
	
	private String buildModel(String pageName,PageModelType pageModelType) throws JavascribeException {
		StringBuilder build = new StringBuilder();
		StringBuilder objCode = new StringBuilder();
		build.append("_obj.model = (function() {\n");
		objCode.append("return {\n");
		List<String> attribNames = pageModelType.getAttributeNames();

		boolean first = true;
		for(String attrib : attribNames) {
			if (first) first = false;
			else objCode.append(',');
			String typeName = pageModelType.getAttributeType(attrib);
			addAttribute(attrib,typeName,pageModelType,build,objCode);
		}

		objCode.append("};\n");
		build.append(objCode);
		build.append("})();\n");

		return build.toString();
	}

}
