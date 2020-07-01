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
import org.vgu.sqlsi.sec.Auth;
import org.vgu.sqlsi.sec.SecPolicyModel;
import org.vgu.sqlsi.sec.SecUnitRule;
import org.vgu.sqlsi.sec.SecurityMode;
import org.vgu.sqlsi.sql.func.SQLSIAuthFunction;
import org.vgu.sqlsi.sql.func.SQLSIAuthRoleFunction;

public class FunctionUtils {

    public static List<SQLSIAuthFunction> printAuthFun(DataModel dataModel,
        SecPolicyModel securityModel, SecurityMode secMode) throws Exception {
        // TEMPORARY CATCH
        if (secMode != SecurityMode.NON_TRUMAN) {
            throw new Exception("Unsuppoted SecurityMode: TRUMAN");
        }
        List<SecUnitRule> rules = RuleUtils.getAllUnitRules(securityModel);
        List<SQLSIAuthFunction> functions = new ArrayList<SQLSIAuthFunction>();
        for (Entity entity : dataModel.getEntities().values()) {
            functions.addAll(getAuthFun(entity, rules));
        }
        for (Association association : dataModel.getAssociations()) {
            functions.addAll(getAuthFun(association, rules));
        }
        return functions;
    }

    private static List<SQLSIAuthFunction> getAuthFun(Entity entity,
        List<SecUnitRule> rules) {
        List<SQLSIAuthFunction> functions = new ArrayList<SQLSIAuthFunction>();
        for (Attribute attribute : entity.getAttributes()) {
            functions
                .add(getAuthFunFromAttribute("READ", entity, attribute, rules));
        }
        return functions;
    }

    private static SQLSIAuthFunction getAuthFunFromAttribute(String action,
        Entity entity, Attribute attribute, List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = filterAndIndexRules(
            action, entity, attribute, rules);
        String authFunName = String.format("%1$s_%2$s", entity.getName(),
            attribute.getName());
        SQLSIAuthFunction sqlAuthFunction = new SQLSIAuthFunction(action,
            authFunName);
        sqlAuthFunction.setParameters(attribute);
        if (!indexRules.isEmpty()) {
            for (String role : indexRules.keySet()) {
                SQLSIAuthRoleFunction sqlAuthRoleFunction = new SQLSIAuthRoleFunction(
                    action, authFunName, role);
                sqlAuthRoleFunction.setParameters(attribute);
                List<SecUnitRule> ruleRoleBased = indexRules.get(role);
                sqlAuthRoleFunction
                    .setOcl(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(Auth::getOcl))
                        .collect(Collectors.toList()));
                sqlAuthRoleFunction
                    .setSql(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(Auth::getSql))
                        .collect(Collectors.toList()));
                sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
            }
        }
        return sqlAuthFunction;
    }

    private static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
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

    private static Collection<? extends SQLSIAuthFunction> getAuthFun(
        Association association, List<SecUnitRule> rules) {
        List<SQLSIAuthFunction> functions = new ArrayList<SQLSIAuthFunction>();
        functions.add(getAuthFunFromAssociation("READ", association, rules));
        return functions;
    }

    private static SQLSIAuthFunction getAuthFunFromAssociation(String action,
        Association association, List<SecUnitRule> rules) {
        HashMap<String, List<SecUnitRule>> indexRules = filterAndIndexRules(
            action, association, rules);
        String authFunName = String.format("%s", association.getName());
        SQLSIAuthFunction sqlAuthFunction = new SQLSIAuthFunction(action,
            authFunName);
        sqlAuthFunction.setParameters(association);
        if (!indexRules.isEmpty()) {
            for (String role : indexRules.keySet()) {
                SQLSIAuthRoleFunction sqlAuthRoleFunction = new SQLSIAuthRoleFunction(
                    action, authFunName, role);
                sqlAuthRoleFunction.setParameters(association);
                List<SecUnitRule> ruleRoleBased = indexRules.get(role);
                sqlAuthRoleFunction
                    .setOcl(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(Auth::getOcl))
                        .collect(Collectors.toList()));
                sqlAuthRoleFunction
                    .setSql(ruleRoleBased.stream().map(SecUnitRule::getAuths)
                        .flatMap(auths -> auths.stream().map(Auth::getSql))
                        .collect(Collectors.toList()));
                sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
            }
        }
        return sqlAuthFunction;
    }

    private static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
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
