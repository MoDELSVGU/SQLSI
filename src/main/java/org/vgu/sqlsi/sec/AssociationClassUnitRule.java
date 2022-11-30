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
 * @author: HuMiTriet
 ***************************************************************************/
package org.vgu.sqlsi.sec;

import java.util.List;

public class AssociationClassUnitRule extends SecUnitRule {
  private String associationClass;

  public AssociationClassUnitRule(
      String action, String role, List<AuthorizationConstraint> auths, String associationClass) {
    super(action, role, auths);
    this.associationClass = associationClass;
  }

  public AssociationClassUnitRule(String action, String role, List<AuthorizationConstraint> auths) {
    super(action, role, auths);
  }

  public String getAssociationClass() {
    return associationClass;
  }

  public void setAssociationClass(String associationClass) {
    this.associationClass = associationClass;
  }
}
