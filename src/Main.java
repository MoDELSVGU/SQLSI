import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.vgu.sqlsi.main.SqlSI;

/**************************************************************************
Copyright 2020 Vietnamese-German-University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author: ngpbh
***************************************************************************/

public class Main {
    public static void main (String[] args) throws FileNotFoundException, IOException, ParseException, Exception {
    	final String dataModelURI = "sncs2021_evaluation/vgu_dm.json";
    	final String databaseSchemaURI = "sncs2021_evaluation/uni.sql";
    	final String policyModelURI = "sncs2021_evaluation/vgu_policy.json";
    	final String sqlAuthFuncURI = "sncs2021_evaluation/uni_sec.sql";
    	final String queryModelURI = "sncs2021_evaluation/vgu_query.json";
    	final String authProcOutputURI = "sncs2021_evaluation/uni_secProc.sql";
    	final String schemaName = "uni";
        SqlSI sqlsi = new SqlSI();
//        sqlsi.setUpDataModelFromURL(dataModelURI);
//    	sqlsi.generateDBSchema(databaseSchemaURI, schemaName);
//        sqlsi.setUpSecurityModelFromURL(policyModelURI);
//        sqlsi.generateSQLAuthFunctions(sqlAuthFuncURI);
        sqlsi.run(dataModelURI, policyModelURI, schemaName, queryModelURI, databaseSchemaURI, sqlAuthFuncURI, authProcOutputURI);
    }
}
