package org.vgu.sqlsi.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

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

public class Main {
  public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, Exception {
    // input
    final String dataModelURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/vgu_dm.json";
    final String policyModelURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/secVGU#C3.json";
    final String queryModelURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/vgu_query.json";

    // ouput
    final String databaseSchemaURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/result/db.sql";
    final String sqlAuthFuncURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/result/secVGU#X3.sql";
    final String authProcOutputURI = "/home/pj/SQLSI_v4/sncs2021_evaluation/result/secProc.sql";
    // final String dataModelURI = "fdse2020_demo/vgu_dm.json";
    // final String databaseSchemaURI = "fdse2020_demo/uni.sql";
    // final String policyModelURI = "fdse2020_demo/vguA.json";
    // final String sqlAuthFuncURI = "fdse2020_demo/uni_sec.sql";
    // final String queryModelURI = "fdse2020_demo/vgu_query.json";
    // final String authProcOutputURI = "fdse2020_demo/uni_secProc.sql";
    final String schemaName = "VGU";
    SqlSI sqlsi = new SqlSI();

    // sqlsi.setUpDataModelFromURL(dataModelURI);
    // sqlsi.generateDBSchema(databaseSchemaURI, schemaName);
    // sqlsi.setUpSecurityModelFromURL(policyModelURI);

    // sqlsi.generateSQLAuthFunctions(sqlAuthFuncURI);

    sqlsi.setDataModel(dataModelURI);
    sqlsi.setQueryModel(queryModelURI);
    sqlsi.setSecurityModel(policyModelURI);

    sqlsi.run(queryModelURI, databaseSchemaURI, sqlAuthFuncURI, authProcOutputURI);

  }
}
