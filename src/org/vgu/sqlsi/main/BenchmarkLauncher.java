package main;
/*
 *     Copyright [2021] Hoang Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import java.util.Map;

import evaluation.Configuration;
import evaluation.Solution;


public class BenchmarkLauncher {
	private static final String ENV_TOOL = "TOOL";
	private static final String ENV_MODE = "MODE";
	private static final String ENV_RUN_INDEX = "RUNINDEX";
	private static final String ENV_DATAMODE_PATH = "DATAMODELPATH";
	private static final String ENV_POLICY_PATH = "POLICYPATH";
	private static final String ENV_USER = "USER";
	private static final String ENV_ROLE = "ROLE";
	private static final String ENV_PROCEDURE_CALL = "PROCEDURECALL";
	private static final String ENV_SCENARIO = "SCENARIO";
	private static final String ENV_QUERY = "QUERY";
	private static final String ENV_QUERY_EXEC = "QUERYEXEC";

	private static Configuration createConfiguration() {
		Configuration c = new Configuration();
		final Map<String, String> env = System.getenv();

//		for (String key : env.keySet()) {
//			System.out.println(key + " " + env.get(key));
//		}

		final String sProcedureCall = env.get(ENV_PROCEDURE_CALL);
		if (sProcedureCall != null) {
			c.setsProcedureCall(sProcedureCall);
		}

		final String sScenario = env.get(ENV_SCENARIO);
		if (sScenario != null) {
			c.setsScenario(sScenario);
		}

		final String sQuery = env.get(ENV_QUERY);
		if (sQuery != null) {
			c.setsQuery(sQuery);
		}

		final String sRole = env.get(ENV_ROLE);
		if (sRole != null) {
			c.setsRole(sRole);
		}

		final String sUser = env.get(ENV_USER);
		if (sUser != null) {
			c.setsUser(sUser);
		}

		final String sQueryExec = env.get(ENV_QUERY_EXEC);
		if (sQueryExec != null) {
			c.setsQueryExec(sQueryExec);
		}

		final String sMode = env.get(ENV_MODE);
		if (sMode != null) {
			c.setsMode(sMode);
		}

		final String sRunIndex = env.get(ENV_RUN_INDEX);
		if (sRunIndex != null) {
			c.setRunIndex(Integer.parseInt(sRunIndex));
		}

		final String sTool = env.get(ENV_TOOL);
		if (sTool != null) {
			c.setsTool(sTool);
		}

		final String sDataModel = env.get(ENV_DATAMODE_PATH);
		if (sDataModel != null) {
			c.setDataModelPath(sDataModel);
		}
		if (sDataModel != null) {
			final File dataModel = new File(sDataModel);
			if (dataModel.canRead()) {
				c.setDataModel(dataModel);
			} else {
				throw new IllegalArgumentException("Cannot read XMI file " + dataModel);
			}
		}

		final String sPolicy = env.get(ENV_POLICY_PATH);
		if (sPolicy != null) {
			c.setDataModelPath(sPolicy);
		}
		if (sPolicy != null) {
			final File securityModel = new File(sPolicy);
			if (securityModel.canRead()) {
				c.setSecurityModel(securityModel);
			} else {
				throw new IllegalArgumentException("Cannot read XMI file " + securityModel);
			}
		}
		return c;
	}

	public static void main(String[] args) {
		Configuration c = createConfiguration();
		if ("DMSM".equalsIgnoreCase(c.getsMode()))
			new Solution().runDMSMTransformation(c);
		else if ("SQLSI".equalsIgnoreCase(c.getsMode()))
			new Solution().runSQLSITransformation(c);
		else if ("QUERY".equalsIgnoreCase(c.getsMode()))
			new Solution().runExecQuery(c);
		else if ("AUTHQUERY".equalsIgnoreCase(c.getsMode()))
			new Solution().runExecAuthQuery(c);
		else
			new Solution().runQR(c);
	}
}
