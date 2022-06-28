package org.vgu.sqlsi.sql.visitor;

import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.vgu.dm2schema.dm.Association;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.sqlsi.sql.func.AuthFunc;
import org.vgu.sqlsi.utils.SQLSIUtils;

import main.SQLSIConfiguration;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayExpression;
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
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
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
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SubSelect;

public class SecExpInjector implements ExpressionVisitor {
	private JSONArray parameters;
	private String action;
	private Expression result;
	private List<AuthFunc> functions;
	private DataModel dataModel;
	private FromItem fromItem;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Expression getResult() {
		return result;
	}

	public void setResult(Expression result) {
		this.result = result;
	}

	public JSONArray getParameters() {
		return parameters;
	}

	public void setParameters(JSONArray parameters) {
		this.parameters = parameters;
	}

	public List<AuthFunc> getFunctions() {
		return functions;
	}

	public void setFunctions(List<AuthFunc> functions) {
		this.functions = functions;
	}

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
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		andExpression.getLeftExpression().accept(leftInjectorExpression);
		andExpression.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		andExpression.getRightExpression().accept(rightInjectorExpression);
		andExpression.setRightExpression(leftInjectorExpression.getResult());

		this.setResult(andExpression);
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
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		equalsTo.getLeftExpression().accept(leftInjectorExpression);
		equalsTo.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		equalsTo.getRightExpression().accept(rightInjectorExpression);
		equalsTo.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(equalsTo);
	}

	@Override
	public void visit(GreaterThan greaterThan) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		greaterThan.getLeftExpression().accept(leftInjectorExpression);
		greaterThan.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		greaterThan.getRightExpression().accept(rightInjectorExpression);
		greaterThan.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(greaterThan);
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		greaterThanEquals.getLeftExpression().accept(leftInjectorExpression);
		greaterThanEquals.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		greaterThanEquals.getRightExpression().accept(rightInjectorExpression);
		greaterThanEquals.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(greaterThanEquals);
	}

	@Override
	public void visit(InExpression inExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IsNullExpression isNullExpression) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		isNullExpression.getLeftExpression().accept(leftInjectorExpression);
		isNullExpression.setLeftExpression(leftInjectorExpression.getResult());

		this.setResult(isNullExpression);

	}

	@Override
	public void visit(LikeExpression likeExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MinorThan minorThan) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		minorThan.getLeftExpression().accept(leftInjectorExpression);
		minorThan.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		minorThan.getRightExpression().accept(rightInjectorExpression);
		minorThan.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(minorThan);

	}

	@Override
	public void visit(MinorThanEquals minorThanEquals) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		minorThanEquals.getLeftExpression().accept(leftInjectorExpression);
		minorThanEquals.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		minorThanEquals.getRightExpression().accept(rightInjectorExpression);
		minorThanEquals.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(minorThanEquals);
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		notEqualsTo.getLeftExpression().accept(leftInjectorExpression);
		notEqualsTo.setLeftExpression(leftInjectorExpression.getResult());

		SecExpInjector rightInjectorExpression = new SecExpInjector();
		rightInjectorExpression.setAction(getAction());
		rightInjectorExpression.setDataModel(getDataModel());
		rightInjectorExpression.setFunctions(getFunctions());
		rightInjectorExpression.setFromItem(getFromItem());
		rightInjectorExpression.setParameters(getParameters());

		notEqualsTo.getRightExpression().accept(rightInjectorExpression);
		notEqualsTo.setRightExpression(rightInjectorExpression.getResult());

		this.setResult(notEqualsTo);
	}

	@Override
	public void visit(Column column) {
		String columnName = column.getColumnName();
		Table table = (Table) column.getTable();
		String tableName = null;
		if (table == null) {
			if (getFromItem() instanceof Table) {
				tableName = ((Table) getFromItem()).getName();
			} else {
				// TODO: Implement other cases.
			}
		} else {
			tableName = table.getName();
		}
		if (SQLSIUtils.isPrimaryKey(dataModel, columnName, tableName)) {
			// do nothing, because primary key is HIDDEN
			// from the eyes of the security modeler
			this.setResult(column);
		} else if (SQLSIUtils.isParameter(this.getParameters(), columnName)) {
			// TODO: What is this parameter?
			this.setResult(column);
		} else if (SQLSIUtils.isAttribute(dataModel, tableName, columnName)) {
			CaseExpression siCase = new CaseExpression();
			Function function = new Function();
			try {
				AuthFunc secFunction = SQLSIUtils.findAuthFunctionAttribute(functions, columnName, tableName);
				function.setName(secFunction.getFunctionName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExpressionList expList = new ExpressionList(Arrays.asList(new Column(SQLSIConfiguration.CALLER),
					new Column(SQLSIConfiguration.ROLE), new Column(String.format("%s_id", tableName))));
			function.setParameters(expList);
			siCase.setSwitchExpression(function);
			WhenClause whenClause = new WhenClause();
			whenClause.setWhenExpression(new LongValue("TRUE"));
			whenClause.setThenExpression(column);
			siCase.setWhenClauses(Arrays.asList(whenClause));
			Function throwError = new Function();
			throwError.setName("throw_error");
			siCase.setElseExpression(throwError);
			this.setResult(siCase);
		} else if (SQLSIUtils.isAssociationEnd(dataModel, tableName, columnName)) {
			CaseExpression siCase = new CaseExpression();
			Function function = new Function();
			// TODO: Implement this!
			try {
				AuthFunc secFunction = SQLSIUtils.findAuthFunctionAssociation(dataModel, functions, columnName,
						tableName);
				function.setName(secFunction.getFunctionName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Association association = SQLSIUtils.getAssociation(dataModel, tableName);
			ExpressionList expList = new ExpressionList(
					Arrays.asList(new Column(SQLSIConfiguration.CALLER), new Column(SQLSIConfiguration.ROLE),
							new Column(association.getLeftEnd()), new Column(association.getRightEnd())));
			function.setParameters(expList);
			siCase.setSwitchExpression(function);
			WhenClause whenClause = new WhenClause();
			whenClause.setWhenExpression(new LongValue("TRUE"));
			whenClause.setThenExpression(column);
			siCase.setWhenClauses(Arrays.asList(whenClause));
			Function throwError = new Function();
			throwError.setName("throw_error");
			siCase.setElseExpression(throwError);
			this.setResult(siCase);
		} else {
			this.setResult(column);
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
	public void visit(NotExpression notExpression) {
		SecExpInjector leftInjectorExpression = new SecExpInjector();
		leftInjectorExpression.setAction(getAction());
		leftInjectorExpression.setDataModel(getDataModel());
		leftInjectorExpression.setFunctions(getFunctions());
		leftInjectorExpression.setFromItem(getFromItem());
		leftInjectorExpression.setParameters(getParameters());

		notExpression.getExpression().accept(leftInjectorExpression);
		notExpression.setExpression(leftInjectorExpression.getResult());

		this.setResult(notExpression);
	}

	@Override
	public void visit(NextValExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CollateExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SimilarToExpression arg0) {
		// TODO Auto-generated method stub

	}

	public FromItem getFromItem() {
		return fromItem;
	}

	public void setFromItem(FromItem fromItem) {
		this.fromItem = fromItem;
	}

	@Override
	public void visit(IntegerDivision division) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FullTextSearch fullTextSearch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IsBooleanExpression isBooleanExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArrayExpression aThis) {
		// TODO Auto-generated method stub

	}

}
