package net.sf.javascribe.langsupport.javascript.types;

import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public abstract class PromiseType extends JavascriptServiceType {

	protected ProcessorContext ctx;
	public void setProcessorContext(ProcessorContext ctx) {
		this.ctx = ctx;
	}
	
	public PromiseType() {
		super("Promise");
	}
	
	public abstract String getResolveTypeName();

	public abstract JavascriptType getResolveType();

	protected void name(String name) {
		this.name = name;
	}

	public static PromiseType noResultPromise() {
		PromiseType ret = new PromiseType() {
			
			@Override
			public JavascriptType getResolveType() {
				return null;
			}

			@Override
			public String getResolveTypeName() {
				return null;
			}

		};
		ret.name("Promise");
		return ret;
	}

	public static PromiseType getPromise(String resolveTypeName, ProcessorContext ctx) {
		PromiseType ret = new PromiseType() {
			
			@Override
			public JavascriptType getResolveType() {
				try {
					this.ctx.setLanguageSupport("Javascript");
					return JavascribeUtils.getType(JavascriptType.class, resolveTypeName, this.ctx);
				} catch(JavascribeException e) {
					System.out.println("ugh");
					// no-op
				}
				return null;
			}

			@Override
			public String getResolveTypeName() {
				return resolveTypeName;
				// return resolveType.getName();
			}

		};
		ret.name(resolveTypeName+"Promise");
		ret.setProcessorContext(ctx);
		return ret;
	}
	
}

