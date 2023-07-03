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

package modeling.security.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;

import modeling.data.entities.Association;
import modeling.data.entities.Attribute;
import modeling.data.entities.DataModel;
import modeling.data.entities.End;
import modeling.data.entities.End_AssociationClass;
import modeling.data.entities.Entity;
import modeling.security.statements.AuthFunc;
import modeling.security.utils.SQLSIUtils;
import modeling.statements.SQLTemporaryTable;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class SelectInjector implements SelectVisitor {
    private DataModel dataModel;
    private List<AuthFunc> functions;
    private String action;
    private JSONArray parameters;
    private Stack<SQLTemporaryTable> results = new Stack<SQLTemporaryTable>();
    private JSONArray authFunctions = new JSONArray();
    Integer tempTableNumber = 1;

    public SelectInjector() {
        results.clear();
    }

    public JSONArray getParameters() {
        return parameters;
    }

    public void setParameters(JSONArray parameters) {
        this.parameters = parameters;
    }

    public Stack<SQLTemporaryTable> getResults() {
        return results;
    }

    public void setResults(Stack<SQLTemporaryTable> results) {
        this.results = results;
    }

    public JSONArray getAuthFunctions() {
        return authFunctions;
    }

    public List<AuthFunc> getFunctions() {
        return functions;
    }

    public void setFunctions(List<AuthFunc> functions) {
        this.functions = functions;
    }

    /*
     * public JSONArray getAuthRoles(String entity, String resource, String action)
     * { JSONArray roles = new JSONArray(); JSONArray policy = this.getPolicy();
     * for(int i=0; i < policy.size(); i++) { JSONObject entityPolicy = (JSONObject)
     * policy.get(i);
     * 
     * if(entityPolicy.get("entity").equals(entity)) { // entity JSONArray
     * permissions = (JSONArray) entityPolicy.get("permissions"); for(int j = 0; j <
     * permissions.size(); j++) { JSONObject permission = (JSONObject)
     * permissions.get(j); for(Object actionPer : (JSONArray)
     * permission.get("actions")) { if (((String) actionPer).equals(action)) {
     * JSONArray resources = (JSONArray) permission.get("resources"); // resource
     * for(int h = 0; h < resources.size(); h++) {
     * if(resources.get(h).equals(resource)) { roles.addAll((JSONArray)
     * permission.get("roles")); } } } } } } } return roles; }
     */
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        try {
            if (plainSelect instanceof SelectBasicClass) {
                visitBasicClass(plainSelect);
            } else if (plainSelect instanceof SelectBasicAssociation) {
                visitBasicAssociation(plainSelect);
            } else if (plainSelect instanceof SelectBasicAssociationClass) {
                visitBasicAssociationClass(plainSelect);
            } else if (plainSelect instanceof SelectBasicSub) {
                visitSubSelect(plainSelect);
            } else if (plainSelect instanceof SelectJoinClassAndAssociation) {
                visitClassJoinAssociation(plainSelect);
            } else if (plainSelect instanceof SelectJoinClassAndSub) {
                visitClassJoinSubSelect(plainSelect);
            } else if (plainSelect instanceof SelectJoinAssociationAndSub) {
                visitAssociationJoinSub(plainSelect);
            } else if (plainSelect instanceof SelectJoinAssociationClassAndClass) {
                visitAssociationClassJoinClass(plainSelect);
            } else if (plainSelect instanceof SelectJoinAssociationClassAndSubSelect) {
                visitAssociationClassJoinSubSelect(plainSelect);
            } else if (plainSelect instanceof SelectJoinSubAndSub) {
                visitSubSelectJoinSubSelect(plainSelect);
            } else {
                throw new Exception("SecQuery cannot process this select statement.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void visitAssociationClassJoinSubSelect(PlainSelect plainSelect) {
        // Case 7
        // SELECT selitems FROM subselect JOIN asc ON exp
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression onExp = plainSelect.getJoins().get(0).getOnExpression();
        Expression whereExp = plainSelect.getWhere();

        FromItem leftFromItem = plainSelect.getFromItem();
        FromItem rightFromItem = plainSelect.getJoins().get(0).getRightItem();

        SubSelect subselect = null;
        Table asc = null;
        Entity ascEntity = null;

        if (leftFromItem instanceof SubSelect) {
            asc = (Table) rightFromItem;
            ascEntity = dataModel.getEntities().get(asc.getName());
            ;
            subselect = (SubSelect) leftFromItem;
        } else {
            asc = (Table) leftFromItem;
            ascEntity = dataModel.getEntities().get(asc.getName());
            ;
            subselect = (SubSelect) rightFromItem;
        }

        /*
         * Getting the information of ends of the association class
         */
        List<Table> end_tables = new ArrayList<Table>();
        List<String> ends = new ArrayList<String>();
        for (End_AssociationClass end_asc : ascEntity.getEnd_acs()) {
            end_tables.add(new Table(end_asc.getTargetClass()));
            ends.add(end_asc.getName());
        }
        // TODO: Generalize for n-ary case
        // For now let's focus on binary
        Table left = end_tables.get(0);
        Table right = end_tables.get(1);
        String leftEnd = ends.get(0);
        String rightEnd = ends.get(1);

        if (whereExp != null) {
            // Case 10
            // Strategy: We build a brand new query and visit that!
            // source:
            // SELECT selitems FROM subselect JOIN asc ON exp WHERE exp'
            // target:
            // SELECT selitems FROM (SELECT selitems' FROM subselect JOIN asc ON exp) AS
            // TEMP WHERE
            // exp'
            // where selitems' contains selitems and PropInWhe(exp')
            PlainSelect newSelect = new SelectBasicSub();
            newSelect.setSelectItems(selitems);
            newSelect.setWhere(whereExp);

            SubSelect subSelect = new SubSelect();
            subSelect.setAlias(new Alias("TEMP"));
            newSelect.setFromItem(subSelect);

            PlainSelect subPlainSelect = new SelectJoinAssociationClassAndClass();
            subSelect.setSelectBody(subPlainSelect);

            subPlainSelect.setFromItem(subselect);
            Join join = new Join();
            join.setRightItem(asc);
            join.setOnExpression(onExp);
            subPlainSelect.setJoins(Arrays.asList(join));

            PropsInInjector propsInInjector = new PropsInInjector();
            whereExp.accept(propsInInjector);
            List<Column> columns = propsInInjector.getProps();

            for (SelectItem si : selitems) {
                if (si instanceof SelectExpressionItem) {
                    propsInInjector = new PropsInInjector();
                    SelectExpressionItem sei = (SelectExpressionItem) si;
                    sei.getExpression().accept(propsInInjector);
                    columns.addAll(propsInInjector.getProps());
                }
            }

            List<SelectItem> selectItemsColumns = new ArrayList<SelectItem>();
            List<String> columnNames = new ArrayList<String>();
            for (Column col : columns) {
                if (SQLSIUtils.isAttribute(dataModel, asc.getName(), col.getColumnName())) {
                    if (!columnNames.contains(col.getColumnName())) {
                        selectItemsColumns.add(new SelectExpressionItem(col));
                    }
                }
            }
            subPlainSelect.addSelectItems(selectItemsColumns);
            newSelect.accept(this);
        } else {
            // Clause 7.a.
            subselect.getSelectBody().accept(this);

            // Clause 7.b.
            PropsInInjector propsInInjector = new PropsInInjector();
            onExp.accept(propsInInjector);
            List<Column> attsInOnExp = propsInInjector.getProps();

            if (!attsInOnExp.isEmpty()) {
                SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
                tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps1 = new PlainSelect();
                List<SelectItem> selectItemsColumns = new ArrayList<SelectItem>();
                for (Column col : attsInOnExp) {
                    if (SQLSIUtils.isAttribute(dataModel, asc.getName(), col.getColumnName())) {
                        selectItemsColumns.add(new SelectExpressionItem(col));
                    }
                }
                try {
                    ps1.addSelectItems(SecExpAttList(selectItemsColumns, asc));
                    ps1.setFromItem(asc);
                    tempTable1.setSelectBody(ps1);
                    this.results.add(tempTable1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Clause 7.c.
            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            try {
                ps2.setSelectItems(SecExpAttList(selitems, asc));
                ps2.setFromItem(asc);
                Join join = new Join();
                join.setRightItem(subselect);
                join.setOnExpression(onExp);
                ps2.setJoins(Arrays.asList(join));
                tempTable2.setSelectBody(ps2);
                this.results.add(tempTable2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clause 7.d.
            boolean isLeftEndPropsIn = PropsIn(onExp, leftEnd);
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isLeftEndPropsIn = isLeftEndPropsIn || PropsIn(sei.getExpression(), leftEnd);
                }
            }
            boolean isRightEndPropsIn = PropsIn(onExp, rightEnd);
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isRightEndPropsIn = isRightEndPropsIn || PropsIn(sei.getExpression(), rightEnd);
                }
            }
            if (isLeftEndPropsIn || isRightEndPropsIn) {
                SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
                tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps3 = new PlainSelect();
                SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                leftSelectExpressionItem.setAlias(new Alias(leftEnd));
                Expression leftIDExp = new Column(
                        String.format("%s_id", left.getName()));
                leftSelectExpressionItem.setExpression(leftIDExp);
                SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
                rightSelectExpressionItem.setAlias(new Alias(rightEnd));
                Expression rightIDExp = new Column(String.format("%s_id", right
                        .getName()));
                rightSelectExpressionItem.setExpression(rightIDExp);
                ps3.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
                ps3.setFromItem(left);
                Join join1 = new Join();
                join1.setRightItem(right);
                join1.setSimple(true);
                ps3.setJoins(Arrays.asList(join1));
                tempTable3.setSelectBody(ps3);
                this.results.add(tempTable3);

                SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
                tempTable4.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps4 = new PlainSelect();
                Expression exp = SecExpAssocClass(new Column(leftEnd), asc);
                SelectExpressionItem leftSelectItem = new SelectExpressionItem(exp);
                leftSelectItem.setAlias(new Alias("res"));
                ps4.addSelectItems(leftSelectItem);
                ps4.setFromItem(new Table(getResults().peek().getName()));
                tempTable4.setSelectBody(ps4);
                this.results.add(tempTable4);
            }

            // Clause 7.e.
            boolean isAttPropsIn = false;
            isAttPropsIn = isAttPropsIn || PropsIn(onExp, String.format("%s_id", asc.getName()));
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), String.format("%s_id", asc.getName()));
                }
            }
            for (Attribute at : ascEntity.getAttributes()) {
                isAttPropsIn = isAttPropsIn || PropsIn(onExp, at.getName());
                for (SelectItem selitem : selitems) {
                    if (selitem instanceof SelectExpressionItem) {
                        SelectExpressionItem sei = (SelectExpressionItem) selitem;
                        isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), at.getName());
                    }
                }
            }
            if (isAttPropsIn) {
                if (isLeftEndPropsIn) {
                    SQLTemporaryTable tempTable5 = new SQLTemporaryTable();
                    tempTable5.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                    PlainSelect ps5 = new PlainSelect();
                    SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                    Expression leftIDExp = new Column(
                            String.format("%s", leftEnd));
                    Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                    leftSelectExpressionItem.setExpression(exp);
                    leftSelectExpressionItem.setAlias(new Alias("left_res"));
                    ps5.addSelectItems(leftSelectExpressionItem);
                    ps5.setFromItem(asc);
                    Join join = new Join();
                    join.setRightItem(subselect);
                    join.setOnExpression(onExp);
                    ps5.setJoins(Arrays.asList(join));
                    tempTable5.setSelectBody(ps5);
                    this.results.add(tempTable5);
                }
                if (isRightEndPropsIn) {
                    SQLTemporaryTable tempTable6 = new SQLTemporaryTable();
                    tempTable6.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                    PlainSelect ps6 = new PlainSelect();
                    SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                    Expression leftIDExp = new Column(
                            String.format("%s", rightEnd));
                    Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                    leftSelectExpressionItem.setExpression(exp);
                    leftSelectExpressionItem.setAlias(new Alias("right_res"));
                    ps6.addSelectItems(leftSelectExpressionItem);
                    ps6.setFromItem(asc);
                    Join join = new Join();
                    join.setRightItem(subselect);
                    join.setOnExpression(onExp);
                    ps6.setJoins(Arrays.asList(join));
                    tempTable6.setSelectBody(ps6);
                    this.results.add(tempTable6);
                }
            }
        }
    }

    private void visitAssociationClassJoinClass(PlainSelect plainSelect) {
        // Case 6
        // SELECT selitems FROM c JOIN asc ON exp
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression onExp = plainSelect.getJoins().get(0).getOnExpression();
        Expression whereExp = plainSelect.getWhere();

        Table leftTable = (Table) plainSelect.getFromItem();
        Table rightTable = (Table) plainSelect.getJoins().get(0).getRightItem();

        Table c = null;
        Table asc = null;
        Entity cEntity = null;
        Entity ascEntity = null;

        Entity leftEntity = dataModel.getEntities().get(leftTable.getName());
        if (leftEntity.isAssociation()) {
            asc = leftTable;
            ascEntity = leftEntity;
            c = rightTable;
            cEntity = dataModel.getEntities().get(c.getName());
        } else {
            c = leftTable;
            cEntity = leftEntity;
            asc = rightTable;
            ascEntity = dataModel.getEntities().get(asc.getName());
        }

        /*
         * Getting the information of ends of the association class
         */
        List<Table> end_tables = new ArrayList<Table>();
        List<String> ends = new ArrayList<String>();
        for (End_AssociationClass end_asc : ascEntity.getEnd_acs()) {
            end_tables.add(new Table(end_asc.getTargetClass()));
            ends.add(end_asc.getName());
        }
        // TODO: Generalize for n-ary case
        // For now let's focus on binary
        Table left = end_tables.get(0);
        Table right = end_tables.get(1);
        String leftEnd = ends.get(0);
        String rightEnd = ends.get(1);

        if (whereExp != null) {
            // Case 9
            // Strategy: We build a brand new query and visit that!
            // source:
            // SELECT selitems FROM c JOIN asc ON exp WHERE exp'
            // target:
            // SELECT selitems FROM (SELECT selitems' FROM c JOIN asc ON exp) AS TEMP WHERE
            // exp'
            // where selitems' contains selitems and PropInWhe(exp')
            PlainSelect newSelect = new SelectBasicSub();
            newSelect.setSelectItems(selitems);
            newSelect.setWhere(whereExp);

            SubSelect subSelect = new SubSelect();
            subSelect.setAlias(new Alias("TEMP"));
            newSelect.setFromItem(subSelect);

            PlainSelect subPlainSelect = new SelectJoinAssociationClassAndClass();
            subSelect.setSelectBody(subPlainSelect);

            subPlainSelect.setFromItem(c);
            Join join = new Join();
            join.setRightItem(asc);
            join.setOnExpression(onExp);
            subPlainSelect.setJoins(Arrays.asList(join));

            PropsInInjector propsInInjector = new PropsInInjector();
            whereExp.accept(propsInInjector);
            List<Column> columns = propsInInjector.getProps();

            for (SelectItem si : selitems) {
                if (si instanceof SelectExpressionItem) {
                    propsInInjector = new PropsInInjector();
                    SelectExpressionItem sei = (SelectExpressionItem) si;
                    sei.getExpression().accept(propsInInjector);
                    columns.addAll(propsInInjector.getProps());
                }
            }

            List<SelectItem> selectItemsColumns = new ArrayList<SelectItem>();
            List<String> columnNames = new ArrayList<String>();
            for (Column col : columns) {
                if (SQLSIUtils.isAttribute(dataModel, asc.getName(), col.getColumnName())) {
                    if (!columnNames.contains(col.getColumnName())) {
                        selectItemsColumns.add(new SelectExpressionItem(col));
                    }
                }
            }
            subPlainSelect.addSelectItems(selectItemsColumns);
            newSelect.accept(this);
        } else {
            // Clause 6.a. and 6.b.
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            ps1.setSelectItems(Arrays.asList(new AllColumns()));
            ps1.setFromItem(c);
            Join join = new Join();
            join.setRightItem(asc);
            join.setOnExpression(SecExpAtt(onExp, c));
            ps1.setJoins(Arrays.asList(join));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

            // Clause 6.c. and 6.d.
            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            try {
                ps2.setSelectItems(SecExpAttList(selitems, c));
                ps2.setFromItem(new Table(getResults().peek().getName()));
                tempTable2.setSelectBody(ps2);
                this.results.add(tempTable2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clause 6.e.
            boolean isLeftEndPropsIn = PropsIn(onExp, leftEnd);
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isLeftEndPropsIn = isLeftEndPropsIn || PropsIn(sei.getExpression(), leftEnd);
                }
            }
            boolean isRightEndPropsIn = PropsIn(onExp, rightEnd);
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isRightEndPropsIn = isRightEndPropsIn || PropsIn(sei.getExpression(), rightEnd);
                }
            }
            if (isLeftEndPropsIn || isRightEndPropsIn) {
                SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
                tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps3 = new PlainSelect();
                SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                leftSelectExpressionItem.setAlias(new Alias(leftEnd));
                Expression leftIDExp = new Column(
                        String.format("%s_id", left.getName()));
                leftSelectExpressionItem.setExpression(leftIDExp);
                SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
                rightSelectExpressionItem.setAlias(new Alias(rightEnd));
                Expression rightIDExp = new Column(String.format("%s_id", right
                        .getName()));
                rightSelectExpressionItem.setExpression(rightIDExp);
                ps3.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
                ps3.setFromItem(left);
                Join join1 = new Join();
                join1.setRightItem(right);
                join1.setSimple(true);
                ps3.setJoins(Arrays.asList(join1));
                tempTable3.setSelectBody(ps3);
                this.results.add(tempTable3);

                SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
                tempTable4.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps4 = new PlainSelect();
                Expression exp = SecExpAssocClass(new Column(leftEnd), asc);
                SelectExpressionItem leftSelectItem = new SelectExpressionItem(exp);
                ps4.addSelectItems(leftSelectItem);
                ps4.setFromItem(new Table(getResults().peek().getName()));
                tempTable4.setSelectBody(ps4);
                this.results.add(tempTable4);
            }

            // Clause 6.f.
            boolean isAttPropsIn = false;
            isAttPropsIn = isAttPropsIn || PropsIn(onExp, String.format("%s_id", asc.getName()));
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), String.format("%s_id", asc.getName()));
                }
            }
            for (Attribute at : ascEntity.getAttributes()) {
                isAttPropsIn = isAttPropsIn || PropsIn(onExp, at.getName());
                for (SelectItem selitem : selitems) {
                    if (selitem instanceof SelectExpressionItem) {
                        SelectExpressionItem sei = (SelectExpressionItem) selitem;
                        isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), at.getName());
                    }
                }
            }
            if (isAttPropsIn) {
                if (isLeftEndPropsIn) {
                    SQLTemporaryTable tempTable5 = new SQLTemporaryTable();
                    tempTable5.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                    PlainSelect ps5 = new PlainSelect();
                    SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                    Expression leftIDExp = new Column(
                            String.format("%s", leftEnd));
                    Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                    leftSelectExpressionItem.setExpression(exp);
                    ps5.addSelectItems(leftSelectExpressionItem);
                    ps5.setFromItem(asc);
                    tempTable5.setSelectBody(ps5);
                    this.results.add(tempTable5);
                }
                if (isRightEndPropsIn) {
                    SQLTemporaryTable tempTable6 = new SQLTemporaryTable();
                    tempTable6.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                    PlainSelect ps6 = new PlainSelect();
                    SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                    Expression leftIDExp = new Column(
                            String.format("%s", rightEnd));
                    Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                    leftSelectExpressionItem.setExpression(exp);
                    ps6.addSelectItems(leftSelectExpressionItem);
                    ps6.setFromItem(asc);
                    tempTable6.setSelectBody(ps6);
                    this.results.add(tempTable6);
                }
            }
        }
    }

    private void visitSubSelectJoinSubSelect(PlainSelect plainSelect) {
//        List<SelectItem> selitems = plainSelect.getSelectItems();
//        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
//        Expression exp_ = plainSelect.getWhere();
        SubSelect subselect1 = (SubSelect) plainSelect.getFromItem();
        SubSelect subselect2 = (SubSelect) plainSelect.getJoins().get(0).getRightItem();

        subselect1.getSelectBody().accept(this);
//        String lastTempSubSelect1 = new String(getResults().peek().getName());

        subselect2.getSelectBody().accept(this);
//        String lastTempSubSelect2 = new String(getResults().peek().getName());

        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//        PlainSelect ps1 = new PlainSelect();
//        ps1.setSelectItems(selitems);
//        ps1.setFromItem(new Table(lastTempSubSelect1));
//        Join join1 = new Join();
//        join1.setRightItem(new Table(lastTempSubSelect2));
//        join1.setOnExpression(exp);
//        ps1.setJoins(Arrays.asList(join1));
//        ps1.setWhere(exp_);
        tempTable1.setSelectBody(plainSelect);
        this.results.add(tempTable1);
    }

    private void visitAssociationJoinSub(PlainSelect plainSelect) throws Exception {
//        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
//        Expression exp_ = plainSelect.getWhere();
        Table as = (Table) plainSelect.getFromItem();
        SubSelect subselect = (SubSelect) plainSelect.getJoins().get(0).getRightItem();

        Association association = SQLSIUtils.getAssociation(dataModel, as.getName());
        Table left = new Table(association.getLeftEnd().getCurrentClass());
        Table right = new Table(association.getRightEnd().getCurrentClass());
        String leftEnd = association.getLeftEnd().getOpp();
        String rightEnd = association.getRightEnd().getOpp();

        subselect.getSelectBody().accept(this);
//        List<SelectItem> currentSelItems1 = new ArrayList<SelectItem>();
//        currentSelItems1
//            .addAll(((PlainSelect) this.getResults().peek().getSelectBody())
//                .getSelectItems());

        if (PropsIn(exp, leftEnd) && !PropsIn(exp, rightEnd)) {
            Table table = new Table(getResults().peek().getName());
            table.setAlias(subselect.getAlias());
            Column col = CompWithInOn(exp, leftEnd);
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            leftSelectExpressionItem.setExpression(col);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            Expression rightExp = new Column(String.format("%s_id", right.getName()));
            rightSelectExpressionItem.setExpression(rightExp);
            ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
//            for (SelectItem item : currentSelItems1) {
//                SelectExpressionItem expItem = (SelectExpressionItem) item;
//                if (!expItem.getAlias().getName()
//                    .equalsIgnoreCase(col.getColumnName())) {
//                    SelectExpressionItem newExpItem = new SelectExpressionItem();
//                    newExpItem.setExpression(
//                        new Column(new Table(subselect.getAlias().getName()),
//                            expItem.getAlias().getName()));
//                    newExpItem.setAlias(expItem.getAlias());
//                    ps1.addSelectItems(newExpItem);
//                }
//            }
            ps1.setFromItem(right);
            Join join = new Join();
            join.setRightItem(table);
            join.setOnExpression(RepExp(exp, association, col, rightExp));
            ps1.setJoins(Arrays.asList(join));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

//            List<SelectItem> currentSelItems2 = new ArrayList<SelectItem>();
//            currentSelItems2
//                .addAll(((PlainSelect) this.getResults().peek().getSelectBody())
//                    .getSelectItems());
            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(new Column(leftEnd));
            ps2.setSelectItems(SecExpList(Arrays.asList(leftSelectExp), as));
//            for (SelectItem item : currentSelItems2) {
//                SelectExpressionItem expItem = (SelectExpressionItem) item;
//                if (!expItem.getAlias().getName().equalsIgnoreCase(leftEnd)
//                    && !expItem.getAlias().getName()
//                        .equalsIgnoreCase(rightEnd)) {
//                    SelectExpressionItem newExpItem = new SelectExpressionItem();
//                    newExpItem.setExpression(new Column(
//                        new Table(this.getResults().peek().getName()),
//                        expItem.getAlias().getName()));
//                    newExpItem.setAlias(expItem.getAlias());
//                    ps2.addSelectItems(newExpItem);
//                }
//            }
            // Old implementation
//            ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
            ps2.setFromItem(new Table(getResults().peek().getName()));
            tempTable2.setSelectBody(ps2);
            this.results.add(tempTable2);

            // Old implementation
//            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//            tempTable3.setName(
//                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(selitems);
//            Table fromItem = new Table(getResults().peek().getName());
//            fromItem.setAlias(subselect.getAlias());
//            ps3.setFromItem(fromItem);
//            EqualsTo linkEq1 = new EqualsTo();
//            linkEq1.setLeftExpression(new Column("link"));
//            linkEq1.setRightExpression(new LongValue(1L));
//            ps3.setWhere(linkEq1);
//            tempTable3.setSelectBody(ps3);
//            this.results.add(tempTable3);

            // New implementation
            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
            tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(selitems);
//            ps3.setFromItem(as);
//            Join join3 = new Join();
//            join3.setRightItem(subselect);
//            join3.setOnExpression(exp);
//            ps3.setJoins(Arrays.asList(join3));
//            ps3.setWhere(exp_);
            tempTable3.setSelectBody(plainSelect);
            this.results.add(tempTable3);

        } else if (PropsIn(exp, rightEnd) && !PropsIn(exp, leftEnd)) {
            Table table = new Table(getResults().peek().getName());
            table.setAlias(subselect.getAlias());
            Column col = CompWithInOn(exp, rightEnd);
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            Expression leftExp = new Column(String.format("%s_id", left.getName()));
            leftSelectExpressionItem.setExpression(leftExp);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            rightSelectExpressionItem.setExpression(col);
            ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
//            for (SelectItem item : currentSelItems1) {
//                SelectExpressionItem expItem = (SelectExpressionItem) item;
//                if (!expItem.getAlias().getName()
//                    .equalsIgnoreCase(col.getColumnName())) {
//                    SelectExpressionItem newExpItem = new SelectExpressionItem();
//                    newExpItem.setExpression(
//                        new Column(new Table(subselect.getAlias().getName()),
//                            expItem.getAlias().getName()));
//                    newExpItem.setAlias(expItem.getAlias());
//                    ps1.addSelectItems(newExpItem);
//                }
//            }
            ps1.setFromItem(left);
            Join join = new Join();
            join.setRightItem(table);
            join.setOnExpression(RepExp(exp, association, leftExp, col));
            ps1.setJoins(Arrays.asList(join));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

//            List<SelectItem> currentSelItems2 = new ArrayList<SelectItem>();
//            currentSelItems2
//                .addAll(((PlainSelect) this.getResults().peek().getSelectBody())
//                    .getSelectItems());
            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(new Column(leftEnd));
            ps2.setSelectItems(SecExpList(Arrays.asList(leftSelectExp), as));
//            for (SelectItem item : currentSelItems2) {
//                SelectExpressionItem expItem = (SelectExpressionItem) item;
//                if (!expItem.getAlias().getName().equalsIgnoreCase(leftEnd)
//                    && !expItem.getAlias().getName()
//                        .equalsIgnoreCase(rightEnd)) {
//                    SelectExpressionItem newExpItem = new SelectExpressionItem();
//                    newExpItem.setExpression(new Column(
//                        new Table(this.getResults().peek().getName()),
//                        expItem.getAlias().getName()));
//                    newExpItem.setAlias(expItem.getAlias());
//                    ps2.addSelectItems(newExpItem);
//                }
//            }
            // Old implementation
//            ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
            ps2.setFromItem(new Table(getResults().peek().getName()));
            tempTable2.setSelectBody(ps2);
            this.results.add(tempTable2);

            // Old implementation
//            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//            tempTable3.setName(
//                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(selitems);
//            Table fromItem = new Table(getResults().peek().getName());
//            fromItem.setAlias(subselect.getAlias());
//            ps3.setFromItem(fromItem);
//            EqualsTo linkEq1 = new EqualsTo();
//            linkEq1.setLeftExpression(new Column("link"));
//            linkEq1.setRightExpression(new LongValue(1L));
//            ps3.setWhere(linkEq1);
//            tempTable3.setSelectBody(ps3);
//            this.results.add(tempTable3);

            // New implementation
            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
            tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(selitems);
//            ps3.setFromItem(as);
//            Join join3 = new Join();
//            join3.setRightItem(subselect);
//            join3.setOnExpression(exp);
//            ps3.setJoins(Arrays.asList(join3));
//            ps3.setWhere(exp_);
            tempTable3.setSelectBody(plainSelect);
            this.results.add(tempTable3);
        } else {
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            Expression leftExp = new Column(String.format("%s_id", left.getName()));
            leftSelectExpressionItem.setExpression(leftExp);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            Expression rightExp = new Column(String.format("%s_id", right.getName()));
            rightSelectExpressionItem.setExpression(rightExp);
            ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
            ps1.setFromItem(left);
            Join join = new Join();
            join.setRightItem(right);
            ps1.setWhere(RepExp(exp, association, leftExp, rightExp));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(new Column(leftEnd));
            ps2.setSelectItems(SecExpList(Arrays.asList(leftSelectExp), as));
            // Old implementation
//            ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
            ps2.setFromItem(new Table(getResults().peek().getName()));
            tempTable2.setSelectBody(ps2);
            this.results.add(tempTable2);

            // Old implementation
//            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//            tempTable3.setName(
//                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(Arrays.asList(leftSelectExp, rightSelectExp));
//            ps3.setFromItem(new Table(getResults().peek().getName()));
//            EqualsTo linkEq1 = new EqualsTo();
//            linkEq1.setLeftExpression(new Column("link"));
//            linkEq1.setRightExpression(new LongValue(1L));
//            ps3.setWhere(linkEq1);
//            tempTable3.setSelectBody(ps3);
//            this.results.add(tempTable3);
//
//            SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
//            tempTable4.setName(
//                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps4 = new PlainSelect();
//            ps4.setSelectItems(selitems);
//            ps4.setFromItem(new Table(lastTempSubSelect1));
//            Join join4 = new Join();
//            join4.setRightItem(new Table(getResults().peek().getName()));
//            join4.setOnExpression(exp);
//            ps4.setJoins(Arrays.asList(join4));
//            ps4.setWhere(exp_);
//            tempTable4.setSelectBody(ps4);
//            this.results.add(tempTable4);

            // New implementation
            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
            tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(selitems);
//            ps3.setFromItem(as);
//            Join join3 = new Join();
//            join3.setRightItem(subselect);
//            join3.setOnExpression(exp);
//            ps3.setJoins(Arrays.asList(join3));
//            ps3.setWhere(exp_);
            tempTable3.setSelectBody(plainSelect);
            this.results.add(tempTable3);
        }
    }

    private Column CompWithInOn(Expression exp, String leftEnd) {
        CompWithInInjector compWithInInjector = new CompWithInInjector();
        compWithInInjector.setPropName(leftEnd);
        exp.accept(compWithInInjector);
        return compWithInInjector.getCol();
    }

    private boolean PropsIn(Expression exp, String leftEnd) {
        PropsInInjector propsInInjector = new PropsInInjector();
        propsInInjector.setPropName(leftEnd);
        exp.accept(propsInInjector);
        return propsInInjector.getIsPropsIn();
    }

    private void visitClassJoinSubSelect(PlainSelect plainSelect) throws Exception {
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
        Expression exp_ = plainSelect.getWhere();
        Table c = (Table) plainSelect.getFromItem();
        SubSelect subselect = (SubSelect) plainSelect.getJoins().get(0).getRightItem();

        subselect.getSelectBody().accept(this);

        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps1 = new PlainSelect();
        ps1.setSelectItems(Arrays.asList(new AllColumns()));
        ps1.setFromItem(c);
        Join join1 = new Join();
        Table table = new Table(getResults().peek().getName());
        table.setAlias(subselect.getAlias());
        join1.setRightItem(table);
        join1.setOnExpression(SecExp(exp, c));
        ps1.setJoins(Arrays.asList(join1));
        tempTable1.setSelectBody(ps1);
        this.results.add(tempTable1);

        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        ps2.setSelectItems(Arrays.asList(new AllColumns()));
        ps2.setFromItem(new Table(getResults().peek().getName()));
        ps2.setWhere(SecExp(exp_, c));
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);

        SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
        tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps3 = new PlainSelect();
        ps3.setSelectItems(SecExpList(selitems, c));
        ps3.setFromItem(new Table(getResults().peek().getName()));
        tempTable3.setSelectBody(ps3);
        this.results.add(tempTable3);
    }

    private void visitClassJoinAssociation(PlainSelect plainSelect) throws Exception {
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
        Expression exp_ = plainSelect.getWhere();
        Table c = (Table) plainSelect.getFromItem();
        Table as = (Table) plainSelect.getJoins().get(0).getRightItem();

        Association association = SQLSIUtils.getAssociation(dataModel, as.getName());
        Table left = new Table(association.getLeftEnd().getCurrentClass());
        Table right = new Table(association.getRightEnd().getCurrentClass());
        String leftEnd = association.getLeftEnd().getOpp();
        String rightEnd = association.getRightEnd().getOpp();

        if (exp_ == null) {
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            leftSelectExpressionItem.setExpression(new Column(String.format("%s_id", left.getName())));
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            rightSelectExpressionItem.setExpression(new Column(String.format("%s_id", right.getName())));
            ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
            ps1.setFromItem(left);
            Join join = new Join();
            join.setRightItem(right);
            join.setSimple(true);
            ps1.setJoins(Arrays.asList(join));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(new Column(leftEnd));
            ps2.setSelectItems(SecExpList(Arrays.asList(leftSelectExp), as));
            // Old implementation
//            ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
            ps2.setFromItem(new Table(getResults().peek().getName()));
            tempTable2.setSelectBody(ps2);
            this.results.add(tempTable2);

            // Old implementation
//            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//            tempTable3.setName(
//                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//            PlainSelect ps3 = new PlainSelect();
//            ps3.setSelectItems(Arrays.asList(leftSelectExp, rightSelectExp));
//            ps3.setFromItem(new Table(getResults().peek().getName()));
//            EqualsTo linkEq1 = new EqualsTo();
//            linkEq1.setLeftExpression(new Column("link"));
//            linkEq1.setRightExpression(new LongValue(1L));
//            ps3.setWhere(linkEq1);
//            tempTable3.setSelectBody(ps3);
//            this.results.add(tempTable3);

            SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
            tempTable4.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps4 = new PlainSelect();
            ps4.setSelectItems(Arrays.asList(new AllColumns()));
            ps4.setFromItem(c);
            Join join4 = new Join();
            join4.setRightItem(as);
            join4.setOnExpression(SecExp(exp, c));
            ps4.setJoins(Arrays.asList(join4));
            tempTable4.setSelectBody(ps4);
            this.results.add(tempTable4);

            SQLTemporaryTable tempTable5 = new SQLTemporaryTable();
            tempTable5.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps5 = new PlainSelect();
            ps5.setSelectItems(Arrays.asList(new AllColumns()));
            ps5.setFromItem(new Table(getResults().peek().getName()));
            ps5.setWhere(SecExp(exp_, c));
            tempTable5.setSelectBody(ps5);
            this.results.add(tempTable5);

            SQLTemporaryTable tempTable6 = new SQLTemporaryTable();
            tempTable6.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps6 = new PlainSelect();
            ps6.setSelectItems(SecExpList(selitems, c));
            ps6.setFromItem(new Table(getResults().peek().getName()));
            tempTable6.setSelectBody(ps6);
            this.results.add(tempTable6);
        } else {
            // Case 8
            // Strategy: We build a brand new query and visit that!
            // source:
            // SELECT selitems FROM c JOIN as ON exp WHERE exp'
            // target:
            // SELECT selitems FROM (SELECT selitems' FROM c JOIN as ON exp) AS
            // TEMP WHERE
            // exp'
            // where selitems' contains selitems and PropInWhe(exp')
            PlainSelect newSelect = new SelectBasicSub();
            newSelect.setSelectItems(selitems);
            newSelect.setWhere(exp_);

            SubSelect subSelect = new SubSelect();
            subSelect.setAlias(new Alias("TEMP"));
            newSelect.setFromItem(subSelect);

            PlainSelect subPlainSelect = new SelectJoinAssociationClassAndClass();
            subSelect.setSelectBody(subPlainSelect);

            subPlainSelect.setFromItem(c);
            Join join = new Join();
            join.setRightItem(as);
            join.setOnExpression(exp);
            subPlainSelect.setJoins(Arrays.asList(join));

            PropsInInjector propsInInjector = new PropsInInjector();
            exp_.accept(propsInInjector);
            List<Column> columns = propsInInjector.getProps();

            for (SelectItem si : selitems) {
                if (si instanceof SelectExpressionItem) {
                    propsInInjector = new PropsInInjector();
                    SelectExpressionItem sei = (SelectExpressionItem) si;
                    sei.getExpression().accept(propsInInjector);
                    columns.addAll(propsInInjector.getProps());
                }
            }

            List<SelectItem> selectItemsColumns = new ArrayList<SelectItem>();
            List<String> columnNames = new ArrayList<String>();
            for (Column col : columns) {
                if (SQLSIUtils.isAttribute(dataModel, c.getName(), col.getColumnName())) {
                    if (!columnNames.contains(col.getColumnName())) {
                        selectItemsColumns.add(new SelectExpressionItem(col));
                    }
                }
            }
            subPlainSelect.addSelectItems(selectItemsColumns);
            newSelect.accept(this);
        }
    }

    /*
     * SELECT selitems FROM subselect WHERE exp. It is worth noticing that selitems
     * and exp can only contains the expression in the subselect or literal
     */
    private void visitSubSelect(PlainSelect plainSelect) {
        /*
         * Gathering components
         */
//        List<SelectItem> selitems = plainSelect.getSelectItems();
        SubSelect subselect = (SubSelect) plainSelect.getFromItem();
//        Expression exp = plainSelect.getWhere();

        // Visiting subselect
        subselect.getSelectBody().accept(this);

        // Old implementation
        // Create temp table with previous temporary table
//        SQLTemporaryTable tempTable = new SQLTemporaryTable();
//        tempTable.setName(
//            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//        PlainSelect ps = new PlainSelect();
//        ps.setSelectItems(selitems);
//        Table table = new Table(getResults().peek().getName());
//        table.setAlias(subselect.getAlias());
//        ps.setFromItem(table);
//        ps.setWhere(exp);
//        tempTable.setSelectBody(ps);
//        this.results.add(tempTable);

        // New implementation
        SQLTemporaryTable tempTable = new SQLTemporaryTable();
        tempTable.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        tempTable.setSelectBody(plainSelect);
        this.results.add(tempTable);
    }

    /*
     * SELECT selitems FROM asc WHERE exp.
     */
    private void visitBasicAssociationClass(PlainSelect plainSelect) throws Exception {
        /*
         * Gathering components
         */
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Table asc = (Table) plainSelect.getFromItem();
        Expression whereExp = plainSelect.getWhere();
        Entity entity = dataModel.getEntities().get(asc.getName());
        /*
         * Getting the information of ends of the association class
         */
        List<Table> end_tables = new ArrayList<Table>();
        List<String> ends = new ArrayList<String>();
        for (End_AssociationClass end_asc : entity.getEnd_acs()) {
            end_tables.add(new Table(end_asc.getTargetClass()));
            ends.add(end_asc.getName());
        }
        // TODO: Generalize for n-ary case
        // For now let's focus on binary
        Table left = end_tables.get(0);
        Table right = end_tables.get(1);
        String leftEnd = ends.get(0);
        String rightEnd = ends.get(1);

        /*
         * Clause 3.a.
         * If there is no WHERE expression, then there is no need for the first
         * temporary
         * table. In case there is, SELECT * FROM c WHERE SecExp(S,exp)
         */
        if (whereExp != null) {
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            ps1.setSelectItems(Arrays.asList(new AllColumns()));
            ps1.setFromItem(asc);
            ps1.setWhere(SecExp(whereExp, asc));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);
        }

        /*
         * Clause 3.b.
         * The next temporary table: In case there is no exp, then it is SELECT
         * SecExpList(S,selitems) FROM c Otherwise SELECT SecExpList(S,selitems) FROM
         * [The previous temp table]
         */
        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        ps2.setSelectItems(SecExpList(selitems, asc));
        if (whereExp != null) {
            ps2.setFromItem(new Table(getResults().peek().getName()));
        } else {
            ps2.setFromItem(asc);
        }
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);

        /*
         * Clause 3.c.
         */
        boolean isLeftEndPropsIn = whereExp == null ? false : PropsIn(whereExp, leftEnd);
        for (SelectItem selitem : selitems) {
            if (selitem instanceof SelectExpressionItem) {
                SelectExpressionItem sei = (SelectExpressionItem) selitem;
                isLeftEndPropsIn = isLeftEndPropsIn || PropsIn(sei.getExpression(), leftEnd);
            }
        }
        boolean isRightEndPropsIn = whereExp == null ? false : PropsIn(whereExp, rightEnd);
        for (SelectItem selitem : selitems) {
            if (selitem instanceof SelectExpressionItem) {
                SelectExpressionItem sei = (SelectExpressionItem) selitem;
                isRightEndPropsIn = isRightEndPropsIn || PropsIn(sei.getExpression(), rightEnd);
            }
        }
        if (isLeftEndPropsIn || isRightEndPropsIn) {
            SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
            tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            Expression leftIDExp = new Column(
                    String.format("%s_id", left.getName()));
            leftSelectExpressionItem.setExpression(leftIDExp);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            Expression rightIDExp = new Column(String.format("%s_id", right
                    .getName()));
            rightSelectExpressionItem.setExpression(rightIDExp);
            ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
            ps1.setFromItem(left);
            Join join = new Join();
            join.setRightItem(right);
            join.setSimple(true);
            ps1.setJoins(Arrays.asList(join));
            boolean isAttributePropsIn = whereExp == null ? false
                    : PropsIn(whereExp, String.format("%s_id", asc.getName()));
            for (Attribute at : entity.getAttributes()) {
                isAttributePropsIn = isAttributePropsIn || PropsIn(whereExp, at.getName());
            }
            if (!isAttributePropsIn) {
                // I built a pseudo association
                End leftE = new End();
                leftE.setOpp(leftEnd);
                End rightE = new End();
                rightE.setOpp(rightEnd);
                Association pseudoAssoc = new Association(asc.getName(), leftE, rightE);
                ps1.setWhere(RepExp(whereExp, pseudoAssoc, leftIDExp, rightIDExp));
            }
            tempTable3.setSelectBody(ps1);
            this.results.add(tempTable3);

            SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
            tempTable4.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps3 = new PlainSelect();
            Expression exp = SecExpAssocClass(new Column(leftEnd), asc);
            SelectExpressionItem leftSelectItem = new SelectExpressionItem(exp);
            leftSelectItem.setAlias(new Alias("res"));
            ps3.addSelectItems(leftSelectItem);
            ps3.setFromItem(new Table(getResults().peek().getName()));
            tempTable4.setSelectBody(ps3);
            this.results.add(tempTable4);
        }

        /*
         * Clause 3.d.
         */
        boolean isAttPropsIn = false;
        isAttPropsIn = isAttPropsIn || whereExp == null ? false
                : PropsIn(whereExp, String.format("%s_id", asc.getName()));
        for (SelectItem selitem : selitems) {
            if (selitem instanceof SelectExpressionItem) {
                SelectExpressionItem sei = (SelectExpressionItem) selitem;
                isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), String.format("%s_id", asc.getName()));
            }
        }
        for (Attribute at : entity.getAttributes()) {
            isAttPropsIn = isAttPropsIn || whereExp == null ? false : PropsIn(whereExp, at.getName());
            for (SelectItem selitem : selitems) {
                if (selitem instanceof SelectExpressionItem) {
                    SelectExpressionItem sei = (SelectExpressionItem) selitem;
                    isAttPropsIn = isAttPropsIn || PropsIn(sei.getExpression(), at.getName());
                }
            }
        }
        if (isAttPropsIn) {
            if (isLeftEndPropsIn) {
                SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
                tempTable4.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps1 = new PlainSelect();
                SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                leftSelectExpressionItem.setAlias(new Alias("left_res"));
                Expression leftIDExp = new Column(
                        String.format("%s", leftEnd));
                Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                leftSelectExpressionItem.setExpression(exp);
                ps1.addSelectItems(leftSelectExpressionItem);
                ps1.setFromItem(asc);
                ps1.setWhere(whereExp);
                tempTable4.setSelectBody(ps1);
                this.results.add(tempTable4);
            }
            if (isRightEndPropsIn) {
                SQLTemporaryTable tempTable5 = new SQLTemporaryTable();
                tempTable5.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
                PlainSelect ps1 = new PlainSelect();
                SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
                leftSelectExpressionItem.setAlias(new Alias("right_res"));
                Expression leftIDExp = new Column(
                        String.format("%s", rightEnd));
                Expression exp = SecExpAssocClassEnd(leftIDExp, asc);
                leftSelectExpressionItem.setExpression(exp);
                ps1.addSelectItems(leftSelectExpressionItem);
                ps1.setFromItem(asc);
                ps1.setWhere(whereExp);
                tempTable5.setSelectBody(ps1);
                this.results.add(tempTable5);
            }
        }
    }

    /*
     * SELECT selitems FROM as WHERE exp. It is worth noticing that selitems and exp
     * can only contains the association ends of as and literals.
     */
    private void visitBasicAssociation(PlainSelect plainSelect) throws Exception {
        /*
         * Gathering components
         */
//        List<SelectItem> selitems = plainSelect.getSelectItems();
        Table as = (Table) plainSelect.getFromItem();
        Expression exp = plainSelect.getWhere();
        Association association = SQLSIUtils.getAssociation(dataModel, as.getName());
        /*
         * Getting the information of two ends of the association
         */
        Table left = new Table(association.getLeftEnd().getCurrentClass());
        Table right = new Table(association.getRightEnd().getCurrentClass());
        String leftEnd = association.getLeftEnd().getOpp();
        String rightEnd = association.getRightEnd().getOpp();
        /*
         * SELECT left_id as left-end, right_id as right-end FROM left, right WHERE exp;
         */
        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps1 = new PlainSelect();
        SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
        leftSelectExpressionItem.setAlias(new Alias(leftEnd));
        Expression leftIDExp = new Column(String.format("%s_id", left.getName()));
        leftSelectExpressionItem.setExpression(leftIDExp);
        SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
        rightSelectExpressionItem.setAlias(new Alias(rightEnd));
        Expression rightIDExp = new Column(String.format("%s_id", right.getName()));
        rightSelectExpressionItem.setExpression(rightIDExp);
        ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
        ps1.setFromItem(left);
        Join join = new Join();
        join.setRightItem(right);
        join.setSimple(true);
        ps1.setJoins(Arrays.asList(join));
        ps1.setWhere(RepExp(exp, association, leftIDExp, rightIDExp));
        tempTable1.setSelectBody(ps1);
        this.results.add(tempTable1);

        /*
         * SELECT SecExp(left) FROM [previous temp table]
         */
        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        Expression leftExp = SecExp(new Column(association.getLeftEnd().getOpp()), as);
        SelectExpressionItem leftSelectItem = new SelectExpressionItem(leftExp);
        leftSelectItem.setAlias(new Alias(association.getLeftEnd().getOpp()));
        ps2.addSelectItems(leftSelectItem);
        // Old implementation EXISTS (...)
//        ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
        ps2.setFromItem(new Table(getResults().peek().getName()));
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);

        // Old implementation
//        SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//        tempTable3.setName(
//            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//        PlainSelect ps3 = new PlainSelect();
//        ps3.setSelectItems(selitems);
//        ps3.setFromItem(new Table(getResults().peek().getName()));
//        EqualsTo linkEq1 = new EqualsTo();
//        linkEq1.setLeftExpression(new Column("link"));
//        linkEq1.setRightExpression(new LongValue(1L));
//        ps3.setWhere(linkEq1);
//        tempTable3.setSelectBody(ps3);
//        this.results.add(tempTable3);

        // New implementation
        /*
         * Back to the original one SELECT selitems FROM as WHERE exp
         */
        SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
        tempTable3.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        tempTable3.setSelectBody(plainSelect);
        this.results.add(tempTable3);
    }

    @SuppressWarnings("unused")
    private SelectItem generateExistsFunction(String leftEnd, String rightEnd, Table as) {
        SelectExpressionItem exists = new SelectExpressionItem();
        exists.setAlias(new Alias("link"));
        SubSelect existsSubSelect = new SubSelect();
        PlainSelect existsPlainSelect = new PlainSelect();
        existsPlainSelect.setSelectItems(Arrays.asList(new AllColumns()));
        existsPlainSelect.setFromItem(as);
        EqualsTo leftEqExp = new EqualsTo();
        leftEqExp.setLeftExpression(new Column(new Table(getResults().peek().getName()), leftEnd));
        leftEqExp.setRightExpression(new Column(as, leftEnd));
        EqualsTo rightEqExp = new EqualsTo();
        rightEqExp.setLeftExpression(new Column(new Table(getResults().peek().getName()), rightEnd));
        rightEqExp.setRightExpression(new Column(as, rightEnd));
        AndExpression andExpression = new AndExpression(leftEqExp, rightEqExp);
        existsPlainSelect.setWhere(andExpression);
        existsSubSelect.setSelectBody(existsPlainSelect);
        ExistsExpression existsExpression = new ExistsExpression();
        existsExpression.setRightExpression(existsSubSelect);
        exists.setExpression(existsExpression);
        return exists;
    }

    private Expression RepExp(Expression exp, Association association, Expression leftExp, Expression rightExp) {
        if (exp == null)
            return null;
        RepExpInjector repExpInjector = new RepExpInjector();
        repExpInjector.setAssociation(association);
        repExpInjector.setLeftExp(leftExp);
        repExpInjector.setRightExp(rightExp);
        repExpInjector.setDataModel(dataModel);
        exp.accept(repExpInjector);
        return repExpInjector.getResult();
    }

    /*
     * SELECT selitems FROM c WHERE exp;
     */
    private void visitBasicClass(PlainSelect plainSelect) throws Exception {
        /* Getting all the components */
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Table c = (Table) plainSelect.getFromItem();
        Expression exp = plainSelect.getWhere();

        /*
         * If there is no expression, then there is no need for the first temporary
         * table. In case there is, SELECT * FROM c WHERE SecExp(S,exp)
         */
        if (exp != null) {
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            ps1.setSelectItems(Arrays.asList(new AllColumns()));
            ps1.setFromItem(c);
            ps1.setWhere(SecExp(exp, c));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);
        }

        /*
         * The next temporary table: In case there is no exp, then it is SELECT
         * SecExpList(S,selitems) FROM c Otherwise SELECT SecExpList(S,selitems) FROM
         * [The previous temp table]
         */
        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        ps2.setSelectItems(SecExpList(selitems, c));
        if (exp != null) {
            ps2.setFromItem(new Table(getResults().peek().getName()));
        } else {
            ps2.setFromItem(c);
        }
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);
    }

    private List<SelectItem> SecExpList(List<SelectItem> selitems, FromItem fromItem) throws Exception {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (SelectItem si : selitems) {
            if (si instanceof SelectExpressionItem) {
                SelectExpressionItem oldSelectItem = (SelectExpressionItem) si;
                SelectExpressionItem newSelectItem = new SelectExpressionItem();
                newSelectItem.setExpression(SecExp(oldSelectItem.getExpression(), fromItem));
                newSelectItem.setAlias(gettingAlias(oldSelectItem));
                selectItems.add(newSelectItem);
            } else if (si instanceof AllColumns) {
                throw new Exception("SelectItem has no support for * (AllColumns)");
            } else if (si instanceof AllTableColumns) {
                throw new Exception("SelectItem has no support for * (AllTableColumns)");
            } else {
                throw new Exception("SelectItem has no support for undefined class type");
            }
        }
        return selectItems;
    }

    private List<SelectItem> SecExpAttList(List<SelectItem> selitems, FromItem fromItem) throws Exception {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (SelectItem si : selitems) {
            if (si instanceof SelectExpressionItem) {
                SelectExpressionItem oldSelectItem = (SelectExpressionItem) si;
                SelectExpressionItem newSelectItem = new SelectExpressionItem();
                newSelectItem.setExpression(SecExpAtt(oldSelectItem.getExpression(), fromItem));
                newSelectItem.setAlias(gettingAlias(oldSelectItem));
                selectItems.add(newSelectItem);
            } else if (si instanceof AllColumns) {
                throw new Exception("SelectItem has no support for * (AllColumns)");
            } else if (si instanceof AllTableColumns) {
                throw new Exception("SelectItem has no support for * (AllTableColumns)");
            } else {
                throw new Exception("SelectItem has no support for undefined class type");
            }
        }
        return selectItems;
    }

    private Alias gettingAlias(SelectExpressionItem oldSelectItem) {
        if (oldSelectItem.getAlias() != null) {
            return oldSelectItem.getAlias();
        } else {
            Expression exp = oldSelectItem.getExpression();
            if (exp instanceof Column) {
                return new Alias(((Column) exp).getColumnName());
            } else {
                // TODO: Adding handling alias for other cases
                return null;
            }
        }
    }

    private Expression SecExp(Expression exp, FromItem fromItem) {
        if (exp == null)
            return null;
        SecExpInjector injectorExpression = new SecExpInjector();
        injectorExpression.setAction(getAction());
        injectorExpression.setParameters(getParameters());
        injectorExpression.setFunctions(getFunctions());
        injectorExpression.setDataModel(getDataModel());
        injectorExpression.setFromItem(fromItem);
        exp.accept(injectorExpression);
        return injectorExpression.getResult();
    }

    private Expression SecExpAtt(Expression exp, FromItem fromItem) {
        if (exp == null)
            return null;
        SecExpInjector injectorExpression = new SecExpInjector();
        injectorExpression.setAction(getAction());
        injectorExpression.setParameters(getParameters());
        injectorExpression.setFunctions(getFunctions());
        injectorExpression.setDataModel(getDataModel());
        injectorExpression.setFromItem(fromItem);
        exp.accept(injectorExpression);
        return injectorExpression.getResult();
    }

    private Expression SecExpAssocClass(Expression exp, FromItem fromItem) {
        SecExpInjector injectorExpression = new SecExpInjector();
        injectorExpression.setAction(getAction());
        injectorExpression.setParameters(getParameters());
        injectorExpression.setFunctions(getFunctions());
        injectorExpression.setDataModel(getDataModel());
        injectorExpression.setFromItem(fromItem);
        injectorExpression.setTargetAttribute(2);
        exp.accept(injectorExpression);
        return injectorExpression.getResult();
    }

    private Expression SecExpAssocClassEnd(Expression exp, FromItem fromItem) {
        SecExpInjector injectorExpression = new SecExpInjector();
        injectorExpression.setAction(getAction());
        injectorExpression.setParameters(getParameters());
        injectorExpression.setFunctions(getFunctions());
        injectorExpression.setDataModel(getDataModel());
        injectorExpression.setFromItem(fromItem);
        injectorExpression.setTargetAttribute(3);
        exp.accept(injectorExpression);
        return injectorExpression.getResult();
    }

    @Override
    public void visit(SetOperationList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(WithItem arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ValuesStatement arg0) {
        // TODO Auto-generated method stub

    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

}
