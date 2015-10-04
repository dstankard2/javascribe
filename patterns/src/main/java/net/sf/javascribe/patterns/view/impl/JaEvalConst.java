package net.sf.javascribe.patterns.view.impl;

public class JaEvalConst {

	public static final String[] expr = new String[] {
		"($expr$)",
		"new $fnCall$",
		"$fnCall$",
		"!$expr$",
		"typeof $expr$",
		//"--$varRef$",
		//"++$varRef$",
		"function($fnArgs$){$codeLines$}",
		"$varRef$[$expr$]",
		//"$varRef$++",
		//"$varRef$--",
		"$expr$?$expr$:$expr$",
		"$expr$===$expr$",
		"$expr$<==$expr$",
		"$expr$>==$expr$",
		"$expr$!=$expr$",
		"$expr$==$expr$",
		"$expr$<=$expr$",
		"$expr$>=$expr$",
		"$expr$&&$expr$",
		"$expr$||$expr$",
		"$expr$+$expr$",
		"$expr$-$expr$",
		"$expr$*$expr$",
		"$expr$/$expr$",
		"$expr$<$expr$",
		"$expr$>$expr$",
		"$expr$%$expr$",
		"$string$", 
		"$number$", 
		"$varRef$",
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
		"var $identifier$;",
		"var $identifier$=$expr$;",
		//"new $fnCall$;",// This is an expression
		"if($expr$)$codeLine$",
		"if($expr$){$codeLines$}",
		"for($declaration$;$expr$;$expr$)$codeLine$",
		"for($declaration$;$expr$;$expr$){$codeLines$}",
		"return $expr$;",
		//"$varRef$=$expr$;", // assignment
		"$fnCall$;",
		"$expr$;"
	};
	
}

