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

package org.vgu.sqlsi.sec.model;

import org.json.simple.JSONObject;

/**
 * Represent an atribute resource in the policy model file. Which will be later converted to the
 * class AtributeUhitRule.
 */
public class AttributeResource extends Resource {
  private String entity;
  private String attribute;

  public AttributeResource(JSONObject resourceJSON) {
    this.entity = (String) resourceJSON.get("entity");
    this.attribute = (String) resourceJSON.get("attribute");
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  @Override
  public String toString() {
    return "AttributeResource [entity=" + entity + ", attribute=" + attribute + "]";
  }
}