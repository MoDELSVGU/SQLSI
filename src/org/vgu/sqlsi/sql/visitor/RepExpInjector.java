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

package org.vgu.sqlsi.sql.visitor;

import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.DataModel;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.SimilarToExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class RepExpInjector implements ExpressionVisitor {

    private Association association;
    private Expression result;
    private DataModel dataModel;
    private Expression leftExp;
    private Expression rightExp;

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public void visit(BitwiseRightShift aThis) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseLeftShift aThis) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NullValue nullValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Function function) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SignedExpression signedExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DoubleValue doubleValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LongValue longValue) {
        this.setResult(longValue);

    }

    @Override
    public void visit(HexValue hexValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DateValue dateValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimeValue timeValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimestampValue timestampValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Parenthesis parenthesis) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(StringValue stringValue) {
        this.setResult(stringValue);

    }

    @Override
    public void visit(Addition addition) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Division division) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Multiplication multiplication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Subtraction subtraction) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AndExpression andExpression) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        andExpression.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        andExpression.getRightExpression().accept(rightRepExpInjector);
        AndExpression newAndExpression = new AndExpression(
            leftRepExpInjector.getResult(), rightRepExpInjector.getResult());
        this.setResult(newAndExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Between between) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        equalsTo.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        equalsTo.getRightExpression().accept(rightRepExpInjector);
        EqualsTo newExpression = new EqualsTo();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        greaterThan.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        greaterThan.getRightExpression().accept(rightRepExpInjector);
        GreaterThan newExpression = new GreaterThan();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        greaterThanEquals.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        greaterThanEquals.getRightExpression().accept(rightRepExpInjector);
        GreaterThanEquals newExpression = new GreaterThanEquals();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(InExpression inExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        isNullExpression.getLeftExpression().accept(leftRepExpInjector);
        IsNullExpression newExpression = new IsNullExpression();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MinorThan minorThan) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        minorThan.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        minorThan.getRightExpression().accept(rightRepExpInjector);
        MinorThan newExpression = new MinorThan();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        minorThanEquals.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        minorThanEquals.getRightExpression().accept(rightRepExpInjector);
        MinorThanEquals newExpression = new MinorThanEquals();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        notEqualsTo.getLeftExpression().accept(leftRepExpInjector);
        RepExpInjector rightRepExpInjector = new RepExpInjector();
        rightRepExpInjector.setAssociation(getAssociation());
        rightRepExpInjector.setLeftExp(getLeftExp());
        rightRepExpInjector.setRightExp(getRightExp());
        rightRepExpInjector.setDataModel(getDataModel());
        notEqualsTo.getRightExpression().accept(rightRepExpInjector);
        NotEqualsTo newExpression = new NotEqualsTo();
        newExpression.setLeftExpression(leftRepExpInjector.getResult());
        newExpression.setRightExpression(rightRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(Column tableColumn) {
        String columnName = tableColumn.getColumnName();
        if(columnName.equalsIgnoreCase(association.getLeftEnd())) {
            this.setResult(getLeftExp());
        } else if(columnName.equalsIgnoreCase(association.getRightEnd())) {
            this.setResult(getRightExp());
        } else {
            this.setResult(tableColumn);
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CaseExpression caseExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(WhenClause whenClause) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Concat concat) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Matches matches) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CastExpression cast) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Modulo modulo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExtractExpression eexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IntervalExpression iexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JsonOperator jsonExpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(UserVariable var) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NumericBind bind) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(KeepExpression aexpr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ValueListExpression valueList) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(OracleHint hint) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NotExpression aThis) {
        RepExpInjector leftRepExpInjector = new RepExpInjector();
        leftRepExpInjector.setAssociation(getAssociation());
        leftRepExpInjector.setLeftExp(getLeftExp());
        leftRepExpInjector.setRightExp(getRightExp());
        leftRepExpInjector.setDataModel(getDataModel());
        aThis.getExpression().accept(leftRepExpInjector);
        NotExpression newExpression = new NotExpression(leftRepExpInjector.getResult());
        this.setResult(newExpression);
    }

    @Override
    public void visit(NextValExpression aThis) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CollateExpression aThis) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SimilarToExpression aThis) {
        // TODO Auto-generated method stub

    }

    public Expression getResult() {
        return result;
    }

    public void setResult(Expression result) {
        this.result = result;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Expression getLeftExp() {
        return leftExp;
    }

    public void setLeftExp(Expression leftExp) {
        this.leftExp = leftExp;
    }

    public Expression getRightExp() {
        return rightExp;
    }

    public void setRightExp(Expression rightExp) {
        this.rightExp = rightExp;
    }

}
