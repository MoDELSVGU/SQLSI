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

public class SecRuleModel {
    private List<SecRoleModel> roles;
    private List<SecActionModel> actions;
    private List<SecResourceModel> resources;
    private List<SecAuthModel> auth;

    public SecRuleModel(JSONObject authEntityJSON) {
        roles = convertRoles((JSONArray) authEntityJSON.get("roles"));
        actions = convertActions((JSONArray) authEntityJSON.get("actions"));
        resources = convertResources(
            (JSONArray) authEntityJSON.get("resources"));
        auth = convertAuth((JSONArray) authEntityJSON.get("auth"));
    }

    private List<SecAuthModel> convertAuth(JSONArray authsJSON) {
        List<SecAuthModel> auths = new ArrayList<SecAuthModel>();
        for (Object authJSON : authsJSON) {
            auths.add(new SecAuthModel(authJSON));
        }
        return auths;
    }

    private List<SecResourceModel> convertResources(JSONArray resourcesJSON) {
        List<SecResourceModel> resources = new ArrayList<SecResourceModel>();
        for (Object resourceJSON : resourcesJSON) {
            resources.add(SecResourceFactory.create(resourceJSON));
        }
        return resources;
    }

    private List<SecActionModel> convertActions(JSONArray actionsJSON) {
        List<SecActionModel> actions = new ArrayList<SecActionModel>();
        for (Object actionJSON : actionsJSON) {
            actions.add(SecActionModel.getAction((String) actionJSON));
        }
        return actions;
    }

    private List<SecRoleModel> convertRoles(JSONArray rolesJSON) {
        List<SecRoleModel> roles = new ArrayList<SecRoleModel>();
        for (Object roleJSON : rolesJSON) {
            roles.add(new SecRoleModel((String) roleJSON));
        }
        return roles;
    }

    public List<SecRoleModel> getRoles() {
        return roles;
    }

    public void setRoles(List<SecRoleModel> roles) {
        this.roles = roles;
    }

    public List<SecActionModel> getActions() {
        return actions;
    }

    public void setActions(List<SecActionModel> actions) {
        this.actions = actions;
    }

    public List<SecResourceModel> getResources() {
        return resources;
    }

    public void setResources(List<SecResourceModel> resources) {
        this.resources = resources;
    }

    public List<SecAuthModel> getAuth() {
        return auth;
    }

    public void setAuth(List<SecAuthModel> auth) {
        this.auth = auth;
    }

    public SecRuleModel(List<SecRoleModel> roles, List<SecActionModel> actions,
        List<SecResourceModel> resources, List<SecAuthModel> auth) {
        super();
        this.roles = roles;
        this.actions = actions;
        this.resources = resources;
        this.auth = auth;
    }

    public SecRuleModel() {
    }
}


