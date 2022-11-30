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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Represent the security policy model that is specified in policyModelURI. The policyModelURI
 * points to a JSON file that contains a JSON array with each element of an array is a specific
 * rule.
 */
public class SecurityModel {
  private List<Rule> rules;

  public List<Rule> getRules() {
    return rules;
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }

  @Override
  public String toString() {
    String ruleString =
        this.rules.stream().map(rule -> rule.toString()).collect(Collectors.joining(","));
    return "SecPolicyModel [rules=" + ruleString + "]";
  }

  // public SecurityModel(List<Rule> rules) {
  //   this.rules = rules;
  // }

  public SecurityModel(JSONArray entitiesJSON) {
    List<Rule> rules = new ArrayList<Rule>();
    for (Object entityJSON : entitiesJSON) {
      rules.add(new Rule((JSONObject) entityJSON));
    }
    this.rules = rules;
  }

  public SecurityModel() {}
}
