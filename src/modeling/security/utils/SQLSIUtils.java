/**************************************************************************
Copyright 2019 Vietnamese-German-University
Copyright 2023 ETH Zurich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author: hoangnguyen (hoang.nguyen@inf.ethz.ch)
***************************************************************************/

package modeling.security.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import modeling.data.entities.Association;
import modeling.data.entities.Attribute;
import modeling.data.entities.DataModel;
import modeling.data.entities.Entity;
import modeling.security.entities.SecurityModel;
import modeling.security.mappings.SelectBasicAssociation;
import modeling.security.mappings.SelectBasicClass;
import modeling.security.mappings.SelectBasicSub;
import modeling.security.mappings.SelectJoinAssociationAndSub;
import modeling.security.mappings.SelectJoinClassAndAssociation;
import modeling.security.mappings.SelectJoinClassAndSub;
import modeling.security.mappings.SelectJoinSubAndSub;
import modeling.security.mappings.SelectSQLSI;
import modeling.security.statements.AuthFunc;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubSelect;

public class SQLSIUtils {

	public static boolean isUserClass(Entity entity, SecurityModel sm) {
		if (sm.getUserClass() == null || sm.getUserClass().size() == 0)
			return false;
		return sm.getUserClass().contains(entity.getName());
	}

	public static void classify(Statement statement, DataModel dataModel) throws Exception {
		if (statement instanceof Select) {
			classifySelect(statement, dataModel);
		} else {
			throw new Exception("Not supported type of statement");
		}
	}

	private static void classifySelect(Statement statement, DataModel dataModel) throws Exception {
		Select select = (Select) statement;
		SelectBody selectBody = select.getSelectBody();
		if (selectBody != null && selectBody instanceof PlainSelect) {
			SelectBody newSelectBody = classifySelectBody(dataModel, selectBody);
			select.setSelectBody(newSelectBody);
		} else {
			throw new Exception("Not supported type of statement");
		}
	}

	private static SelectBody classifySelectBody(DataModel dataModel, SelectBody selectBody) throws Exception {
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			SelectSQLSI newSelectBody;
			List<Join> joins = plainSelect.getJoins();
			if (joins == null || joins.size() == 0) {
				FromItem fromItem = plainSelect.getFromItem();
				newSelectBody = classifySelectBasic(dataModel, fromItem);
			} else if (joins.size() == 1) {
				FromItem fromItem = plainSelect.getFromItem();
				newSelectBody = classifySelectWithSingleJoin(dataModel, fromItem, joins);
			} else {
				throw new Exception("Not supported type of statement");
			}
			newSelectBody.setSelectItems(plainSelect.getSelectItems());
			newSelectBody.setWhere(plainSelect.getWhere());
			return newSelectBody;
		} else {
			throw new Exception("Not supported type of statement");
		}
	}

	private static SelectSQLSI classifySelectWithSingleJoin(DataModel dataModel, FromItem fromItem, List<Join> joins)
			throws Exception {
		Join join = joins.get(0);
		FromItem newFromItem;
		SelectSQLSI selectSQLSI;
		if (fromItem instanceof SubSelect) {
			SubSelect subselect = (SubSelect) fromItem;
			newFromItem = selectSS(dataModel, subselect);
			selectSQLSI = classifySelectWhenLeftItemIsSubSelect(dataModel, join);
		} else if (fromItem instanceof Table) {
			Table table = (Table) fromItem;
			newFromItem = table;
			if (dataModel.getAssociations() != null && dataModel.getAssociations().stream()
					.anyMatch(assoc -> assoc.getName().equals(table.getName()))) {
				selectSQLSI = classifySelectWhenLeftItemIsAssociation(dataModel, join);
			} else if (dataModel.getEntities() != null && dataModel.getEntities().containsKey(table.getName())) {
				selectSQLSI = classifySelectWhenLeftItemIsClass(dataModel, join);
			} else {
				throw new Exception("Not supported type of statement");
			}
		} else {
			throw new Exception("Not supported type of statement");
		}
		selectSQLSI.setFromItem(newFromItem);
		return selectSQLSI;
	}

	private static SelectSQLSI classifySelectWhenLeftItemIsClass(DataModel dataModel, Join join) throws Exception {

		SelectSQLSI selectSQLSI;
		Join newJoin = new Join();
		FromItem rightItem = join.getRightItem();
		FromItem newRightItem;
		if (rightItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) rightItem;
			selectSQLSI = new SelectJoinClassAndSub();
			newRightItem = selectSS(dataModel, subSelect);
		} else if (rightItem instanceof Table) {
			Table table = (Table) rightItem;
			newRightItem = table;
			if (dataModel.getAssociations() != null && dataModel.getAssociations().stream()
					.anyMatch(assoc -> assoc.getName().equals(table.getName()))) {
				selectSQLSI = new SelectJoinClassAndAssociation();
			} else if (dataModel.getEntities() != null && dataModel.getEntities().containsKey(table.getName())) {
				throw new Exception("Not supported type of statement");
			} else {
				throw new Exception("Not supported type of statement");
			}
		} else {
			throw new Exception("Not supported type of statement");
		}
		newJoin.setRightItem(newRightItem);
		newJoin.setOnExpression(join.getOnExpression());
		selectSQLSI.setJoins(Arrays.asList(newJoin));
		return selectSQLSI;
	}

	private static SelectSQLSI classifySelectWhenLeftItemIsAssociation(DataModel dataModel, Join join)
			throws Exception {
		SelectSQLSI selectSQLSI;
		Join newJoin = new Join();
		FromItem rightItem = join.getRightItem();
		FromItem newRightItem;
		if (rightItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) rightItem;
			selectSQLSI = new SelectJoinAssociationAndSub();
			newRightItem = selectSS(dataModel, subSelect);
		} else if (rightItem instanceof Table) {
			Table table = (Table) rightItem;
			newRightItem = table;
			if (dataModel.getAssociations() != null && dataModel.getAssociations().stream()
					.anyMatch(assoc -> assoc.getName().equals(table.getName()))) {
				throw new Exception("Not supported type of statement");
			} else if (dataModel.getEntities() != null && dataModel.getEntities().containsKey(table.getName())) {
				selectSQLSI = new SelectJoinClassAndAssociation();
			} else {
				throw new Exception("Not supported type of statement");
			}
		} else {
			throw new Exception("Not supported type of statement");
		}
		newJoin.setRightItem(newRightItem);
		newJoin.setOnExpression(join.getOnExpression());
		selectSQLSI.setJoins(Arrays.asList(newJoin));
		return selectSQLSI;
	}

	private static SelectSQLSI classifySelectWhenLeftItemIsSubSelect(DataModel dataModel, Join join) throws Exception {
		SelectSQLSI selectSQLSI;
		Join newJoin = new Join();
		FromItem rightItem = join.getRightItem();
		FromItem newRightItem;
		if (rightItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) rightItem;
			selectSQLSI = new SelectJoinSubAndSub();
			newRightItem = selectSS(dataModel, subSelect);
		} else if (rightItem instanceof Table) {
			Table table = (Table) rightItem;
			newRightItem = table;
			if (dataModel.getAssociations() != null && dataModel.getAssociations().stream()
					.anyMatch(assoc -> assoc.getName().equals(table.getName()))) {
				selectSQLSI = new SelectJoinAssociationAndSub();
			} else if (dataModel.getEntities() != null && dataModel.getEntities().containsKey(table.getName())) {
				selectSQLSI = new SelectJoinClassAndSub();
			} else {
				throw new Exception("Not supported type of statement");
			}
		} else {
			throw new Exception("Not supported type of statement");
		}
		newJoin.setRightItem(newRightItem);
		newJoin.setOnExpression(join.getOnExpression());
		selectSQLSI.setJoins(Arrays.asList(newJoin));
		return selectSQLSI;
	}

	private static SelectSQLSI classifySelectBasic(DataModel dataModel, FromItem fromItem) throws Exception {
		SelectSQLSI selectSQLSI = null;
		FromItem newFromItem;
		if (fromItem instanceof SubSelect) {
			selectSQLSI = new SelectBasicSub();
			SubSelect subSelect = (SubSelect) fromItem;
			newFromItem = selectSS(dataModel, subSelect);
		} else if (fromItem instanceof Table) {
			Table table = (Table) fromItem;
			newFromItem = table;
			if (dataModel.getAssociations() != null && dataModel.getAssociations().stream()
					.anyMatch(assoc -> assoc.getName().equals(table.getName()))) {
				selectSQLSI = new SelectBasicAssociation();
			} else if (dataModel.getEntities() != null && dataModel.getEntities().containsKey(table.getName())) {
				selectSQLSI = new SelectBasicClass();
			}
		} else {
			throw new Exception("Not supported type of statement");
		}
		selectSQLSI.setFromItem(newFromItem);
		return selectSQLSI;
	}

	private static SubSelect selectSS(DataModel dataModel, SubSelect subSelect) throws Exception {
		SubSelect newSubSelect = new SubSelect();
		newSubSelect.setAlias(subSelect.getAlias());
		newSubSelect.setSelectBody(classifySelectBody(dataModel, subSelect.getSelectBody()));
		return newSubSelect;
	}

	public static List<JSONObject> getAssociations(JSONArray context) {
		List<JSONObject> assocs = new ArrayList<JSONObject>();

		for (Object assoc : context) {
			JSONObject jsonAssos = (JSONObject) assoc;
			if (jsonAssos.containsKey("association")) {
				assocs.add(jsonAssos);
			}
		}
		return assocs;
	}

	public static JSONArray getAttributes(JSONArray context, String className) {
		JSONArray attributes = new JSONArray();
		for (Object entity : context) {
			if (((JSONObject) entity).containsKey("class")) {
				if (((JSONObject) entity).get("class").equals(className)) {
					if (((JSONObject) entity).containsKey("attributes")) {
						attributes = (JSONArray) ((JSONObject) entity).get("attributes");
					}

				}
			}
		}
		return attributes;
	}

	/* className is the name of the UMLclass (i.e., not an UMLassociation */
	@SuppressWarnings("unchecked")
	public static JSONArray getAssociationsFromClass(JSONArray context, String className) {
		JSONArray associations = new JSONArray();
		for (Object object : context) {
			if (((JSONObject) object).containsKey("association")) {
				JSONArray classes = (JSONArray) ((JSONObject) object).get("classes");
				JSONArray ends = (JSONArray) ((JSONObject) object).get("ends");
				for (int index_class = 0; index_class < classes.size(); index_class++) {
					if (classes.get(index_class).equals(className)) {
						JSONObject resource = new JSONObject();
						resource.put("name", ends.get(index_class));
						resource.put("association", ((JSONObject) object).get("association"));
						associations.add(resource);
					}
				}
			}

		}
		return associations;
	}

	public static Association getAssociation(DataModel dataModel, String association) {
		if (dataModel.getAssociations() == null || dataModel.getAssociations().isEmpty()) {
			return null;
		} else {
			for (Association as : dataModel.getAssociations()) {
				if (as.getName().equalsIgnoreCase(association)) {
					return as;
				} else {
					continue;
				}
			}
		}
		return null;
	}

	/* getAssociationSource */
	/* className is the association */
	/* endName is the assoc end */
	/* we assume that association+assoc_end is unique */
	public static String getAssociationSource(DataModel dataModel, String className, String endName) {
		if (dataModel.getAssociations() == null || dataModel.getAssociations().isEmpty()) {
			return null;
		} else {
			for (Association as : dataModel.getAssociations()) {
				if (as.getLeftEnd().getCurrentClass().equals(className) && as.getLeftEnd().equals(endName)) {
					return as.getName();
				} else if (as.getRightEnd().getCurrentClass().equals(className) && as.getRightEnd().equals(endName)) {
					return as.getName();
				} else {
					continue;
				}
			}
		}
		return null;
	}

	public static String getEndClassName(DataModel context, Association association, String endName) {
		if (association.getLeftEnd().getOpp().equals(endName)) {
			return association.getLeftEnd().getCurrentClass();
		}
		if (association.getRightEnd().getOpp().equals(endName)) {
			return association.getRightEnd().getCurrentClass();
		}
		return null;
	}

	public static boolean isAttribute(DataModel dataModel, String entityName, String attribute) {
		if (dataModel.getEntities() == null || dataModel.getEntities().isEmpty()) {
			return false;
		}
		if (!dataModel.getEntities().containsKey(entityName)) {
			return false;
		}
		Entity entity = dataModel.getEntities().get(entityName);
		for (Attribute att : entity.getAttributes()) {
			if (att.getName().equalsIgnoreCase(attribute)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAssociationEnd(DataModel dataModel, String className, String endName) {
		if (dataModel.getAssociations() == null || dataModel.getAssociations().isEmpty()) {
			return false;
		}
		for (Association assoc : dataModel.getAssociations()) {
			if (!assoc.getName().equalsIgnoreCase(className)) {
				continue;
			}
			if (assoc.getLeftEnd().getOpp().equalsIgnoreCase(endName)) {
				return true;
			}
			if (assoc.getRightEnd().getOpp().equalsIgnoreCase(endName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAssociation(JSONArray context, String className, String endName) {
		boolean result = false;
		for (Object object : context) {
			if (((JSONObject) object).containsKey("association")) {
				JSONArray ends = (JSONArray) ((JSONObject) object).get("ends");
				for (int index_end = 0; index_end < ends.size(); index_end++) {
					if (ends.get(index_end).equals(endName)) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	// super
	public static String getSuperClass(JSONArray context, String entityName) {
		// System.out.println(entityName);
		String superClass = null;
		for (Object entity : context) {
			if (((JSONObject) entity).get("class").equals(entityName)) {
				if (((JSONObject) entity).containsKey("super")) {
					superClass = (String) ((JSONObject) entity).get("super");
					break;
				}
			}
		}
		return superClass;
	}

	public static boolean isPrimaryKey(DataModel dataModel, String columnName, String tableName) {
		Entity entity = dataModel.getEntities().get(tableName);
		if (entity == null) {
			return false;
		}
		String primaryKey = String.format("%s_id", entity.getName());
		return columnName.equalsIgnoreCase(primaryKey);
	}

	// super
	public static boolean isSuperKey(JSONArray context, String columnName) {
		boolean result = false;
		for (Object entity : context) {
			if (((JSONObject) entity).containsKey("class")) {
				if (((JSONObject) entity).containsKey("super")) {
					String superClass = (String) ((JSONObject) entity).get("super");
					if (superClass.equals(columnName)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	// super
	public static boolean isSuper(JSONArray context, String entityName, String attributeName) {
		boolean result = false;
		for (Object entity : context) {
			if (((JSONObject) entity).get("class").equals(entityName)) {
				if (((JSONObject) entity).containsKey("super")) {
					String superClass = (String) ((JSONObject) entity).get("super");
					if (superClass.equals(attributeName)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public static boolean isParameter(JSONArray parameters, String columnName) {
		boolean result = false;
		if (parameters == null || parameters.isEmpty())
			return result;
		for (Object par : parameters) {
			if (((JSONObject) par).containsKey("name")) {
				if (((JSONObject) par).get("name").equals(columnName)) {
					result = true;
				}
			}
		}
		return result;
	}

	public static boolean isEntity(DataModel dataModel, String entityName) {
		return dataModel.getEntities().containsKey(entityName);
	}

	public static boolean isTable(DataModel dataModel, String entityName) {
		return dataModel.getEntities().containsKey(entityName)
				|| dataModel.getAssociations().stream().anyMatch(as -> as.getName().equals(entityName));
	}

	public static AuthFunc findAuthFunctionAssociation(DataModel dataModel, List<AuthFunc> functions, String columnName,
			String associationName) throws Exception {
		Optional<AuthFunc> authFun;
		if (functions == null || functions.isEmpty()) {
			throw new Exception("There is no auth function created");
		} else {
			String authFunName = String.format("%s", associationName);
			authFun = functions.stream().filter(f -> f.getResource().equals(authFunName)).findFirst();
		}
		if (authFun.isPresent()) {
			return authFun.get();
		} else {
			throw new Exception("There is no auth function created");
		}
	}

	public static AuthFunc findAuthFunctionAttribute(List<AuthFunc> functions, String columnName, String tableName)
			throws Exception {
		Optional<AuthFunc> authFun;
		if (functions == null || functions.isEmpty()) {
			throw new Exception("There is no auth function created");
		} else {
			String authFunName = String.format("%s_%s", tableName, columnName);
			authFun = functions.stream().filter(f -> f.getResource().equals(authFunName)).findFirst();
		}
		if (authFun.isPresent()) {
			return authFun.get();
		} else {
			throw new Exception("There is no auth function created");
		}
	}

}
