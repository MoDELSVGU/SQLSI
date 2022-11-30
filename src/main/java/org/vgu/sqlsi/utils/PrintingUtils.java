/**************************************************************************
 * Copyright 2020 Vietnamese-German-University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author: ngpbh
 ***************************************************************************/

package org.vgu.sqlsi.utils;

import java.util.HashMap;
import org.vgu.sqlsi.sql.func.AuthFunc;
import org.vgu.sqlsi.sql.func.AuthRoleFunc;
import org.vgu.sqlsi.sql.func.CreateFunction;
import org.vgu.sqlsi.sql.func.DropFunction;
import org.vgu.sqlsi.sql.func.SQLNormalFunction;
import org.vgu.sqlsi.sql.proc.CreateProcedure;
import org.vgu.sqlsi.sql.proc.DropProcedure;
import org.vgu.sqlsi.sql.proc.SQLSIStoredProcedure;

/**
 * Provides all of the java String statement that will be write to the file SQL file that will
 * includes all of the Authorization functions, helpers functions that will be used by the store
 * procdure.
 */
public class PrintingUtils {
  /** Output example: kcaller VARCHAR(100), kself VARCHAR(100). */
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
    return String.format("%s;\r\n%s\r\n", dropProcedure.toString(), createProcedure.toString());
  }

  public static String printAuthFunc(AuthFunc function) {
    DropFunction dropFunction = new DropFunction();
    dropFunction.setIfExists(true);
    dropFunction.setFunction(function);
    CreateFunction createFunction = new CreateFunction();
    createFunction.setFunction(function);
    String output = "";
    output =
        output.concat(
            String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
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
        createFunction.setFunction(roleFunc);
        output =
            output.concat(
                String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
      }
    }
    return output;
  }

  /**
   * provide the SQL statement that will create the SQL function which will be used to throw errors
   * in cases that the resource is not authorized.
   */
  public static String printThrowErrorFunc() throws Exception {

    // actual function body
    SQLNormalFunction throwError = new SQLNormalFunction();
    throwError.setName("throw_error");
    throwError.setBody(Template.THROW_ERROR_BODY);

    // drop the function if exist
    DropFunction dropFunction = new DropFunction();
    dropFunction.setIfExists(true);
    dropFunction.setFunction(throwError);

    CreateFunction createFunction = new CreateFunction();
    createFunction.setFunction(throwError);
    String output = "";
    output =
        output.concat(
            String.format("%s;\r\n%s\r\n", dropFunction.toString(), createFunction.toString()));
    return output;
  }
}
