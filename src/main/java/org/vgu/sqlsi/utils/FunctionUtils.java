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

package org.vgu.sqlsi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.Attribute;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.dm2schema.dm.Entity;
import org.vgu.sqlsi.sec.AssociationUnitRule;
import org.vgu.sqlsi.sec.AttributeUnitRule;
import org.vgu.sqlsi.sec.AuthorizationConstraint;
import org.vgu.sqlsi.sec.SecUnitRule;
import org.vgu.sqlsi.sec.model.SecurityModel;
import org.vgu.sqlsi.sql.func.AuthFunc;
import org.vgu.sqlsi.sql.func.AuthRoleFunc;

public class FunctionUtils {

    public static List<AuthFunc> printAuthFun(DataModel dataModel,
        SecurityModel securityModel) throws Exception {
        List<SecUnitRule> rules = RuleUtils.getAllUnitRules(securityModel);
        List<AuthFunc> functions = new ArrayList<AuthFunc>();
        for (Entity entity : dataModel.getEntities().values()) {
            functions.addAll(getAuthFun(entity, rules));
        }
        for (Association association : dataModel.getAssociations()) {
            functions.addAll(getAuthFun(association, rules));
        }
        return functions;
    }

    private static List<AuthFunc> getAuthFun(Entity entity,
        List<SecUnitRule> rules) {
        List<AuthFunc> functions = new ArrayList<AuthFunc>();
        for (Attribute attribute : entity.getAttributes()) {
            functions
                .add(getAuthFunFromAttribute("READ", entity, attribute, rules));
        }
        return functions;
    }

    private static AuthFunc getAuthFunFromAttribute(String action,
        Entity entity, Attribute attribute, List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = filterAndIndexRules(
            action, entity, attribute, rules);
        String authFunName = String.format("%1$s_%2$s", entity.getName(),
            attribute.getName());
        AuthFunc sqlAuthFunction = new AuthFunc(action,
            authFunName);
        sqlAuthFunction.setParameters(attribute);
        if (!indexRules.isEmpty()) {
            for (String role : indexRules.keySet()) {
                AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(
                    action, authFunName, role);
                sqlAuthRoleFunction.setParameters(attribute);
                List<SecUnitRule> ruleRoleBased = indexRules.get(role);
                sqlAuthRoleFunction
                    .setOcl(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                        .collect(Collectors.toList()));
                sqlAuthRoleFunction
                    .setSql(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                        .collect(Collectors.toList()));
                sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
            }
        }
        return sqlAuthFunction;
    }

    public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
        String action, Entity entity, Attribute attribute,
        List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = new HashMap<String, List<SecUnitRule>>();
        if (rules != null) {
            for (SecUnitRule rule : rules) {
                if (rule instanceof AttributeUnitRule) {
                    AttributeUnitRule attRule = (AttributeUnitRule) rule;
                    if (attRule.getEntity().equals(entity.getName())
                        && attRule.getAttribute().equals(attribute.getName())
                        && attRule.getAction().equals(action)) {
                        if (indexRules.containsKey(rule.getRole())) {
                            indexRules.get(rule.getRole()).add(rule);
                        } else {
                            indexRules.put(rule.getRole(),
                                new ArrayList<SecUnitRule>());
                            indexRules.get(rule.getRole()).add(rule);
                        }
                    }
                }
            }
        }
        return indexRules;
    }

    private static Collection<? extends AuthFunc> getAuthFun(
        Association association, List<SecUnitRule> rules) {
        List<AuthFunc> functions = new ArrayList<AuthFunc>();
        functions.add(getAuthFunFromAssociation("READ", association, rules));
        return functions;
    }

    private static AuthFunc getAuthFunFromAssociation(String action,
        Association association, List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = filterAndIndexRules(
            action, association, rules);
        String authFunName = String.format("%s", association.getName());
        AuthFunc sqlAuthFunction = new AuthFunc(action,
            authFunName);
        sqlAuthFunction.setParameters(association);
        if (!indexRules.isEmpty()) {
            for (String role : indexRules.keySet()) {
                AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(
                    action, authFunName, role);
                sqlAuthRoleFunction.setParameters(association);
                List<SecUnitRule> ruleRoleBased = indexRules.get(role);
                sqlAuthRoleFunction
                    .setOcl(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                        .collect(Collectors.toList()));
                sqlAuthRoleFunction
                    .setSql(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                        .collect(Collectors.toList()));
                sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
            }
        }
        return sqlAuthFunction;
    }

    public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
        String action, Association association, List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = new HashMap<String, List<SecUnitRule>>();
        if (rules != null) {
            for (SecUnitRule rule : rules) {
                if (rule instanceof AssociationUnitRule) {
                    AssociationUnitRule attRule = (AssociationUnitRule) rule;
                    if (attRule.getAssociation().equals(association.getName())
                        && attRule.getAction().equals(action)) {
                        if (indexRules.containsKey(rule.getRole())) {
                            indexRules.get(rule.getRole()).add(rule);
                        } else {
                            indexRules.put(rule.getRole(),
                                new ArrayList<SecUnitRule>());
                            indexRules.get(rule.getRole()).add(rule);
                        }
                    }
                }
            }
        }
        return indexRules;
    }

}
