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

package org.vgu.sqlsi.sql.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.Attribute;
import org.vgu.sqlsi.main.Configuration;
import org.vgu.sqlsi.utils.PrintingUtils;
import org.vgu.sqlsi.utils.Template;

public class SQLSIAuthFunction extends SQLFunction {
    private List<SQLSIAuthRoleFunction> functions;

    public List<SQLSIAuthRoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<SQLSIAuthRoleFunction> functions) {
        this.functions = functions;
    }

    public SQLSIAuthFunction(String action, String resource,
        List<SQLSIAuthRoleFunction> functions) {
        super(action, resource);
        this.functions = functions;
    }

    @Override
    public String toString() {
        return String.format(Template.AUTH_FUNC, getAuthFunBody());
    }

    public SQLSIAuthFunction() {
        this.functions = new ArrayList<SQLSIAuthRoleFunction>();
    }

    public SQLSIAuthFunction(String action, String authFunName) {
        super(action, authFunName);
        this.functions = new ArrayList<SQLSIAuthRoleFunction>();
    }

    @Override
    public String getFunctionName() {
        return String.format("auth_%s_%s", this.getAction(),
            this.getResource());
    }

    private String getAuthFunBody() {
        if (functions == null || functions.isEmpty()) {
            return "RETURN 0;\r\n";
        } else {
            String authRoleFuns = "RETURN 0;";
            for (int i = functions.size() - 1; i >= 0; i--) {
                SQLSIAuthRoleFunction function = functions.get(i);
                authRoleFuns = String.format(Template.AUTH_FUN_BODY,
                    function.getRole(), function.getFunctionName(),
                    function.getAuthFunParameters(), authRoleFuns,
                    Configuration.PARAM_PREFIX);
            }
            return authRoleFuns;
        }
    }

    @Override
    public String getAuthFunParametersWithType() {
        return PrintingUtils.getParametersWithType(getParameters());
    }

//    public String printAuthRoleFuns(OCL2PSQL_2 ocl2psql)
//        throws OclParseException, ParseException, IOException {
//        String authFuns = "";
//        for (SQLSIAuthRoleFunction authFunc : functions) {
//            ocl2psql.setContextualType("self", authFunc.getTable());
//            authFuns += authFunc.printAuthRoleFun(ocl2psql);
//        }
//        return authFuns;
//    }

    @Override
    public String getAuthFunParameters() {
        return PrintingUtils.getParameters(getParameters());
    }

    public void setParameters(Attribute attribute) {
        this.setParameters(new HashMap<String, String>());
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, Configuration.SELF),
            Configuration.PARAM_TYPE);
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, Configuration.CALLER),
            Configuration.PARAM_TYPE);
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, Configuration.ROLE),
            Configuration.PARAM_TYPE);
    }

    public void setParameters(Association association) {
        this.setParameters(new HashMap<String, String>());
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, association.getLeftEnd()),
            Configuration.PARAM_TYPE);
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, association.getRightEnd()),
            Configuration.PARAM_TYPE);
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, Configuration.CALLER),
            Configuration.PARAM_TYPE);
        this.getParameters().put(String.format("%s%s",
            Configuration.PARAM_PREFIX, Configuration.ROLE),
            Configuration.PARAM_TYPE);
    }
}
