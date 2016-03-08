package rs.bignumbers;

import java.util.HashMap;
import java.util.Map;

enum StatementType {
	Insert, Update, Delete
}

public class Statement {
	private String sql;
	private Map<String, Object> parameters;
	private StatementType statementType;
	private Object object;
	
	public Statement() {
		parameters = new HashMap<String, Object>();
	}

	public Statement(String sql, Map<String, Object> parameters,StatementType statementType, Object object) {
		this.sql = sql;
		this.parameters = parameters;
		this.statementType = statementType;
		this.object = object;
	}

	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public StatementType getStatementType() {
		return statementType;
	}

	public void setStatementType(StatementType statementType) {
		this.statementType = statementType;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "sql " + statementType + ": " + sql + " with parameters:" + parameters.toString();
	}
}