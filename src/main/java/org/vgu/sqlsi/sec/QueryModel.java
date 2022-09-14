package org.vgu.sqlsi.sec;

import org.json.simple.JSONArray;

public class QueryModel {
	private String name;
	private JSONArray vars;
	private JSONArray pars;
	private String statement;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JSONArray getVars() {
		return vars;
	}

	public void setVars(JSONArray vars) {
		this.vars = vars;
	}

	public JSONArray getPars() {
		return pars;
	}

	public void setPars(JSONArray pars) {
		this.pars = pars;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

}
