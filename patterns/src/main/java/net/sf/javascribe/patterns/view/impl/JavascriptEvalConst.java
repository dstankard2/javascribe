package net.sf.javascribe.patterns.view.impl;

public class JavascriptEvalConst {

	public static final String[] expr = new String[] {
		"$string$",
		"$number$", 
		"!$expr$",
		"($expr$)",
		"typeof $expr$",
		"$expr$>==$expr$",
		"$expr$<==$expr$",
		"$expr$===$expr$",
		"$expr$==$expr$",
		"$expr$<=$expr$",
		"$expr$&&$expr$",
		"$expr$||$expr$",
		"$expr$>$expr$",
		"$expr$<$expr$",
		"$expr$*$expr$",
		"$expr$/$expr$",
		"$expr$+$expr$",
		"$expr$-$expr$",
		"$expr$%$expr$",
		"$expr$?$expr$:$expr$",
		"$varRef$",
		"$varRef$($fnArgs$)",
		"new $varRef$($fnArgs$)",
		"$varRef$[$expr$]",
		"true",
		"false",
		"undefined",
		"null"
	};

	/*
	public static final String[] expr = new String[] {
		"$number$", 
		"$string$", 
		"function_(_$fnArgs$_)_{$codeLines$}", // function declaration
		"typeof $expr$",
		"(_$expr$_)",
		"!_$expr$",
		"new $varRef$_(_$fnArgs$_)", // instantiation function call
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
		"$varRef$_(_$fnArgs$_)", // function call
		"$varRef$",
		"$varRef$_[_$expr$_]",
		"true",
		"false",
		"undefined",
		"null"
	};
	*/

	// Keywords that varRefs and identifiers cannot be equal to
	public static final String[] keywords = new String[] {
		"true","false","undefined","null","new","if","for"
	};

	public static final String[] codeLine = new String[] {
		"$forLoop$_$codeLine$",
		"$forLoop$_{_$codeLines$_}",
		"var $identifier$_;",
		"var $identifier$_=_$expr$;",
		"if_(_$expr$_)_$codeLine$",
		"if_(_$expr$_)_{_$codeLines$}",
		"while(_$expr$_)_$codeLine$",
		"while(_$expr$_)_{$codeLines$}",
		"$assignment$;",
		"return $expr$;",
		//"$fnCall$;",
		"$expr$;"
	};
	
}

