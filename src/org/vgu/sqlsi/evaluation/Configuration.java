package org.vgu.sqlsi.evaluation;

import java.io.File;

/**
 * Stores the relevant configuration options read from the environment
 * variables.
 */
public class Configuration {

	private String sMode;
	private String sScenario, sQueryExec, sProcedureCall, sUser, sRole;

	private int runIndex;
	private String sTool, sQuery, dataModelPath, securityModelPath;
	private File dataModel, securityModel;

	public String getsTool() {
		return sTool;
	}

	public void setsTool(String sTool) {
		this.sTool = sTool;
	}

	public String getsQuery() {
		return sQuery;
	}

	public void setsQuery(String sQuery) {
		this.sQuery = sQuery;
	}

	public int getRunIndex() {
		return runIndex;
	}

	public void setRunIndex(int runIndex) {
		this.runIndex = runIndex;
	}

	public File getDataModel() {
		return dataModel;
	}

	public void setDataModel(File dataModel) {
		this.dataModel = dataModel;
	}

	public File getSecurityModel() {
		return securityModel;
	}

	public void setSecurityModel(File securityModel) {
		this.securityModel = securityModel;
	}

	public String getDataModelPath() {
		return dataModelPath;
	}

	public void setDataModelPath(String dataModelPath) {
		this.dataModelPath = dataModelPath;
	}

	public String getSecurityModelPath() {
		return securityModelPath;
	}

	public void setSecurityModelPath(String securityModelPath) {
		this.securityModelPath = securityModelPath;
	}

	public String getsMode() {
		return sMode;
	}

	public void setsMode(String sMode) {
		this.sMode = sMode;
	}

	public String getsProcedureCall() {
		return sProcedureCall;
	}

	public void setsProcedureCall(String sProcedureCall) {
		this.sProcedureCall = sProcedureCall;
	}

	public String getsUser() {
		return sUser;
	}

	public void setsUser(String sUser) {
		this.sUser = sUser;
	}

	public String getsRole() {
		return sRole;
	}

	public void setsRole(String sRole) {
		this.sRole = sRole;
	}

	public String getsScenario() {
		return sScenario;
	}

	public void setsScenario(String sScenario) {
		this.sScenario = sScenario;
	}

	public String getsQueryExec() {
		return sQueryExec;
	}

	public void setsQueryExec(String sQueryExec) {
		this.sQueryExec = sQueryExec;
	}

}
