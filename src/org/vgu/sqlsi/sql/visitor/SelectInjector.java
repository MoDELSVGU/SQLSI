package org.vgu.sqlsi.sql.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.ocl2psql.sql.statement.select.Join;
import org.vgu.sqlsi.sql.func.SQLSIAuthFunction;
import org.vgu.sqlsi.sql.select.SelectBasicAssociation;
import org.vgu.sqlsi.sql.select.SelectBasicClass;
import org.vgu.sqlsi.sql.select.SelectBasicSub;
import org.vgu.sqlsi.sql.select.SelectJoinAssociationAndSub;
import org.vgu.sqlsi.sql.select.SelectJoinClassAndAssociation;
import org.vgu.sqlsi.sql.select.SelectJoinClassAndSub;
import org.vgu.sqlsi.sql.select.SelectJoinSubAndSub;
import org.vgu.sqlsi.sql.temptable.SQLTemporaryTable;
import org.vgu.sqlsi.utils.SQLSIUtils;

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
    private List<SQLSIAuthFunction> functions;
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

    public List<SQLSIAuthFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<SQLSIAuthFunction> functions) {
        this.functions = functions;
    }

    /*
     * public JSONArray getAuthRoles(String entity, String resource, String
     * action) { JSONArray roles = new JSONArray(); JSONArray policy =
     * this.getPolicy(); for(int i=0; i < policy.size(); i++) { JSONObject
     * entityPolicy = (JSONObject) policy.get(i);
     * 
     * if(entityPolicy.get("entity").equals(entity)) { // entity JSONArray
     * permissions = (JSONArray) entityPolicy.get("permissions"); for(int j = 0;
     * j < permissions.size(); j++) { JSONObject permission = (JSONObject)
     * permissions.get(j); for(Object actionPer : (JSONArray)
     * permission.get("actions")) { if (((String) actionPer).equals(action)) {
     * JSONArray resources = (JSONArray) permission.get("resources"); //
     * resource for(int h = 0; h < resources.size(); h++) {
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
            } else if (plainSelect instanceof SelectBasicSub) {
                visitSubSelect(plainSelect);
            } else if (plainSelect instanceof SelectJoinClassAndAssociation) {
                visitClassJoinAssociation(plainSelect);
            } else if (plainSelect instanceof SelectJoinClassAndSub) {
                visitClassJoinSubSelect(plainSelect);
            } else if (plainSelect instanceof SelectJoinAssociationAndSub) {
                visitAssociationJoinSub(plainSelect);
            } else if (plainSelect instanceof SelectJoinSubAndSub) {
                visitSubSelectJoinSubSelect(plainSelect);
            } else {
                throw new Exception(
                    "SecQuery cannot process this select statement.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void visitSubSelectJoinSubSelect(PlainSelect plainSelect) {
//        List<SelectItem> selitems = plainSelect.getSelectItems();
//        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
//        Expression exp_ = plainSelect.getWhere();
        SubSelect subselect1 = (SubSelect) plainSelect.getFromItem();
        SubSelect subselect2 = (SubSelect) plainSelect.getJoins().get(0)
            .getRightItem();

        subselect1.getSelectBody().accept(this);
//        String lastTempSubSelect1 = new String(getResults().peek().getName());

        subselect2.getSelectBody().accept(this);
//        String lastTempSubSelect2 = new String(getResults().peek().getName());

        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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

    private void visitAssociationJoinSub(PlainSelect plainSelect)
        throws Exception {
//        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
//        Expression exp_ = plainSelect.getWhere();
        Table as = (Table) plainSelect.getFromItem();
        SubSelect subselect = (SubSelect) plainSelect.getJoins().get(0)
            .getRightItem();

        Association association = SQLSIUtils.getAssociation(dataModel,
            as.getName());
        Table left = new Table(association.getLeftEntityName());
        Table right = new Table(association.getRightEntityName());
        String leftEnd = association.getLeftEnd();
        String rightEnd = association.getRightEnd();

        subselect.getSelectBody().accept(this);
//        List<SelectItem> currentSelItems1 = new ArrayList<SelectItem>();
//        currentSelItems1
//            .addAll(((PlainSelect) this.getResults().peek().getSelectBody())
//                .getSelectItems());

        if (PropsInOn(exp, leftEnd) && !PropsInOn(exp, rightEnd)) {
            Table table = new Table(getResults().peek().getName());
            table.setAlias(subselect.getAlias());
            Column col = CompWithInOn(exp, leftEnd);
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            leftSelectExpressionItem.setExpression(col);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            Expression rightExp = new Column(
                String.format("%s_id", right.getName()));
            rightSelectExpressionItem.setExpression(rightExp);
            ps1.addSelectItems(leftSelectExpressionItem,
                rightSelectExpressionItem);
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
            tempTable2.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(
                new Column(leftEnd));
            ps2.setSelectItems(
                SecExpList(Arrays.asList(leftSelectExp), as));
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
            tempTable3.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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

        } else if (PropsInOn(exp, rightEnd) && !PropsInOn(exp, leftEnd)) {
            Table table = new Table(getResults().peek().getName());
            table.setAlias(subselect.getAlias());
            Column col = CompWithInOn(exp, rightEnd);
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            Expression leftExp = new Column(
                String.format("%s_id", left.getName()));
            leftSelectExpressionItem.setExpression(leftExp);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            rightSelectExpressionItem.setExpression(col);
            ps1.addSelectItems(leftSelectExpressionItem,
                rightSelectExpressionItem);
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
            tempTable2.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(
                new Column(leftEnd));
            ps2.setSelectItems(
                SecExpList(Arrays.asList(leftSelectExp), as));
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
            tempTable3.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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
            tempTable1.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
            leftSelectExpressionItem.setAlias(new Alias(leftEnd));
            Expression leftExp = new Column(
                String.format("%s_id", left.getName()));
            leftSelectExpressionItem.setExpression(leftExp);
            SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
            rightSelectExpressionItem.setAlias(new Alias(rightEnd));
            Expression rightExp = new Column(
                String.format("%s_id", right.getName()));
            rightSelectExpressionItem.setExpression(rightExp);
            ps1.addSelectItems(leftSelectExpressionItem,
                rightSelectExpressionItem);
            ps1.setFromItem(left);
            Join join = new Join();
            join.setRightItem(right);
            ps1.setWhere(RepExp(exp, association, leftExp, rightExp));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);

            SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
            tempTable2.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps2 = new PlainSelect();
            SelectExpressionItem leftSelectExp = new SelectExpressionItem(
                new Column(leftEnd));
            ps2.setSelectItems(
                SecExpList(Arrays.asList(leftSelectExp), as));
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
            tempTable3.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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

    private boolean PropsInOn(Expression exp, String leftEnd) {
        PropsInInjector propsInInjector = new PropsInInjector();
        propsInInjector.setPropName(leftEnd);
        exp.accept(propsInInjector);
        return propsInInjector.getIsPropsIn();
    }

    private void visitClassJoinSubSelect(PlainSelect plainSelect)
        throws Exception {
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
        Expression exp_ = plainSelect.getWhere();
        Table c = (Table) plainSelect.getFromItem();
        SubSelect subselect = (SubSelect) plainSelect.getJoins().get(0)
            .getRightItem();

        subselect.getSelectBody().accept(this);

        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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
        tempTable2.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        ps2.setSelectItems(Arrays.asList(new AllColumns()));
        ps2.setFromItem(new Table(getResults().peek().getName()));
        ps2.setWhere(SecExp(exp_, c));
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);

        SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
        tempTable3.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps3 = new PlainSelect();
        ps3.setSelectItems(SecExpList(selitems, c));
        ps3.setFromItem(new Table(getResults().peek().getName()));
        tempTable3.setSelectBody(ps3);
        this.results.add(tempTable3);
    }

    private void visitClassJoinAssociation(PlainSelect plainSelect)
        throws Exception {
        List<SelectItem> selitems = plainSelect.getSelectItems();
        Expression exp = plainSelect.getJoins().get(0).getOnExpression();
        Expression exp_ = plainSelect.getWhere();
        Table c = (Table) plainSelect.getFromItem();
        Table as = (Table) plainSelect.getJoins().get(0).getRightItem();

        Association association = SQLSIUtils.getAssociation(dataModel,
            as.getName());
        Table left = new Table(association.getLeftEntityName());
        Table right = new Table(association.getRightEntityName());
        String leftEnd = association.getLeftEnd();
        String rightEnd = association.getRightEnd();

        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps1 = new PlainSelect();
        SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
        leftSelectExpressionItem.setAlias(new Alias(leftEnd));
        leftSelectExpressionItem
            .setExpression(new Column(String.format("%s_id", left.getName())));
        SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
        rightSelectExpressionItem.setAlias(new Alias(rightEnd));
        rightSelectExpressionItem
            .setExpression(new Column(String.format("%s_id", right.getName())));
        ps1.addSelectItems(leftSelectExpressionItem, rightSelectExpressionItem);
        ps1.setFromItem(left);
        Join join = new Join();
        join.setRightItem(right);
        join.setSimple(true);
        ps1.setJoins(Arrays.asList(join));
        tempTable1.setSelectBody(ps1);
        this.results.add(tempTable1);

        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        SelectExpressionItem leftSelectExp = new SelectExpressionItem(
            new Column(leftEnd));
        ps2.setSelectItems(
            SecExpList(Arrays.asList(leftSelectExp), as));
        // Old implementation
//        ps2.addSelectItems(generateExistsFunction(leftEnd, rightEnd, as));
        ps2.setFromItem(new Table(getResults().peek().getName()));
        tempTable2.setSelectBody(ps2);
        this.results.add(tempTable2);

        // Old implementation
//        SQLTemporaryTable tempTable3 = new SQLTemporaryTable();
//        tempTable3.setName(
//            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
//        PlainSelect ps3 = new PlainSelect();
//        ps3.setSelectItems(Arrays.asList(leftSelectExp, rightSelectExp));
//        ps3.setFromItem(new Table(getResults().peek().getName()));
//        EqualsTo linkEq1 = new EqualsTo();
//        linkEq1.setLeftExpression(new Column("link"));
//        linkEq1.setRightExpression(new LongValue(1L));
//        ps3.setWhere(linkEq1);
//        tempTable3.setSelectBody(ps3);
//        this.results.add(tempTable3);

        SQLTemporaryTable tempTable4 = new SQLTemporaryTable();
        tempTable4.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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
        tempTable5.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps5 = new PlainSelect();
        ps5.setSelectItems(Arrays.asList(new AllColumns()));
        ps5.setFromItem(new Table(getResults().peek().getName()));
        ps5.setWhere(SecExp(exp_, c));
        tempTable5.setSelectBody(ps5);
        this.results.add(tempTable5);

        SQLTemporaryTable tempTable6 = new SQLTemporaryTable();
        tempTable6.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps6 = new PlainSelect();
        ps6.setSelectItems(SecExpList(selitems, c));
        ps6.setFromItem(new Table(getResults().peek().getName()));
        tempTable6.setSelectBody(ps6);
        this.results.add(tempTable6);
    }

    /*
     * SELECT selitems FROM subselect WHERE exp. It is worth noticing that
     * selitems and exp can only contains the expression in the subselect or
     * literal
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
        tempTable.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        tempTable.setSelectBody(plainSelect);
        this.results.add(tempTable);
    }

    /*
     * SELECT selitems FROM as WHERE exp. It is worth noticing that selitems and
     * exp can only contains the association ends of as and literals.
     */
    private void visitBasicAssociation(PlainSelect plainSelect)
        throws Exception {
        /*
         * Gathering components
         */
//        List<SelectItem> selitems = plainSelect.getSelectItems();
        Table as = (Table) plainSelect.getFromItem();
        Expression exp = plainSelect.getWhere();
        Association association = SQLSIUtils.getAssociation(dataModel,
            as.getName());
        /*
         * Getting the information of two ends of the association
         */
        Table left = new Table(association.getLeftEntityName());
        Table right = new Table(association.getRightEntityName());
        String leftEnd = association.getLeftEnd();
        String rightEnd = association.getRightEnd();
        /*
         * SELECT left_id as left-end, right_id as right-end FROM left, right
         * WHERE exp;
         */
        SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
        tempTable1.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps1 = new PlainSelect();
        SelectExpressionItem leftSelectExpressionItem = new SelectExpressionItem();
        leftSelectExpressionItem.setAlias(new Alias(leftEnd));
        Expression leftIDExp = new Column(
            String.format("%s_id", left.getName()));
        leftSelectExpressionItem.setExpression(leftIDExp);
        SelectExpressionItem rightSelectExpressionItem = new SelectExpressionItem();
        rightSelectExpressionItem.setAlias(new Alias(rightEnd));
        Expression rightIDExp = new Column(
            String.format("%s_id", right.getName()));
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
        tempTable2.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        PlainSelect ps2 = new PlainSelect();
        Expression leftExp = SecExp(new Column(association.getLeftEnd()), as);
        SelectExpressionItem leftSelectItem = new SelectExpressionItem(leftExp);
        leftSelectItem.setAlias(new Alias(association.getLeftEnd()));
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
        tempTable3.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
        tempTable3.setSelectBody(plainSelect);
        this.results.add(tempTable3);

    }

    @SuppressWarnings("unused")
    private SelectItem generateExistsFunction(String leftEnd, String rightEnd,
        Table as) {
        SelectExpressionItem exists = new SelectExpressionItem();
        exists.setAlias(new Alias("link"));
        SubSelect existsSubSelect = new SubSelect();
        PlainSelect existsPlainSelect = new PlainSelect();
        existsPlainSelect.setSelectItems(Arrays.asList(new AllColumns()));
        existsPlainSelect.setFromItem(as);
        EqualsTo leftEqExp = new EqualsTo();
        leftEqExp.setLeftExpression(
            new Column(new Table(getResults().peek().getName()), leftEnd));
        leftEqExp.setRightExpression(new Column(as, leftEnd));
        EqualsTo rightEqExp = new EqualsTo();
        rightEqExp.setLeftExpression(
            new Column(new Table(getResults().peek().getName()), rightEnd));
        rightEqExp.setRightExpression(new Column(as, rightEnd));
        AndExpression andExpression = new AndExpression(leftEqExp, rightEqExp);
        existsPlainSelect.setWhere(andExpression);
        existsSubSelect.setSelectBody(existsPlainSelect);
        ExistsExpression existsExpression = new ExistsExpression();
        existsExpression.setRightExpression(existsSubSelect);
        exists.setExpression(existsExpression);
        return exists;
    }

    private Expression RepExp(Expression exp, Association association,
        Expression leftExp, Expression rightExp) {
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
         * If there is no expression, then there is no need for the first
         * temporary table. In case there is, SELECT * FROM c WHERE
         * SecExp(S,exp)
         */
        if (exp != null) {
            SQLTemporaryTable tempTable1 = new SQLTemporaryTable();
            tempTable1.setName(
                String.format("TEMP%s", String.valueOf(tempTableNumber++)));
            PlainSelect ps1 = new PlainSelect();
            ps1.setSelectItems(Arrays.asList(new AllColumns()));
            ps1.setFromItem(c);
            ps1.setWhere(SecExp(exp, c));
            tempTable1.setSelectBody(ps1);
            this.results.add(tempTable1);
        }

        /*
         * The next temporary table: In case there is no exp, then it is SELECT
         * SecExpList(S,selitems) FROM c Otherwise SELECT SecExpList(S,selitems)
         * FROM [The previous temp table]
         */
        SQLTemporaryTable tempTable2 = new SQLTemporaryTable();
        tempTable2.setName(
            String.format("TEMP%s", String.valueOf(tempTableNumber++)));
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

    private List<SelectItem> SecExpList(List<SelectItem> selitems,
        FromItem fromItem) throws Exception {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (SelectItem si : selitems) {
            if (si instanceof SelectExpressionItem) {
                SelectExpressionItem oldSelectItem = (SelectExpressionItem) si;
                SelectExpressionItem newSelectItem = new SelectExpressionItem();
                newSelectItem.setExpression(
                    SecExp(oldSelectItem.getExpression(), fromItem));
                newSelectItem.setAlias(gettingAlias(oldSelectItem));
                selectItems.add(newSelectItem);
            } else if (si instanceof AllColumns) {
                throw new Exception(
                    "SelectItem has no support for * (AllColumns)");
            } else if (si instanceof AllTableColumns) {
                throw new Exception(
                    "SelectItem has no support for * (AllTableColumns)");
            } else {
                throw new Exception(
                    "SelectItem has no support for undefined class type");
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
