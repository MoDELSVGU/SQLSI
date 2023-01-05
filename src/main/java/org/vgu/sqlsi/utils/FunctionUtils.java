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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.AssociationClass;
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

/** Provide the necessary functions to. */
public class FunctionUtils {

  /**
   * Return a list of Authorization function helper to be used by the stored procedure. Note that
   * for each attribute of each entity and AssociationClass or for each association there is a
   * coressponding Authorization function helper.
   *
   * <p>This means that there is also authorization helper function even for the resources not
   * explicitly specified inside the policy model file, in this case the function will enact the
   * default behavior of disallowing any access to the resource.
   */
  public static List<AuthFunc> printAuthFun(DataModel dataModel, SecurityModel securityModel)
      throws Exception {

    List<SecUnitRule> rules = RuleUtils.getAllUnitRules(securityModel);

    List<AuthFunc> functions = new ArrayList<AuthFunc>();

    for (Entity entity : dataModel.getEntities().values()) {
      functions.addAll(getAuthFun(entity, rules));
    }

    for (Association association : dataModel.getAssociations()) {
      functions.addAll(getAuthFun(association, rules));
    }

    for (AssociationClass associationClass : dataModel.getAssociationClasses()) {
      functions.addAll(getAuthFun(associationClass, rules));
    }

    return functions;
  }

  private static List<AuthFunc> getAuthFun(Entity entity, List<SecUnitRule> rules) {
    List<AuthFunc> functions = new ArrayList<AuthFunc>();
    for (Attribute attribute : entity.getAttributes()) {
      functions.add(getAuthFunFromAttribute("READ", entity, attribute, rules));
    }
    return functions;
  }

  private static Collection<? extends AuthFunc> getAuthFun(
      Association association, List<SecUnitRule> rules) {
    List<AuthFunc> functions = new ArrayList<AuthFunc>();
    functions.add(getAuthFunFromAssociation("READ", association, rules));
    return functions;
  }

  /**
   * Since AssociationClass has attribute of their there must also be coressponding authorization
   * functions corresponding to those attributes.
   *
   * @author HuMiTriet
   */
  private static Collection<? extends AuthFunc> getAuthFun(
      AssociationClass associationClass, List<SecUnitRule> rules) {
    List<AuthFunc> functions = new ArrayList<AuthFunc>();

    functions.add(getAuthFunFromAssociationClass("READ", associationClass, rules));

    associationClass
        .getAttributes()
        .forEach(
            associationClassAttribute -> {
              functions.add(
                  getAuthFunFromAttribute(
                      "READ", associationClass, associationClassAttribute, rules));
            });

    return functions;
  }

  private static AuthFunc getAuthFunFromAttribute(
      String action, Entity entity, Attribute attribute, List<SecUnitRule> rules) {
    HashMap<String, List<SecUnitRule>> indexRules =
        filterAndIndexRules(action, entity, attribute, rules);

    String authFunName = String.format("%1$s_%2$s", entity.getName(), attribute.getName());

    AuthFunc sqlAuthFunction = new AuthFunc(action, authFunName);

    sqlAuthFunction.setParameters(attribute);

    // only add in Authorization constraint if it is said explicitly in the security policy file
    if (!indexRules.isEmpty()) {
      for (String role : indexRules.keySet()) {

        AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(action, authFunName, role);
        sqlAuthRoleFunction.setParameters(attribute);
        List<SecUnitRule> ruleRoleBased = indexRules.get(role);

        sqlAuthRoleFunction.setOcl(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                .collect(Collectors.toList()));
        sqlAuthRoleFunction.setSql(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                .collect(Collectors.toList()));
        sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
      }
    }
    return sqlAuthFunction;
  }

  private static AuthFunc getAuthFunFromAttribute(
      String action,
      AssociationClass associationClass,
      Attribute attribute,
      List<SecUnitRule> rules) {
    HashMap<String, List<SecUnitRule>> indexRules =
        filterAndIndexRules(action, associationClass, attribute, rules);

    String authFunName =
        String.format("%1$s_%2$s", associationClass.getName(), attribute.getName());

    AuthFunc sqlAuthFunction = new AuthFunc(action, authFunName);

    sqlAuthFunction.setParameters(attribute);

    // only add in Authorization constraint if it is said explicitly in the security policy file
    if (!indexRules.isEmpty()) {
      for (String role : indexRules.keySet()) {

        AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(action, authFunName, role);
        sqlAuthRoleFunction.setParameters(attribute);
        List<SecUnitRule> ruleRoleBased = indexRules.get(role);

        sqlAuthRoleFunction.setOcl(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                .collect(Collectors.toList()));

        sqlAuthRoleFunction.setSql(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                .collect(Collectors.toList()));
        sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
      }
    }
    return sqlAuthFunction;
  }

  private static AuthFunc getAuthFunFromAssociation(
      String action, Association association, List<SecUnitRule> rules) {

    HashMap<String, List<SecUnitRule>> indexRules = filterAndIndexRules(action, association, rules);

    String authFunName = String.format("%s", association.getName());
    AuthFunc sqlAuthFunction = new AuthFunc(action, authFunName);

    sqlAuthFunction.setParameters(association);

    if (!indexRules.isEmpty()) {
      for (String role : indexRules.keySet()) {
        AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(action, authFunName, role);
        sqlAuthRoleFunction.setParameters(association);
        List<SecUnitRule> ruleRoleBased = indexRules.get(role);
        sqlAuthRoleFunction.setOcl(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                .collect(Collectors.toList()));
        sqlAuthRoleFunction.setSql(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                .collect(Collectors.toList()));
        sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
      }
    }
    return sqlAuthFunction;
  }

  private static AuthFunc getAuthFunFromAssociationClass(
      String action, AssociationClass associationClass, List<SecUnitRule> rules) {
    HashMap<String, List<SecUnitRule>> indexRules =
        filterAndIndexRules(action, associationClass, rules);

    String authFunName = String.format("%s", associationClass.getName());

    AuthFunc sqlAuthFunction = new AuthFunc(action, authFunName);

    sqlAuthFunction.setParameters(associationClass);

    if (!indexRules.isEmpty()) {
      for (String role : indexRules.keySet()) {
        AuthRoleFunc sqlAuthRoleFunction = new AuthRoleFunc(action, authFunName, role);
        sqlAuthRoleFunction.setParameters(associationClass);
        List<SecUnitRule> ruleRoleBased = indexRules.get(role);
        sqlAuthRoleFunction.setOcl(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getOcl))
                .collect(Collectors.toList()));
        sqlAuthRoleFunction.setSql(
            ruleRoleBased.stream()
                .map(SecUnitRule::getAuths)
                .flatMap(auths -> auths.stream().map(AuthorizationConstraint::getSql))
                .collect(Collectors.toList()));
        sqlAuthFunction.getFunctions().add(sqlAuthRoleFunction);
      }
    }

    return sqlAuthFunction;
  }

  /**
   * Filter and index rule overload for each attribute.
   *
   * <p>Filter: because the caller of this function getAuthFunFromAttribute(...) pass in a list of
   * attributes from getAuthFunc(..) which is all of the attribute of that entity. This method main
   * task is to filter out the unit rules that applies to the attribute.
   *
   * <p>Index: This method return a hashmap where the key is the role of the rule and the value is
   * the SecUnitRule that applies to the attribute.
   */
  public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
      String action, Entity entity, Attribute attribute, List<SecUnitRule> rules) {
    HashMap<String, List<SecUnitRule>> indexRules = new HashMap<>();

    if (rules != null) {
      for (SecUnitRule rule : rules) {
        if (rule instanceof AttributeUnitRule) {
          AttributeUnitRule attRule = (AttributeUnitRule) rule;
          if (attRule.getEntity().equals(entity.getName())
              && attRule.getAttribute().equals(attribute.getName())
              && attRule.getAction().equals(action)) {
            if (indexRules.containsKey(rule.getRole())) {
              // adding to existing role key if it already existed
              indexRules.get(rule.getRole()).add(rule);
            } else {
              // creating new role key if it didn't exist
              indexRules.put(rule.getRole(), new ArrayList<SecUnitRule>());
              indexRules.get(rule.getRole()).add(rule);
            }
          }
        }
      }
    }
    return indexRules;
  }

  public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
      String action,
      AssociationClass associationClass,
      Attribute attribute,
      List<SecUnitRule> rules) {
    HashMap<String, List<SecUnitRule>> indexRules = new HashMap<>();

    if (rules != null) {
      for (SecUnitRule rule : rules) {
        if (rule instanceof AttributeUnitRule) {
          AttributeUnitRule attRule = (AttributeUnitRule) rule;
          if (attRule.getEntity().equals(associationClass.getName())
              && attRule.getAttribute().equals(attribute.getName())
              && attRule.getAction().equals(action)) {
            if (indexRules.containsKey(rule.getRole())) {
              // adding to existing role key if it already existed
              indexRules.get(rule.getRole()).add(rule);
            } else {
              // creating new role key if it didn't exist
              indexRules.put(rule.getRole(), new ArrayList<SecUnitRule>());
              indexRules.get(rule.getRole()).add(rule);
            }
          }
        }
      }
    }
    return indexRules;
  }

  /** Same as the filter and index rule above. */
  public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
      String action, Association association, List<SecUnitRule> rules) {

    // Map roles and their association rules
    HashMap<String, List<SecUnitRule>> indexRules = new HashMap<String, List<SecUnitRule>>();

    if (rules != null) {
      for (SecUnitRule rule : rules) {
        if (rule instanceof AssociationUnitRule) {
          AssociationUnitRule attRule = (AssociationUnitRule) rule;
          if (attRule.getAssociation().equals(association.getName())
              && attRule.getAction().equals(action)) {
            if (indexRules.containsKey(rule.getRole())) {
              // adding to existing role key if it already existed
              indexRules.get(rule.getRole()).add(rule);
            } else {
              // creating new role key if it didn't exist
              indexRules.put(rule.getRole(), new ArrayList<SecUnitRule>());
              indexRules.get(rule.getRole()).add(rule);
            }
          }
        }
      }
    }
    return indexRules;
  }

  public static HashMap<String, List<SecUnitRule>> filterAndIndexRules(
      String action, AssociationClass associationClass, List<SecUnitRule> rules) {

    HashMap<String, List<SecUnitRule>> indexRules = new HashMap<String, List<SecUnitRule>>();

    if (rules != null) {
      for (SecUnitRule rule : rules) {
        if (rule instanceof AssociationUnitRule) {
          AssociationUnitRule attRule = (AssociationUnitRule) rule;
          if (attRule.getAssociation().equals(associationClass.getName())
              && attRule.getAction().equals(action)) {
            if (indexRules.containsKey(rule.getRole())) {
              indexRules.get(rule.getRole()).add(rule);
            } else {
              indexRules.put(rule.getRole(), new ArrayList<SecUnitRule>());
              indexRules.get(rule.getRole()).add(rule);
            }
          }
        }
      }
    }
    return indexRules;
  }
}
