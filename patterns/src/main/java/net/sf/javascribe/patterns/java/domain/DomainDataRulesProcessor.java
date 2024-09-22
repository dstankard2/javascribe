package net.sf.javascribe.patterns.java.domain;

import java.util.List;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;
import net.sf.javascribe.langsupport.java.types.impl.JavaServiceType;
import net.sf.javascribe.patterns.xml.java.domain.DomainDataRules;

@Plugin
public class DomainDataRulesProcessor implements ComponentProcessor<DomainDataRules> {

	@Override
	public void process(DomainDataRules comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		String content = comp.getContent();
		
		DomainDataRuleSet rules = DomainRuleUtils.getDomainDataRules(ctx);
		
		if (content.trim().length()==0) {
			throw new JavascribeException("Tried to process domain data rules but they were empty");
		}
		
		String[] str = content.split("\n");
		for(String s : str) {
			s = s.trim();
			if (s.length()==0) continue;
			DomainDataRule newRule = new DomainDataRule();
			int eq = s.indexOf('=');
			if (eq<1) {
				throw new JavascribeException("Couldn't parse rule '"+s+"' - = was not in expected position");
			}
			String attr = s.substring(0, eq).trim();
			String ref = s.substring(eq+1).trim();
			newRule.setAttribute(attr);
			List<ServiceOperation> ops = JavascribeUtils.findRuleFromRef(ref, ctx);
			newRule.setOperations(ops);
			String serviceAttr = ref.substring(0, ref.indexOf('.'));
			JavaServiceType srv = JavascribeUtils.getTypeForSystemAttribute(JavaServiceType.class, serviceAttr, ctx);
			newRule.setServiceType(srv);
			newRule.setServiceRef(serviceAttr);
			rules.addRule(newRule);
		}
	}

}

