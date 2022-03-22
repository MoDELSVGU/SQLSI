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

package org.vgu.sqlsi.sec;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SecPolicyModel {
    private List<SecRuleModel> rules;
    
    public List<SecRuleModel> getRules() {
        return rules;
    }

    public void setRules(List<SecRuleModel> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "SecPolicyModel [rules=" + rules + "]";
    }

    public SecPolicyModel(List<SecRuleModel> rules) {
        this.rules = rules;
    }
    
    public SecPolicyModel(JSONArray entitiesJSON) {
        List<SecRuleModel> rules = new ArrayList<SecRuleModel>();
        for(Object entityJSON : entitiesJSON) {
            rules.add(new SecRuleModel((JSONObject) entityJSON));
        }
        this.rules = rules;
    }

    public SecPolicyModel() {
    }
}
