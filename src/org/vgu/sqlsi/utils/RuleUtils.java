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
import java.util.List;
import java.util.stream.Collectors;

import org.vgu.sqlsi.sec.AssociationUnitRule;
import org.vgu.sqlsi.sec.AttributeUnitRule;
import org.vgu.sqlsi.sec.Auth;
import org.vgu.sqlsi.sec.SecActionModel;
import org.vgu.sqlsi.sec.SecPolicyModel;
import org.vgu.sqlsi.sec.SecResourceAssociationModel;
import org.vgu.sqlsi.sec.SecResourceAttributeModel;
import org.vgu.sqlsi.sec.SecRoleModel;
import org.vgu.sqlsi.sec.SecRuleModel;
import org.vgu.sqlsi.sec.SecUnitRule;

public class RuleUtils {

    public static List<SecUnitRule> getAllUnitRules(SecPolicyModel securityModel) {
        List<SecUnitRule> unitRules = new ArrayList<SecUnitRule>();
        unitRules.addAll(getUnitRulesFromEntity(securityModel));
        unitRules.addAll(getUnitRulesFromAssociaiton(securityModel));
        return unitRules;
    }

    private static Collection<? extends SecUnitRule> getUnitRulesFromAssociaiton(
        SecPolicyModel securityModel) {
        List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
        rules.addAll(
            getUnitRulesFromAssociaiton(SecActionModel.CREATE, securityModel));
        rules.addAll(
            getUnitRulesFromAssociaiton(SecActionModel.READ, securityModel));
        rules.addAll(
            getUnitRulesFromAssociaiton(SecActionModel.UPDATE, securityModel));
        rules.addAll(
            getUnitRulesFromAssociaiton(SecActionModel.DELETE, securityModel));
        return rules;
    }

    private static Collection<? extends SecUnitRule> getUnitRulesFromAssociaiton(
        SecActionModel action, SecPolicyModel securityModel) {
        List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
        List<SecRuleModel> secReadRuleModels = securityModel.getRules().stream()
            .filter(
                r -> r.getActions() != null && r.getActions().contains(action))
            .collect(Collectors.toList());
        for (SecRuleModel ruleModel : secReadRuleModels) {
            List<SecRoleModel> roles = ruleModel.getRoles();
            List<SecResourceAssociationModel> resources = ruleModel
                .getResources().stream()
                .filter(res -> res instanceof SecResourceAssociationModel)
                .map(SecResourceAssociationModel.class::cast)
                .collect(Collectors.toList());
            List<Auth> auths = ruleModel.getAuth().stream()
                .map(au -> new Auth(au)).collect(Collectors.toList());
            for (SecRoleModel role : roles) {
                for (SecResourceAssociationModel resource : resources) {
                    rules
                        .add(new AssociationUnitRule(SecActionModel.READ.name(),
                            role.getRole(), auths, resource.getAssociation()));
                }
            }
        }
        return rules;
    }

    private static Collection<? extends SecUnitRule> getUnitRulesFromEntity(
        SecPolicyModel securityModel) {
        List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
        rules.addAll(
            getUnitRulesFromEntity(SecActionModel.CREATE, securityModel));
        rules
            .addAll(getUnitRulesFromEntity(SecActionModel.READ, securityModel));
        rules.addAll(
            getUnitRulesFromEntity(SecActionModel.UPDATE, securityModel));
        rules.addAll(
            getUnitRulesFromEntity(SecActionModel.DELETE, securityModel));
        return rules;
    }

    private static Collection<? extends SecUnitRule> getUnitRulesFromEntity(
        SecActionModel action, SecPolicyModel securityModel) {
        List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
        List<SecRuleModel> secReadRuleModels = securityModel.getRules().stream()
            .filter(
                r -> r.getActions() != null && r.getActions().contains(action))
            .collect(Collectors.toList());
        for (SecRuleModel ruleModel : secReadRuleModels) {
            List<SecRoleModel> roles = ruleModel.getRoles();
            List<SecResourceAttributeModel> resources = ruleModel.getResources()
                .stream()
                .filter(res -> res instanceof SecResourceAttributeModel)
                .map(SecResourceAttributeModel.class::cast)
                .collect(Collectors.toList());
            List<Auth> auths = ruleModel.getAuth().stream()
                .map(au -> new Auth(au)).collect(Collectors.toList());
            for (SecRoleModel role : roles) {
                for (SecResourceAttributeModel resource : resources) {
                    rules.add(new AttributeUnitRule(SecActionModel.READ.name(),
                        role.getRole(), auths, resource.getEntity(),
                        resource.getAttribute()));
                }
            }
        }
        return rules;
    }

}
