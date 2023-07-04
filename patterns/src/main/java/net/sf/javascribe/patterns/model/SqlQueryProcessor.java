package net.sf.javascribe.patterns.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import net.sf.javascribe.api.ComponentProcessor;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaClassSourceFile;
import net.sf.javascribe.langsupport.java.JavaUtils;
import net.sf.javascribe.langsupport.java.types.JavaVariableType;
import net.sf.javascribe.langsupport.java.types.impl.JavaDataObjectType;
import net.sf.javascribe.patterns.xml.model.SqlQuery;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

//@Plugin
public class SqlQueryProcessor implements ComponentProcessor<SqlQuery> {

	@Override
	public void process(SqlQuery comp, ProcessorContext ctx) throws JavascribeException {
		ctx.setLanguageSupport("Java8");
		if (comp.getQuery().trim().length()==0) {
			throw new JavascribeException("No query string found for sqlQuery component");
		}

		CCJSqlParserManager pm = new CCJSqlParserManager();
		Statement stmt = null;
		try {
			stmt = pm.parse(new StringReader(comp.getQuery()));
		} catch(JSQLParserException e) {
			throw new JavascribeException("Couldn't parse SQL query", e);
		}

		EntityManagerLocator loc = 
				JavascribeUtils.getType(EntityManagerLocator.class, comp.getEmLocator(), ctx);
		JavaClassSourceFile src = null;
		String serviceName = comp.getService();
		String ruleName = comp.getRuleName();
		
		String pkg = JavaUtils.getJavaPackage(comp, ctx);
		src = JavaUtils.getClassSourceFile(pkg+'.'+serviceName, ctx);
		JavaClassSource cl = src.getSrc();

		if (stmt instanceof Select) {
			Select sel = (Select)stmt;
			handleSelect(comp, sel,cl,pkg, ctx);
		} else {
			throw new JavascribeException("NativeQuery component only supports select statements for now");
		}
		
		ctx.getLog().warn("TODO: Process SqlQuery component");
	}

	protected void handleSelect(SqlQuery comp, Select sel, JavaClassSource cl,String pkg, ProcessorContext ctx) throws JavascribeException {
		String dataObjName = comp.getResultType();
		JavaDataObjectType dataType = new JavaDataObjectType(dataObjName,pkg+'.'+dataObjName,ctx.getBuildContext());
		JavaClassSourceFile dataObjSrc = new JavaClassSourceFile(ctx);
		ctx.addVariableType(dataType);
		dataObjSrc.getSrc().setPackage(pkg);
		dataObjSrc.getSrc().setName(dataObjName);
		ctx.addSourceFile(dataObjSrc);
		SelectBody body = sel.getSelectBody();
		List<String> attributes = new ArrayList<>();

		if (body instanceof PlainSelect) {
			PlainSelect ps = (PlainSelect)body;
			List<SelectItem> selectItems = ps.getSelectItems();
			for(SelectItem item : selectItems) {
				String name = null;
				String typeName = null;
				if (item instanceof SelectExpressionItem) {
					SelectExpressionItem i = (SelectExpressionItem)item;
					if (i.getAlias()!=null) {
						name = i.getAlias().getName();
					} else if (i.getExpression() instanceof Column) {
						Column c = (Column)i.getExpression();
						name = c.getColumnName();
					}
				}
				if (name==null) {
					throw new JavascribeException("Couldn't find property name from select item '"+item.toString()+"'");
				}
				typeName = ctx.getSystemAttribute(name);
				if (typeName==null) {
					throw new JavascribeException("Found no system attribute named '"+name+"'");
				}
				attributes.add(name);
			}
			Expression whereEx = ps.getWhere();
		} else {
			throw new JavascribeException("The select statement is not specified - it is not a PlainSelect");
		}

		for(String name : attributes) {
			String typeName = ctx.getSystemAttribute(name);
			JavaVariableType type = JavascribeUtils.getType(JavaVariableType.class, typeName, ctx);
			if (type.getImport()!=null) {
				dataObjSrc.addImport(type);
			}
			dataObjSrc.getSrc().addProperty(type.getClassName(), name);
			dataType.addProperty(name, typeName);
		}

		System.out.println("Hi");
	}
	
}
