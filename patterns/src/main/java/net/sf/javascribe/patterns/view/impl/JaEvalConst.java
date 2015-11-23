package net.sf.javascribe.patterns.view.impl;

public class JaEvalConst {

	public static final String[] expr = new String[] {
		"$expr$_?_$expr$_:_$expr$",
		"$expr$_===_$expr$",
		"$expr$_<==_$expr$",
		"$expr$_>==_$expr$",
		"$expr$_!=_$expr$",
		"$expr$_==_$expr$",
		"$expr$_<=_$expr$",
		"$expr$_>=_$expr$",
		"$expr$_&&_$expr$",
		"$expr$_||_$expr$",
		"$expr$_+_$expr$",
		"$expr$_-_$expr$",
		"$expr$_*_$expr$",
		"$expr$_/_$expr$",
		"$expr$_<_$expr$",
		"$expr$_>_$expr$",
		"$expr$_%_$expr$",
		"(_$expr$_)",
		"new $fnCall$",
		"$fnCall$",
		"!_$expr$",
		"typeof $expr$",
		"function_(_$fnArgs$_)_{_$codeLines$_}",
		"$varRef$_[_$expr$_]",
		"$varRef$",
		"$number$", 
		"$string$", 
		"true",
		"false",
		"undefined",
		"null"
	};
	
	// Keywords that varRefs and identifiers cannot be equal to
	public static final String[] keywords = new String[] {
		"true","false","undefined","null","new","if","for"
	};
	
	public static final String[] codeLine = new String[] {
		"_$forLoop$_$codeLine$",
		"_$forLoop$_{_$codeLines_}",
		"_var $identifier$_;",
		"_var $identifier$_=_$expr$;",
		"_if_(_$expr$_)_$codeLine$",
		"_if_(_$expr$_)_{_$codeLines$}",
		"_while(_$expr$_)_$codeLine$",
		"_while(_$expr$_)_{$codeLines}",
		"_$assignment$",
		"_return $expr$;",
		"_$fnCall$;",
		"_$expr$;"
		//";"
	};
	
}

