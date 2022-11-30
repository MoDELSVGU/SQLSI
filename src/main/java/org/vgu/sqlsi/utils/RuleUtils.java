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

package org.vgu.sqlsi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.vgu.sqlsi.sec.AssociationClassUnitRule;
import org.vgu.sqlsi.sec.AssociationUnitRule;
import org.vgu.sqlsi.sec.AttributeUnitRule;
import org.vgu.sqlsi.sec.AuthorizationConstraint;
import org.vgu.sqlsi.sec.SecUnitRule;
import org.vgu.sqlsi.sec.model.Action;
import org.vgu.sqlsi.sec.model.AssociationClassResource;
import org.vgu.sqlsi.sec.model.AssociationResource;
import org.vgu.sqlsi.sec.model.AttributeResource;
import org.vgu.sqlsi.sec.model.Role;
import org.vgu.sqlsi.sec.model.Rule;
import org.vgu.sqlsi.sec.model.SecurityModel;

public class RuleUtils {

  public static List<SecUnitRule> getAllUnitRules(SecurityModel securityModel) {
    List<SecUnitRule> unitRules = new ArrayList<SecUnitRule>();
    unitRules.addAll(getUnitRulesFromEntity(securityModel));
    unitRules.addAll(getUnitRulesFromAssociaiton(securityModel));
    unitRules.addAll(getUnitrulesFromAssociationClass(securityModel));
    return unitRules;
  }

  private static Collection<? extends SecUnitRule> getUnitRulesFromAssociaiton(
      SecurityModel securityModel) {
    List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
    rules.addAll(getUnitRulesFromAssociaiton(Action.CREATE, securityModel));
    rules.addAll(getUnitRulesFromAssociaiton(Action.READ, securityModel));
    rules.addAll(getUnitRulesFromAssociaiton(Action.UPDATE, securityModel));
    rules.addAll(getUnitRulesFromAssociaiton(Action.DELETE, securityModel));
    return rules;
  }

  private static Collection<? extends SecUnitRule> getUnitRulesFromAssociaiton(
      Action action, SecurityModel securityModel) {
    List<SecUnitRule> rules = new ArrayList<>();
    List<Rule> secReadRuleModels =
        securityModel.getRules().stream()
            .filter(r -> r.getActions() != null && r.getActions().contains(action))
            .collect(Collectors.toList());
    for (Rule ruleModel : secReadRuleModels) {

      List<Role> roles = ruleModel.getRoles();

      List<AssociationResource> resources =
          ruleModel.getResources().stream()
              .filter(res -> res instanceof AssociationResource)
              .map(AssociationResource.class::cast)
              .collect(Collectors.toList());

      List<AuthorizationConstraint> auths =
          ruleModel.getAuth().stream()
              .map(au -> new AuthorizationConstraint(au))
              .collect(Collectors.toList());

      for (Role role : roles) {
        for (AssociationResource resource : resources) {
          rules.add(
              new AssociationUnitRule(
                  Action.READ.name(), role.getRole(), auths, resource.getAssociation()));
        }
      }
    }
    return rules;
  }

  private static Collection<? extends SecUnitRule> getUnitrulesFromAssociationClass(
      SecurityModel securityModel) {
    List<SecUnitRule> rules = new ArrayList<>();
    rules.addAll(getUnitRulesFromAssociationClass(Action.CREATE, securityModel));
    rules.addAll(getUnitRulesFromAssociationClass(Action.READ, securityModel));
    rules.addAll(getUnitRulesFromAssociationClass(Action.UPDATE, securityModel));
    rules.addAll(getUnitRulesFromAssociationClass(Action.DELETE, securityModel));
    return rules;
  }

  private static Collection<? extends SecUnitRule> getUnitRulesFromAssociationClass(
      Action action, SecurityModel securityModel) {
    List<SecUnitRule> rules = new ArrayList<SecUnitRule>();
    List<Rule> secReadruleModels =
        securityModel.getRules().stream()
            .filter(r -> r.getActions() != null && r.getActions().contains(action))
            .collect(Collectors.toList());
    for (Rule ruleModel : secReadruleModels) {

      List<Role> roles = ruleModel.getRoles();

      List<AssociationClassResource> resources =
          ruleModel.getResources().stream()
              .filter(res -> res instanceof AssociationClassResource)
              .map(AssociationClassResource.class::cast)
              .collect(Collectors.toList());

      List<AuthorizationConstraint> auths =
          ruleModel.getAuth().stream()
              .map(au -> new AuthorizationConstraint(au))
              .collect(Collectors.toList());

      for (Role role : roles) {
        for (AssociationClassResource resource : resources) {
          rules.add(
              new AssociationClassUnitRule(
                  Action.READ.name(), role.getRole(), auths, resource.getAssociationClass()));
        }
      }
    }
    return rules;
  }

  private static Collection<? extends SecUnitRule> getUnitRulesFromEntity(
      SecurityModel securityModel) {
    List<SecUnitRule> rules = new ArrayList<>();
    rules.addAll(getUnitRulesFromEntity(Action.CREATE, securityModel));
    rules.addAll(getUnitRulesFromEntity(Action.READ, securityModel));
    rules.addAll(getUnitRulesFromEntity(Action.UPDATE, securityModel));
    rules.addAll(getUnitRulesFromEntity(Action.DELETE, securityModel));
    return rules;
  }

  private static Collection<? extends SecUnitRule> getUnitRulesFromEntity(
      Action action, SecurityModel securityModel) {

    List<SecUnitRule> rules = new ArrayList<SecUnitRule>();

    List<Rule> secReadRuleModels =
        securityModel.getRules().stream()
            .filter(r -> r.getActions() != null && r.getActions().contains(action))
            .collect(Collectors.toList());
    for (Rule ruleModel : secReadRuleModels) {
      List<Role> roles = ruleModel.getRoles();

      List<AttributeResource> resources =
          ruleModel.getResources().stream()
              .filter(AttributeResource.class::isInstance)
              .map(AttributeResource.class::cast)
              .collect(Collectors.toList());

      List<AuthorizationConstraint> auths =
          ruleModel.getAuth().stream()
              .map(AuthorizationConstraint::new)
              .collect(Collectors.toList());

      for (Role role : roles) {
        for (AttributeResource resource : resources) {
          rules.add(
              new AttributeUnitRule(
                  Action.READ.name(),
                  role.getRole(),
                  auths,
                  resource.getEntity(),
                  resource.getAttribute()));
        }
      }
    }
    return rules;
  }
}
