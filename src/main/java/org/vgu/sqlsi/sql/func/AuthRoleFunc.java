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

package org.vgu.sqlsi.sql.func;

import java.util.HashMap;
import java.util.List;
import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.AssociationClass;
import org.vgu.dm2schema.dm.Attribute;
import org.vgu.sqlsi.main.SQLSIConfiguration;
import org.vgu.sqlsi.utils.PrintingUtils;
import org.vgu.sqlsi.utils.Template;

/** A Class that contain all of the authorization constrain attributed to that role. */
public class AuthRoleFunc extends SQLFunction {
  private String role;
  private List<String> ocl;
  private List<String> sql;

  public AuthRoleFunc(
      String action, String resource, String role, List<String> ocl, List<String> sql) {
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

  public AuthRoleFunc() {}

  public AuthRoleFunc(String action, String resource, String role) {
    super(action, resource);
    this.role = role;
  }

  @Override
  public String getFunctionName() {
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

  // public String printAuthRoleFun() {
  // String sqlStatement =
  // ocl2psql.mapOCLStringToSQLString(getAuth()).getStatement();
  // return String.format(Template.FUN_ROLE_AUTH, getFunctionName(),
  // PrintingUtils.getParameters(getParameters(), true), sqlStatement);
  // }

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
    return PrintingUtils.getParameters(getParameters());
  }

  @Override
  public String getAuthFunParametersWithType() {
    return PrintingUtils.getParametersWithType(getParameters());
  }

  /**
   * this method is important for the generation of th final SQL statement that will be a part of
   * the bigger helper Authorization function.
   */
  @Override
  public String toString() {
    return String.format(Template.AUTH_ROLE_FUNC, getAuthFunRoleSQL());
  }

  public void setParameters(Attribute attribute) {
    this.setParameters(new HashMap<String, String>());
    this.getParameters().put(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
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
  }

  public void setParameters(AssociationClass associationclass) {
    this.setParameters(new HashMap<String, String>());
    // this.getParameters().put(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE);
    // this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
    this.getParameters()
        .put(
            String.format(
                "%s%s", SQLSIConfiguration.PARAM_PREFIX, associationclass.getLeft().getName()),
            SQLSIConfiguration.PARAM_TYPE);

    this.getParameters()
        .put(
            String.format(
                "%s%s", SQLSIConfiguration.PARAM_PREFIX, associationclass.getRight().getName()),
            SQLSIConfiguration.PARAM_TYPE);

    this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
  }
}
