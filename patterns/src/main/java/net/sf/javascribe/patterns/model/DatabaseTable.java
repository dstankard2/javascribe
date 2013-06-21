package net.sf.javascribe.patterns.model;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTable {

	private List<DatabaseTableColumn> columns = new ArrayList<DatabaseTableColumn>();
	private String name = null;
	private String primaryKeyColumn = null;
	private String versionField = null;
	
	public String getVersionField() {
		return versionField;
	}
	public void setVersionField(String versionField) {
		this.versionField = versionField;
	}
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	public List<DatabaseTableColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<DatabaseTableColumn> columns) {
		this.columns = columns;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
