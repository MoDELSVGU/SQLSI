/**************************************************************************
Copyright 2019 Vietnamese-German-University
Copyright 2023 ETH Zurich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author: hoangnguyen (hoang.nguyen@inf.ethz.ch)
***************************************************************************/

package modeling.security.utils;

import java.util.HashMap;

import modeling.api.SQLSIConfiguration;
import modeling.data.templates.SQLTemplate;
import modeling.security.statements.AuthFunc;
import modeling.security.statements.AuthRoleFunc;
import modeling.security.statements.SQLSIStoredProcedure;
import modeling.statements.CompoundStatement;
import modeling.statements.Function;
import modeling.statements.create.CreateFunction;
import modeling.statements.create.CreateProcedure;
import modeling.statements.drop.DropFunction;
import modeling.statements.drop.DropProcedure;

public class PrintingUtils {
	/*
	 * Output example: kcaller VARCHAR(100), kself VARCHAR(100)
	 */
	public static String getParametersWithType(HashMap<String, String> hashMap) {
		String s = "";
		int countPars = hashMap.size();
		for (String key : hashMap.keySet()) {
			s = s.concat(String.format("%s %s", key, hashMap.get(key)));
			countPars = countPars - 1;
			if (countPars > 0) {
				s = String.format("%s, ", s);
			}
		}
		return s;
	}

	/*
	 * Output example: kcaller, kself
	 */
	public static String getParameters(HashMap<String, String> hashMap) {
		String s = "";
		int countPars = hashMap.size();
		for (String key : hashMap.keySet()) {
			s = s.concat(String.format("%s", key));
			countPars = countPars - 1;
			if (countPars > 0) {
				s = String.format("%s, ", s);
			}
		}
		return s;
	}

	public static String printProc(SQLSIStoredProcedure storedProcedure) {
		DropProcedure dropProcedure = new DropProcedure();
		dropProcedure.setIfExists(true);
		dropProcedure.setStoredProcedure(storedProcedure);
		CreateProcedure createProcedure = new CreateProcedure();
		createProcedure.setStoredProcedure(storedProcedure);
		createProcedure.setDelimiter(SQLSIConfiguration.DELIMITER);
		return String.format("%s;\r\n%s\r\n", dropProcedure.toString(), createProcedure.toString());
	}

	public static String printAuthFunc(AuthFunc function) {
		String output = "";
		DropFunction dropFunction = new DropFunction();
		dropFunction.setIfExists(true);
		dropFunction.setFunction(function);
		CreateFunction createFunction = new CreateFunction();
		createFunction.setDelimiter(SQLSIConfiguration.DELIMITER);
		createFunction.setFunction(function);
		output = output.concat(String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
		output = output.concat(printAuthRoleFunc(function));
		return output;
	}

	public static String printAuthRoleFunc(AuthFunc function) {
		String output = "";
		if (function.getFunctions() != null && !function.getFunctions().isEmpty()) {
			for (AuthRoleFunc roleFunc : function.getFunctions()) {
				DropFunction dropFunction = new DropFunction();
				dropFunction.setIfExists(true);
				dropFunction.setFunction(roleFunc);
				CreateFunction createFunction = new CreateFunction();
				createFunction.setDelimiter(SQLSIConfiguration.DELIMITER);
				createFunction.setFunction(roleFunc);
				output = output
						.concat(String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
			}
		}
		return output;
	}

	public static String printThrowErrorFunc() throws Exception {
		String output = "";
		Function throwError = new Function();
		throwError.setName("throw_error");
		CompoundStatement cs = new CompoundStatement();
		cs.setStatement(String.format(SQLTemplate.THROW_ERROR_BODY, SQLSIConfiguration.DELIMITER));
		throwError.setStatement(cs);
		DropFunction dropFunction = new DropFunction();
		dropFunction.setIfExists(true);
		dropFunction.setFunction(throwError);
		CreateFunction createFunction = new CreateFunction();
		createFunction.setDelimiter(SQLSIConfiguration.DELIMITER);
		createFunction.setFunction(throwError);
		output = output.concat(String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
		return output;
	}
}
