package net.sf.javascribe.patterns.js.page;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.javascript.JavascriptSourceFile;

@Scannable
@Processor
public class PageFinalizerProcessor {

	@ProcessorMethod(componentClass=PageFinalizer.class)
	public void process(PageFinalizer comp,ProcessorContext ctx) throws JavascribeException {
		
		System.out.println("Finalizing page '"+comp.getPageName()+"'");

		StringBuilder init = PageUtils.getInitFunction(ctx, comp.getPageName());
		JavascriptSourceFile src = (JavascriptSourceFile)ctx.getSourceFile(comp.getJsFileName());
		
		init.append("}.bind("+comp.getPageName()+");\n");
		src.getSource().append(init);
	}

}
