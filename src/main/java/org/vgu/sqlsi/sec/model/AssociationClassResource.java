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

package org.vgu.sqlsi.sec.model;

import org.json.simple.JSONObject;

public class AssociationClassResource extends Resource {
  private String associationClass;

  // private Set<AttributeResource> attributes = new HashSet<>();

  public AssociationClassResource(JSONObject resourceJson) {
    this.associationClass = (String) resourceJson.get("association-class");
    // check if there is an attribute in resourceJson if yes then add to attributes
    // if (resourceJson.containsKey("attribute")) {
    //   this.attributes.add(
    //       new AttributeResource(this.associationClass, (String) resourceJson.get("attribute")));
    // }
  }

  public String getAssociationClass() {
    return associationClass;
  }

  public void setAssociationClass(String associationClass) {
    this.associationClass = associationClass;
  }

  @Override
  public String toString() {
    // String attributeString =
    //     this.attributes.stream().map(AttributeResource::toString).collect(Collectors.joining(",
    // "));
    return "AssociationClassResource [associationClass="
        + associationClass
        // + ", attributes="
        // + attributeString
        + "]";
  }
}
