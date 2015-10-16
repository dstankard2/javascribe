package net.sf.javascribe.patterns.view.impl;

public class JaEvalConst {

	public static final String[] expr = new String[] {
		//"--$varRef$",
		//"++$varRef$",
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
		"($expr$)",
		"new $fnCall$",
		"$fnCall$",
		"!$expr$",
		"typeof $expr$",
		"function($fnArgs$){$codeLines$}",
		"$varRef$[$expr$]",
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
		"var $identifier$;",
		"var $identifier$=$expr$;",
		"if ($expr$) $codeLine$",
		"if ($expr$) { $codeLines$}",
		"for ($declaration$;$expr$;$expr$) $codeLine$",
		"for ($declaration$;$expr$;$expr$) { $codeLines$}",
		"return $expr$;",
		"$fnCall$;",
		"$expr$;"
	};
	
}

