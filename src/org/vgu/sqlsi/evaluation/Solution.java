package org.vgu.sqlsi.evaluation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.vgu.sqlsi.main.SqlSI;

public class Solution {

	private static final String METRIC_DATAMODEL_TIME = "DataModelTransformTimeSecs";
	private static final String METRIC_SECURITY_TIME = "SecurityModelTransformTimeSecs";
	private static final String METRIC_QUERY_REWRITING_TIME = "QueryRewritingTimeSecs";
	private static final String METRIC_EXECUTION_TIME = "ExecutionTimeSecs";
	private static final String METRIC_EXECUTION_AUTH_TIME = "ExecutionAuthTimeSecs";

	private static final String databaseSchemaURI = "uni.sql";
	private static final String sqlAuthFuncURI = "uni_sec.sql";
	private static final String schemaName = "uni";

	public void runDMSMTransformation(Configuration c) {
		SqlSI sqlsi = new SqlSI();
		
		try {
			sqlsi.setUpDataModel(c.getDataModel());
//			final long nanosDMActionStart = System.nanoTime();
			sqlsi.generateDBSchema(databaseSchemaURI, schemaName);
//			final long nanosDMActionEnd = System.nanoTime();
//			final double timeInSecs = ((double) nanosDMActionEnd - nanosDMActionStart) / 1_000_000_000;
//			printDMSMMetric(c, METRIC_DATAMODEL_TIME, timeInSecs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			sqlsi.setUpSecurityModel(c.getSecurityModel());
			final long nanosSMActionStart = System.nanoTime();
			sqlsi.generateSQLAuthFunctions(sqlAuthFuncURI);
			final long nanosSMActionEnd = System.nanoTime();
			final double timeInSecs = ((double) nanosSMActionEnd - nanosSMActionStart) / 1_000_000_000;
			printDMSMMetric(c, METRIC_SECURITY_TIME, timeInSecs);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void printDMSMMetric(Configuration c, String metricName, Object metricValue) {
		// Tool;DataModel;SecurityModel;RunIndex;MetricName;MetricValue

		System.out.println(String.format("%s;%s;%s;%d;%s;%s", c.getsTool(), c.getDataModel().getName(),
				c.getSecurityModel().getName(), c.getRunIndex(), metricName, metricValue.toString()));
	}

	public void runSQLSITransformation(Configuration c) {
		Connection conn = DatabaseConnection.getConnection(c.getsScenario());
		final String query = c.getsQueryExec();
		Statement st;
		
		final String callStatement = String.format("{call %s(?,?)}", c.getsProcedureCall());
		CallableStatement cs;
		try {
			cs = conn.prepareCall(callStatement);
			cs.setString(1, c.getsUser());
			cs.setString(2, c.getsRole());
			final long nanosExecutionStart = System.nanoTime();
			cs.execute();
			final long nanosExecutionEnd = System.nanoTime();
			final double timeInSecs = ((double) nanosExecutionEnd - nanosExecutionStart) / 1_000_000_000;
			printSQLSIMetric(c, METRIC_EXECUTION_TIME, timeInSecs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		
//		try {
//			final long nanosExecutionStart = System.nanoTime();
//			st = conn.createStatement();
//			st.executeQuery(query);
//			final long nanosExecutionEnd = System.nanoTime();
//			final double timeInSecs = ((double) nanosExecutionEnd - nanosExecutionStart) / 1_000_000_000;
//			printQueryExecutionMetric(c, METRIC_EXECUTION_TIME, timeInSecs);
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
	}

	private void printQueryExecutionMetric(Configuration c, String metricName, Object metricValue) {
		String scenario = c.getsScenario().substring(3);

		System.out.println(String.format("%s;%d;%s;%d;%s;%s;%s;%s", c.getsTool(), Integer.valueOf(scenario),
				c.getsProcedureCall(), c.getRunIndex(), "any", "any", metricName, metricValue.toString()));
		
	}

	private void printSQLSIMetric(Configuration c, String metricName, Object metricValue) {
		// Tool;Scenario;ProcedureCall;RunIndex;User;Role;MetricName;MetricValue
		String user = null;
		if (c.getsUser().contains("0")) {
			user = "lidn";
		} else {
			user = c.getsUser();
		}
		String scenario = c.getsScenario().substring(3);

		System.out.println(String.format("%s;%d;%s;%d;%s;%s;%s;%s", c.getsTool(), Integer.valueOf(scenario),
				c.getsProcedureCall(), c.getRunIndex(), user, c.getsRole(), metricName, metricValue.toString()));

	}

	public void runQR(Configuration c) {
		SqlSI sqlsi = new SqlSI();
		try {
			sqlsi.setUpDataModel(c.getDataModel());
			sqlsi.generateDBSchema(databaseSchemaURI, schemaName);
			sqlsi.setUpSecurityModel(c.getSecurityModel());
			sqlsi.generateSQLAuthFunctions(sqlAuthFuncURI);
			final long nanosQRActionStart = System.nanoTime();
			String s = sqlsi.getSecQuery(c.getsQuery());
			final long nanosQRActionEnd = System.nanoTime();
			final double timeInSecs = ((double) nanosQRActionEnd - nanosQRActionStart) / 1_000_000_000;
			printQRMetric(c, METRIC_QUERY_REWRITING_TIME, timeInSecs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printQRMetric(Configuration c, String metricName, Object metricValue) {
		// Tool;DataModel;SecurityModel;Query;RunIndex;MetricName;MetricValue

		System.out.println(String.format("%s;%s;%s;%s;%d;%s;%s", c.getsTool(), c.getDataModel().getName(),
				c.getSecurityModel().getName(), c.getsQuery(), c.getRunIndex(), metricName, metricValue.toString()));
	}

	public void runExecQuery(Configuration c) {
		Connection conn = DatabaseConnection.getConnection(c.getsScenario());
		final String callStatement = String.format("{call %s()}", c.getsProcedureCall());
		CallableStatement cs;
		try {
			cs = conn.prepareCall(callStatement);
			final long nanosExecutionStart = System.nanoTime();
			cs.execute();
			final long nanosExecutionEnd = System.nanoTime();
			final double timeInSecs = ((double) nanosExecutionEnd - nanosExecutionStart) / 1_000_000_000;
			printQueryExecutionMetric(c, METRIC_EXECUTION_TIME, timeInSecs);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void runExecAuthQuery(Configuration c) {
		Connection conn = DatabaseConnection.getConnection(c.getsScenario());
		final String callStatement = String.format("{call %s}", c.getsProcedureCall());
		CallableStatement cs;
		try {
			cs = conn.prepareCall(callStatement);
			if (callStatement.contains("(?)")) {
				String param = "lid".concat(c.getsScenario().substring(3));
				cs.setString(1, param);
			}
			if (callStatement.contains("(?,?)")) {
				String param = "lid".concat(c.getsScenario().substring(3));
				cs.setString(1, param);
				param = "sid1";
				cs.setString(2, param);
			}
			final long nanosExecutionStart = System.nanoTime();
			cs.execute();
			final long nanosExecutionEnd = System.nanoTime();
			final double timeInSecs = ((double) nanosExecutionEnd - nanosExecutionStart) / 1_000_000_000;
			printQueryExecutionMetric(c, METRIC_EXECUTION_AUTH_TIME, timeInSecs);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
