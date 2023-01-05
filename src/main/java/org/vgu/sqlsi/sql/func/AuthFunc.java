/**************************************************************************
 * Copyright 2020 Vietnamese-German-University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
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

package org.vgu.sqlsi.sql.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.AssociationClass;
import org.vgu.dm2schema.dm.Attribute;
import org.vgu.sqlsi.main.SQLSIConfiguration;
import org.vgu.sqlsi.utils.PrintingUtils;
import org.vgu.sqlsi.utils.Template;

/**
 * + Each object instance represents an helper Auth Function which will be created for each
 * relations (tables)'s attributes (column). An auth function will be created even if there are no
 * authorization constraints explicitly defined to access that attribute, in this case the
 * authorization will be by default false.
 *
 * @author ngpbh
 */
public class AuthFunc extends SQLFunction {
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
    return String.format(Template.AUTH_FUNC, getAuthFunBody());
  }

  public AuthFunc() {
    this.functions = new ArrayList<AuthRoleFunc>();
  }

  public AuthFunc(String action, String authFunName) {
    super(action, authFunName);
    this.functions = new ArrayList<AuthRoleFunc>();
  }

  @Override
  public String getFunctionName() {
    return String.format("auth_%s_%s", this.getAction(), this.getResource());
  }

  private String getAuthFunBody() {
    if (functions == null || functions.isEmpty()) {
      return "RETURN 0;\r\n";
    } else {
      String authRoleFuns = "RETURN 0;";
      for (int i = functions.size() - 1; i >= 0; i--) {
        AuthRoleFunc function = functions.get(i);
        authRoleFuns =
            String.format(
                Template.AUTH_FUN_BODY,
                function.getRole(),
                function.getFunctionName(),
                function.getAuthFunParameters(),
                authRoleFuns);
      }
      return authRoleFuns;
    }
  }

  @Override
  public String getAuthFunParametersWithType() {
    return PrintingUtils.getParametersWithType(getParameters());
  }

  @Override
  public String getAuthFunParameters() {
    return PrintingUtils.getParameters(getParameters());
  }

  public void setParameters(Attribute attribute) {
    this.setParameters(new HashMap<String, String>());
    this.getParameters().put(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters().put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
  }

  public void setParameters(Association association) {
    this.setParameters(new HashMap<String, String>());
    this.getParameters()
        .put(
            String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getLeftEnd()),
            SQLSIConfiguration.PARAM_TYPE);
    this.getParameters()
        .put(
            String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getRightEnd()),
            SQLSIConfiguration.PARAM_TYPE);

    this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters().put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
  }

  public void setParameters(AssociationClass associationClass) {
    this.setParameters(new HashMap<String, String>());

    this.getParameters()
        .put(
            String.format(
                "%s%s", SQLSIConfiguration.PARAM_PREFIX, associationClass.getLeft().getName()),
            SQLSIConfiguration.PARAM_TYPE);

    this.getParameters()
        .put(
            String.format(
                "%s%s", SQLSIConfiguration.PARAM_PREFIX, associationClass.getRight().getName()),
            SQLSIConfiguration.PARAM_TYPE);

    this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters().put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
  }
}
