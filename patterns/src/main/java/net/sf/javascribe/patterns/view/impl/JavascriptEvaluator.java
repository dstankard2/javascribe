package net.sf.javascribe.patterns.view.impl;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.expressions.ExpressionUtil;
import net.sf.javascribe.langsupport.javascript.JavascriptBaseObjectType;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;
import net.sf.javascribe.patterns.view.DirectiveUtils;

public class JavascriptEvaluator {
	private CodeExecutionContext execCtx = null;
	private String code = null;
	private String originalCode = null;
	private String error = null;
	StringBuilder result = new StringBuilder();
	
	public JavascriptEvaluator(String code,CodeExecutionContext execCtx) {
		this.code = code;
		this.originalCode = code;
		this.execCtx = execCtx;
	}
	
	public String getResult() {
		return result.toString();
	}
	
	public String getError() {
		return error;
	}
	
	public String getOriginalCode() {
		return originalCode;
	}
	
	private static final List<String> expressionAtoms = Arrays.asList(new String[] {
			">==", "<==", "===", "!==",
			">=", "<=", "==", "!=","||","&&", "++", "--",
			">", "<", "!", "(", ")", "?", ":", "-", "+", "{", "}"
	});
	private static final List<String> reservedWords = Arrays.asList(new String[] {
			"return", "true", "false", "while", "for",
			"if"
	});

	// Parses a block of arbitrary code.  May include set statements like "x = y".
	public void parseCodeBlock() {
		try {
			StringTokenizer tok = new StringTokenizer(code,";");
			while(tok.hasMoreTokens()) {
				String t = tok.nextToken();
				t = t.trim();
				if (t.length()>0) {
					result.append(parseLine(t.trim(),true));
					result.append(';');
				}
			}
		} catch(Exception e) {
			System.err.println("Couldn't parse code block "+code);
			e.printStackTrace();
			this.error = "Exception while parsing code block";
		}
	}
	
	// Parses an expression that is supposed to evaluate to something
	// such as a boolean on string.
	public void parseExpression() {
		result.append(parseLine(code,false));
	}

	private String buildResult(String base,String addition) {
		if ((base==null) || (addition==null)) return null;
		return base+addition;
	}
	// Common shared API
	protected String parseLine(String line,boolean isCodeBlock) {
		String l = internalParseLine(line,isCodeBlock,false);
		if (l==null) this.error = "Couldn't parse line "+line;
		return l;
	}
	
	private String internalParseLine(String line,boolean isCodeBlock,boolean inFunctionCall) {
		String result = null;
		
		line = line.trim();
		if (line.length()==0) return "";
		if (inFunctionCall) {
			char c = line.charAt(0);
			if (c==',') {
				result = ',' + internalParseLine(line.substring(1),isCodeBlock,true);
			}
			if (c==')') {
				result = ')' + internalParseLine(line.substring(1),isCodeBlock,false);
			}
		}
		if ((result==null) && (isCodeBlock) && (!inFunctionCall)) {
			result = findVariableDeclaration(line);
			if (result==null) result = findSetExpression(line);
			if (result!=null) return result;
		}
		if (result==null) result = findFunctionCall(line,isCodeBlock,inFunctionCall);
		if (result==null) result = findVariableExpression(line,isCodeBlock,inFunctionCall);
		if (result==null) result = findExpressionAtom(line,isCodeBlock,inFunctionCall);
		if (result==null) result = findNumberLiteral(line,isCodeBlock,inFunctionCall);
		if (result==null) result = findStringLiteral(line,isCodeBlock,inFunctionCall);
		/*
		if (result==null) {
			this.error = "Couldn't parse string '"+line+"'";
		} else {
			this.error = null;
		}
		*/

		return result;
	}
	
	protected String findVariableDeclaration(String line) {
		StringBuilder b = new StringBuilder();
		if ((line.startsWith("var")) && (Character.isWhitespace(line.charAt(3)))) {
			b.append("var ");
			line = line.substring(3).trim();
			String id = findIdentifier(line);
			if ((id==null) || (id.indexOf('.')>=0)) return null;
			b.append(id);
			execCtx.addVariable(id, "object");
		} else {
			return null;
		}
		return b.toString();
	}
	
	private String findVariableExpression(String line,boolean isCodeBlock,boolean inFunctionCall) {
		String s = findIdentifier(line);
		if (s==null) return null;
		int i = s.length();
		String remainder = null;
		if (s.length()<line.length()) {
			remainder = line.substring(i).trim();
			if (remainder.length()==0) remainder = null;
		}
		s = DirectiveUtils.getValidReference(s, execCtx);
		//ExpressionUtil.evaluateValueExpression(s, null, execCtx);
		//DirectiveUtils.evaluateExpression(s, execCtx);
		if (remainder!=null) {
			s = buildResult(s,internalParseLine(remainder,isCodeBlock,inFunctionCall));
		}
		
		return s;
	}

	private String findFunctionCall(String line,boolean isCodeBlock,boolean inFunctionCall) {
		StringBuilder b = new StringBuilder();
		String s = findIdentifier(line);
		if (s==null) return null;
		int i = s.length();
		if (i==line.length()) return null;
		
		String remainder = line.substring(i).trim();
		if (remainder.charAt(0)!='(') return null;
		remainder = remainder.substring(1);
		String result = internalParseLine(remainder, isCodeBlock, true);
		if (result==null) return null;

		/**
		 * Append the identifier to the return value
		 * For now, if the identifier has a '.' then it is a reference to a function on the 
		 * window which is handwritten by the user.  Otherwise it might be a function on the 
		 * page, if there is one.
		 */
		if ((execCtx.getTypeForVariable(DirectiveUtils.PAGE_VAR)!=null) && (s.indexOf('.')<0)) {
			JavascriptBaseObjectType pageType = (JavascriptBaseObjectType)execCtx.getTypeForVariable(DirectiveUtils.PAGE_VAR);
			List<JavascriptFunctionType> fns = pageType.getOperations(s);
			if (fns.size()>0) {
				if (fns.size()>1) {
					this.error = "Couldn't evaluate a Javascript expression because page "+pageType.getName()+" had multiple functions called "+s;
					return null;
				}
				s = DirectiveUtils.PAGE_VAR+'.'+s;
			}
		}

		b.append(s);
		
		b.append('(').append(result);
		
		return b.toString();
	}
	
	// TODO: Implement
	private String findSetExpression(String line) {
		String left = findIdentifier(line);
		if (left==null) return null;
		line = line.substring(left.length()).trim();
		if (line.length()==0) return null;
		if (line.charAt(0)!='=') return null;
		String right = internalParseLine(line.substring(1), true, false);
		if (right==null) return null;
		String ret = null;
		try {
			ret = ExpressionUtil.evaluateSetExpression(left, right, execCtx);
		} catch(Exception e) { }
		
		return ret;
		//return null;
		//return internalParseLine(line, isCodeBlock, false);
	}
	
	private String findExpressionAtom(String line,boolean isCodeBlock,boolean inFunctionCall) {
		for(String s : expressionAtoms) {
			if (line.indexOf(s)==0) {
				return buildResult(s,internalParseLine(line.substring(s.length()),isCodeBlock,inFunctionCall));
				//return s+internalParseLine(line.substring(s.length()),isCodeBlock,inFunctionCall);
			}
		}
		for(String s : reservedWords) {
			if (line.indexOf(s)==0) {
				return buildResult(s,internalParseLine(line.substring(s.length()),isCodeBlock,inFunctionCall));
				//return s + internalParseLine(line.substring(s.length()),isCodeBlock,inFunctionCall);
			}
		}
		return null;
	}
	
	// Returns a number (integer or decimal) if the expression starts with one
	private String findNumberLiteral(String line,boolean isCodeBlock,boolean inFunctionCall) {
		boolean decimal = false;
		String s = "";
		for(int i=0;i<line.length();i++) {
			char c = line.charAt(i);
			if ((c=='.') && (decimal)) return null;
			if ((Character.isDigit(c)) || (c=='.')) {
				s = s + c;
			}
			else break;
		}
		if (s.length()==0) return null;
		return buildResult(s,internalParseLine(line.substring(s.length()),isCodeBlock,inFunctionCall));
		//return s + internalParseLine(line.substring(s.length()), isCodeBlock, inFunctionCall);
	}

	// For use when looking for an atom that might be a string expression
	// returns null if the expr doesn't start with a string ' "
	private String findStringLiteral(String expr,boolean isCodeBlock,boolean inFunctionCall) {
		char start = expr.charAt(0);
		String s = "";
		
		if ((start!='\'') && (start!='"')) return null;
		
		s = s + start;
		String ignore = "\\"+start;
		for(int i=1;i<expr.length();i++) {
			char c = expr.charAt(i);
			s = s + c;
			if (i<expr.length()-1) {
				if (expr.substring(i, i+2).equals(ignore)) {
					i++;
					s = s + expr.charAt(i);
					continue;
				}
				if (c==start) {
					// This is the end of the string
					//s = s + start;
					if (i==expr.length()-1) return s;
					else return buildResult(s,internalParseLine(expr.substring(i+1),isCodeBlock,inFunctionCall));
					//else return s + internalParseLine(expr.substring(i+1), isCodeBlock, inFunctionCall);
				}
			}
		}
		return null;
	}

	private static String findIdentifier(String line) {
		StringBuilder ref = new StringBuilder();
		boolean first = true;
		
		for(int i=0;i<line.length();i++) {
			char c = line.charAt(i);
			if (first) {
				if (!Character.isJavaIdentifierStart(c)) return null;
				first = false;
			} else {
				if (c=='.') {
					first = true;
				} else if (!Character.isJavaIdentifierPart(c)) break;
			}
			ref.append(c);
		}
		
		for(String s : reservedWords) {
			if (s.equals(ref.toString())) return null;
		}
		
		return ref.toString();
	}

}

