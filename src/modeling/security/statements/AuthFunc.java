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

public class AuthFunc extends SQLAuthFunction {
    private List<AuthRoleFunc> functions;

    public List<AuthRoleFunc> getFunctions() {
        return functions;
    }

    public void setFunctions(List<AuthRoleFunc> functions) {
        this.functions = functions;
    }

    public AuthFunc(String action, String resource, List<AuthRoleFunc> functions) {
        super(action, resource);
        this.functions = functions;
    }

    @Override
    public String toString() {
        return String.format(SQLTemplate.AUTH_FUNC, getAuthFunBody(), this.getDelimiter());
    }

    public AuthFunc() {
        this.functions = new ArrayList<AuthRoleFunc>();
    }

    public AuthFunc(String action, String authFunName) {
        super(action, authFunName);
        this.functions = new ArrayList<AuthRoleFunc>();
    }

    @Override
    public String getName() {
        return String.format("auth_%s_%s", this.getAction(), this.getResource());
    }

    @Override
    public CompoundStatement getStatement() {
        CompoundStatement cs = new CompoundStatement();
        cs.setStatement(this.toString());
        return cs;
    }

    public String getAuthFunBody() {
        if (functions == null || functions.isEmpty()) {
            return "RETURN 0;\r\n";
        } else {
            String authRoleFuns = "RETURN 0;";
            for (int i = functions.size() - 1; i >= 0; i--) {
                AuthRoleFunc function = functions.get(i);
                authRoleFuns = String.format(SQLTemplate.AUTH_FUN_BODY, function.getRole(), function.getName(),
                        function.getAuthFunParameters(), authRoleFuns, SQLSIConfiguration.PARAM_PREFIX);
            }
            return authRoleFuns;
        }
    }

    @Override
    public String getAuthFunParametersWithType() {
        return PrintingUtils.getParametersWithType(getVariables());
    }

    @Override
    public String getAuthFunParameters() {
        return PrintingUtils.getParameters(getVariables());
    }

    public void setParameters(Attribute attribute) {
        this.setVariables(new ArrayList<Variable>());
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE));
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE));
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
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE));
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
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE));
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
        this.getVariables().add(new SecurityVariable(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE));
    }
}
