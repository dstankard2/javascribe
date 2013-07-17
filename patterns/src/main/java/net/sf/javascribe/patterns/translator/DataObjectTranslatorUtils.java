package net.sf.javascribe.patterns.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;

public class DataObjectTranslatorUtils {

	public static List<FieldTranslator> geTranslationStrategy(String name,ProcessorContext ctx) throws JavascribeException {
		List<FieldTranslator> ret = null;
		HashMap<String,List<FieldTranslator>> map = null;
		
		map = (HashMap<String,List<FieldTranslator>>)ctx.getObject(TranslationStrategy.TRANSLATION_OPERATIONS);
		if (map==null) {
			map = new HashMap<String,List<FieldTranslator>>();
			ctx.putObject(TranslationStrategy.TRANSLATION_OPERATIONS, map);
		}
		ret = map.get(name);
		if (ret==null) {
			ret = loadStrategy(name,ctx);
			map.put(name, ret);
		}
		
		return ret;
	}

	public static void storeTranslationStrategy(TranslationStrategy t,ProcessorContext ctx) {
		ctx.putObject(TranslationStrategy.TRANSLATION_STRATEGY+t.getName(), t);
	}
	
	private static List<FieldTranslator> loadStrategy(String name,ProcessorContext ctx) throws JavascribeException {
		List<FieldTranslator> ret = new ArrayList<FieldTranslator>();
		TranslationStrategy strat = (TranslationStrategy)ctx.getObject(TranslationStrategy.TRANSLATION_STRATEGY+name);
		
		if (strat==null) {
			throw new JavascribeException("Could not find a translation strategy called '"+name+"'");
		}
		
		for(TranslationOperation op : strat.getOperation()) {
			FieldTranslator trans = getTranslator(op.getName(),ctx);
			if (trans==null) {
				throw new JavascribeException("Could not find translation operation '"+op.getName()+"'");
			}
			ret.add(trans);
		}
		
		return ret;
	}
	
	private static FieldTranslator getTranslator(String name,ProcessorContext ctx) throws JavascribeException {
		TranslationOperation ret = null;
		List<Class<?>> classes = ctx.getEngineProperties().getScannedClassesOfInterface(FieldTranslator.class);

			for(Class<?> cl : classes) {
				try {
				FieldTranslator trans = (FieldTranslator)cl.newInstance();
				if (trans.name().equals(name)) return trans;
				} catch(Exception e) {
					throw new JavascribeException("Could not instantiate field translator "+cl.getCanonicalName());
				}
			}
		
		return null;
	}
	
}

