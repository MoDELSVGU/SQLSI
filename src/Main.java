import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.vgu.sqlsi.main.Configuration;
import org.vgu.sqlsi.main.SqlSI;

/**************************************************************************
 * Copyright 2020 Vietnamese-German-University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author: ngpbh
 ***************************************************************************/

public class Main {
    public static class InDirect {
        public static void main(String[] args)
            throws FileNotFoundException, IOException, ParseException, Exception {
            SqlSI sqlSI = new SqlSI();
            
            sqlSI.setUpDataModelFromURL(Configuration.dataModelInputURI);
            sqlSI.setUpSecurityModelFromURL(Configuration.policyModelInputURI);
            sqlSI.generateSQLSecureQuery("SELECT email FROM Lecturer", Configuration.sqlStoredProcedureOutputURI);
        }
    }
    
    public static class Direct {
        public static void main(String[] args)
            throws FileNotFoundException, IOException, ParseException, Exception {
            SqlSI sqlSI = new SqlSI();
            sqlSI.run(Configuration.dataModelInputURI,
                Configuration.policyModelInputURI, Configuration.schemaName,
                Configuration.queryToBeEnforcedInputURI,
                Configuration.sqlSchemaOutputURI,
                Configuration.sqlAuthFunctionOutputURI,
                Configuration.sqlStoredProcedureOutputURI);
        }
    }
}


