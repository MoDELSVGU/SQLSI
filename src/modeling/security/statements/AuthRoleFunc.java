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

package modeling.security.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import modeling.api.SQLSIConfiguration;
import modeling.data.entities.Association;
import modeling.data.entities.Attribute;
import modeling.data.entities.End_AssociationClass;
import modeling.data.entities.Entity;
import modeling.data.templates.SQLTemplate;
import modeling.security.mappings.SecurityVariable;
import modeling.security.utils.PrintingUtils;
import modeling.statements.CompoundStatement;
import modeling.statements.Variable;

public class AuthRoleFunc extends SQLAuthFunction {
    private String role;
    private List<String> ocl;
    private List<String> sql;

    public AuthRoleFunc(String action, String resource, String role, List<String> ocl, List<String> sql) {
        super(action, resource);
        this.role = role;
        this.ocl = ocl;
        this.sql = sql;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AuthRoleFunc() {
    }

    public AuthRoleFunc(String action, String resource, String role) {
        super(action, resource);
        this.role = role;
    }

    @Override
    public String getName() {
        return String.format("auth_%s_%s_%s", this.getAction(), this.getResource(), this.role);
    }

    public List<String> getOcl() {
        return ocl;
    }

    public void setOcl(List<String> ocl) {
        this.ocl = ocl;
    }

    public List<String> getSql() {
        return sql;
    }

    public void setSql(List<String> sql) {
        this.sql = sql;
    }

//    public String printAuthRoleFun() {
//        String sqlStatement = ocl2psql.mapOCLStringToSQLString(getAuth()).getStatement();
//        return String.format(Template.FUN_ROLE_AUTH, getFunctionName(),
//            PrintingUtils.getParameters(getParameters(), true), sqlStatement);
//    }

    public String getAuthFunRoleSQL() {
        String returnedSQL = "";
        for (String s : sql) {
            if (returnedSQL.equals("")) {
                returnedSQL = String.format("(%s)", s);
            } else {
                returnedSQL = String.format("%1$s or (%2$s)", returnedSQL, s);
            }
        }
        return returnedSQL;
    }

    @Override
    public String getAuthFunParameters() {
        return PrintingUtils.getParameters(getVariables());
    }

    @Override
    public String getAuthFunParametersWithType() {
        return PrintingUtils.getParametersWithType(getVariables());
    }

    @Override
    public String toString() {
        return String.format(SQLTemplate.AUTH_ROLE_FUNC, getAuthFunRoleSQL(), this.getDelimiter());
    }

    @Override
    public CompoundStatement getStatement() {
        CompoundStatement cs = new CompoundStatement();
        cs.setStatement(this.toString());
        return cs;
    }

    public void setParameters(Attribute attribute) {
        this.setVariables(new ArrayList<Variable>());
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE));
    }

    public void setParameters(Association association) {
        this.setVariables(new ArrayList<Variable>());
        this.getVariables()
                .add(new SecurityVariable(
                        String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getLeftEnd().getOpp()),
                        SQLSIConfiguration.PARAM_TYPE));
        this.getVariables()
                .add(new SecurityVariable(
                        String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getRightEnd().getOpp()),
                        SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE));
    }

    public void setParameters(Entity entity) {
        this.setVariables(new ArrayList<Variable>());
        for (End_AssociationClass end_asc : entity.getEnd_acs()) {
            this.getVariables()
                    .add(new SecurityVariable(String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, end_asc.getName()),
                            SQLSIConfiguration.PARAM_TYPE));
        }
//        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE));
    }

    public void setParameters(End_AssociationClass end_asc) {
        this.setVariables(new ArrayList<Variable>());
        this.getVariables()
                .add(new SecurityVariable(
                        String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, end_asc.getCurrentClass()),
                        SQLSIConfiguration.PARAM_TYPE));
        this.getVariables()
                .add(new SecurityVariable(
                        String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, end_asc.getName()),
                        SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE));
    }

}
