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

package org.vgu.sqlsi.main;

import org.vgu.sqlsi.sec.SecurityMode;

public class Configuration {
    // relative file URL of DataModel
    public static final String dataModelInputURI = "jot2020_demo/vgu_dm.json";
    // relative file URL of SecurityModel
    public static final String policyModelInputURI = "jot2020_demo/vgu_policy.json";
    // relative file URL of Query for enforcing security policy
    public static final String queryToBeEnforcedInputURI = "jot2020_demo/vgu_query.json";
    // relative file URL of SQL Schema auto-generate
    public static final String sqlSchemaOutputURI = "jot2020_demo/vgu_db.sql";
    // schema name
    public static final String schemaName = "sqlsi2020";
    // relative file URL of SQL auth function generate
    public static final String sqlAuthFunctionOutputURI = "jot2020_demo/vgu_fun.sql";
    // relative file URL of SQL auth stored procedure generate
    public static final String sqlStoredProcedureOutputURI = "jot2020_demo/vgu_sp.sql";
    // Security Mode: Currently accepting NON_TRUMAN
    public static final SecurityMode securityMode = SecurityMode.NON_TRUMAN;
    // Security Prefix Parameter: The prefix of auth function/sec stored
    // procedure parameters
    public static final String PARAM_PREFIX = "k";
    // Security Parameter Type: The type of auth function/sec stored
    // procedure parameters
    public static final String PARAM_TYPE = "varchar(250)";
    // Security variables
    public static final String SELF = "self";
    public static final String CALLER = "caller";
    public static final String TARGET = "target";
    public static final String VALUE = "value";
    public static final String ROLE = "role";
}
