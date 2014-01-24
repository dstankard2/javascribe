package net.sf.javascribe.patterns.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.annotation.Processor;
import net.sf.javascribe.api.annotation.ProcessorMethod;
import net.sf.javascribe.api.annotation.Scannable;

import org.apache.log4j.Logger;

@Scannable
@Processor
public class RetrieveDataStrategyProcessor {

	private static final Logger log = Logger.getLogger(RetrieveDataStrategyProcessor.class);
	public static final String STRATEGY_LOCATION = "net.sf.javascribe.patterns.domain.RetrieveDataStrategy.strategy.";
	
	@ProcessorMethod(componentClass=RetrieveDataStrategy.class)
	public void process(RetrieveDataStrategy comp,ProcessorContext ctx) throws JavascribeException {
		List<Resolver> strategy = new ArrayList<Resolver>();

		log.info("Processing data retrieval strategy '"+comp.getName()+"'");

		List<Class<?>>ifs = ctx.getEngineProperties().getScannedClassesOfInterface(Resolver.class);
		HashMap<String,Resolver> map = new HashMap<String,Resolver>();
		
		for(Class<?> cl : ifs) {
			try {
				Resolver res = (Resolver)cl.newInstance();
				map.put(res.name(), res);
			} catch(Exception e) {
				log.warn("Couldn't load all retrieve data rule resolvers",e);
			}
		}
		// Now parse the strategy and see what's there
		for(RetrieveDataOperation op : comp.getOperation()) {
			if (op.getName().trim().length()==0) {
				throw new JavascribeException("Invalid strategy has an operation with no name");
			}
			Resolver res = map.get(op.getName().trim());
			if (res==null) {
				throw new JavascribeException("Couldn't find a retrieve data operation named '"+op.getName().trim()+"'");
			}
			strategy.add(res);
		}
		if (strategy.size()==0) {
			throw new JavascribeException("Found an invalid strategy with no operations");
		}
		ctx.putObject(STRATEGY_LOCATION+comp.getName(), strategy);
	}
	
	public static List<Resolver> findStrategy(ProcessorContext ctx,String name) throws JavascribeException {
		List<Resolver> ret = null;
		
		ret = (List<Resolver>)ctx.getObject(STRATEGY_LOCATION+name);
		if (ret==null) {
			throw new JavascribeException("Couldn't find strategy '"+name+"'");
		}
		
		return ret;
	}
	
}
