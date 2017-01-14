package net.sf.javascribe.patterns.js.navigation;

import java.util.List;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptCode;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.langsupport.javascript.JavascriptObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;
import net.sf.javascribe.langsupport.javascript.JavascriptUtils;
import net.sf.javascribe.patterns.js.page.PageModelType;
import net.sf.javascribe.patterns.js.page.PageType;
import net.sf.javascribe.patterns.js.page.PageUtils;
import net.sf.javascribe.patterns.xml.navigation.Page;
import net.sf.javascribe.patterns.xml.navigation.PageNavigation;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class PageNavigationProcessor {

	private static final Logger log = Logger.getLogger(PageNavigationProcessor.class);
	
	@ProcessorMethod(componentClass=PageNavigation.class)
	public void process(PageNavigation comp,ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Javascript");
		
		JavascriptSourceFile src = JavascriptUtils.getSourceFile(ctx);
		if (comp.getName().trim().length() == 0) {
			throw new JavascribeException(
					"Found a Page Navigation component with no name.");
		}
		
		log.info("Processing Page Navigation '"+comp.getName()+"'");
		
		AnimationProvider provider = getAnimationProvider(ctx);
		if (provider==null) {
			throw new JavascribeException("Couldn't find animation provider for Page Navigation component");
		}
		
		JavascriptObjectType type = new JavascriptObjectType(comp.getName());
		ctx.getTypes().addType(type);

		src.getSource().append("var "+comp.getName()+" = { currentPage : null, currentDiv : null };\n");
		
		if (comp.getPage().size()==0) {
			throw new JavascribeException("A Page Navigation component must have at least 1 page.");
		}
		
		StringBuilder showPageCode = new StringBuilder();
		StringBuilder refreshPageCode = new StringBuilder();
		JavascriptCode append = null;

		JavascriptFunctionType fn = new JavascriptFunctionType("hideCurrentPage");
		type.addOperation(fn);
		src.getSource().append(comp.getName()+".hideCurrentPage = function() {\n")
			.append("if (this.currentPage!=null) {\n");
		append = provider.hide("this.currentDiv",comp.getHide(),"400",null);
		src.getSource().append(append.getCodeText());
		src.getSource().append("}\n};\n");

		fn = new JavascriptFunctionType("switchPage");
		fn.addParam("page", "string");
		fn.addParam("data", "object");
		type.addOperation(fn);

		src.getSource().append(comp.getName()+".switchPage = function(pageName,data) {\n")
			.append("if (this.currentPage!=null) {\n")
			.append("var temp = this.currentPage;\n");
		append = provider.hide("this.currentDiv", comp.getHide(),"400", "function() { "+comp.getName()+".showPage(pageName,data); }");
		src.getSource().append(append.getCodeText());
			//.append("$('#'+this.currentDiv).hide('"+comp.getHide()+"',{},400,function() { "+comp.getName()+".showPage(pageName,data); });\n");

		boolean first = true;
		for(Page p : comp.getPage()) {
			if (p.getOnHide().trim().length()>0) {
				if (first) first = false;
				else src.getSource().append("else ");
				src.getSource().append("if (temp=='"+p.getName()+"') {\n");
				src.getSource().append(p.getName()+".event('"+p.getOnHide()+"'); }\n");
			}
		}
		
		fn = new JavascriptFunctionType("showPage");
		fn.addParam("pageName", "string");
		fn.addParam("data", "object");
		type.addOperation(fn);

		src.getSource().append("} else {\n")
			.append("this.showPage(pageName,data);\n")
			.append("}\n}.bind("+comp.getName()+");\n");
		
		showPageCode.append(comp.getName()+".showPage = function(pageName,data) {\n");
		showPageCode.append("this.currentPage=pageName;\n");
		showPageCode.append("var div = null;\n");
		first = true;
		for(Page p : comp.getPage()) {
			// Get the page and model types
			PageType pageType = PageUtils.getPageType(ctx, p.getName());
			if (pageType==null) throw new JavascribeException("Could not find type for page '"+p.getName()+"'");
			PageModelType modelType = PageUtils.getModelType(ctx, p.getName());

			// Append to showPage
			if (first) first = false;
			else showPageCode.append("else ");
			showPageCode.append("if (pageName=='"+p.getName()+"') {\n")
				.append("div = '").append(p.getDiv()).append("';\n")
				.append("this.currentDiv = div;\n");
			// Set page model attribs from data parameter, if necessary
			showPageCode.append("if (data!=null) {\n");
			if (modelType!=null) {
				List<String> att = modelType.getAttributeNames();
				for(String s : att) {
					showPageCode.append("if (data.hasOwnProperty('"+s+"')) { ")
						.append(p.getName()+".model.set").append(Character.toUpperCase(s.charAt(0)))
						.append(s.substring(1)).append("(data."+s+");}\n");
				}
			}
			showPageCode.append("}\n");
			if (p.getOnShow().trim().length()>0) {
				showPageCode.append(p.getName()+".event('"+p.getOnShow()+"');\n");
			}
			showPageCode.append("}\n");
		}
		showPageCode.append("else { alert('Unrecognized page '+this.currentPage); return; }\n");
		append = provider.show("this.currentDiv", comp.getShow(), "400", null);
		showPageCode.append(append.getCodeText());
		//showPageCode.append("$('#'+this.currentDiv).show('"+comp.getShow()+"',{},400,null);\n");
		showPageCode.append("}.bind("+comp.getName()+");\n");

		fn = new JavascriptFunctionType("refreshCurrentPage");
		type.addOperation(fn);
		refreshPageCode.append(comp.getName()+".refreshCurrentPage = function() {\n")
			.append("if (this.currentPage!=null) {\n");
		first = true;
		for(Page p : comp.getPage()) {
			if (p.getOnRefresh().trim().length()>0) {
				if (first) first = false;
				else refreshPageCode.append("else ");
				refreshPageCode.append("if (this.currentPage=='"+p.getName()+"') {\n");
				refreshPageCode.append(p.getName()+".event('"+p.getOnRefresh()+"');\n");
				refreshPageCode.append("}\n");
			}
		}
		refreshPageCode.append("}\n}.bind("+comp.getName()+");\n");
		
		
		src.getSource().append(showPageCode);
		src.getSource().append(refreshPageCode);
	}
	
	protected static AnimationProvider getAnimationProvider(ProcessorContext ctx) {
		String prop = ctx.getProperty(PageNavigation.PROPERTY_ANIMATION_PROVIDER);
		if (prop==null) prop = "jquery";
		List<Class<?>> classes = ctx.getEngineProperties().getScannedClassesOfInterface(AnimationProvider.class);
		for(Class<?> cl : classes) {
			try {
				AnimationProvider a = (AnimationProvider)cl.newInstance();
				if (a.getName().equals(prop)) return a;
			} catch(Exception e) {
				// TODO
			}
		}
		return null;
	}

}

