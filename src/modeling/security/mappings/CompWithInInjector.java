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

import net.sf.jsqlparser.expression.AllValue;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayConstructor;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.ConnectByRootOperator;
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
import net.sf.jsqlparser.expression.JsonAggregateFunction;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.JsonFunction;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.OracleNamedFunctionParameter;
import net.sf.jsqlparser.expression.OverlapsCondition;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.RowGetExpression;
import net.sf.jsqlparser.expression.SafeCastExpression;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.TimezoneExpression;
import net.sf.jsqlparser.expression.TryCastExpression;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.VariableAssignment;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.XMLSerializeExpr;
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
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.GeometryDistance;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression;
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
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SubSelect;

public class CompWithInInjector implements ExpressionVisitor {

    private Column col;
    private String propName;

    public Column getCol() {
        return col;
    }

    public void setCol(Column col) {
        this.col = col;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        Expression left = andExpression.getLeftExpression();
        Expression right = andExpression.getRightExpression();
        setCol(getCol(left, right));
    }

    @Override
    public void visit(OrExpression orExpression) {
        Expression left = orExpression.getLeftExpression();
        Expression right = orExpression.getRightExpression();
        setCol(getCol(left, right));
    }

    private Column getCol(Expression left, Expression right) {
        if (left instanceof Column && right instanceof Column) {
            Column leftCol = (Column) left;
            Column rightCol = (Column) right;
            if (leftCol.getColumnName().equalsIgnoreCase(propName)) {
                return rightCol;
            }
            if (rightCol.getColumnName().equalsIgnoreCase(propName)) {
                return leftCol;
            }
        }
        return null;
    }

    @Override
    public void visit(Between between) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        Expression left = equalsTo.getLeftExpression();
        Expression right = equalsTo.getRightExpression();
        setCol(getCol(left, right));
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        Expression left = greaterThan.getLeftExpression();
        Expression right = greaterThan.getRightExpression();
        setCol(getCol(left, right));

    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        Expression left = greaterThanEquals.getLeftExpression();
        Expression right = greaterThanEquals.getRightExpression();
        setCol(getCol(left, right));

    }

    @Override
    public void visit(InExpression inExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MinorThan minorThan) {
        Expression left = minorThan.getLeftExpression();
        Expression right = minorThan.getRightExpression();
        setCol(getCol(left, right));

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        Expression left = minorThanEquals.getLeftExpression();
        Expression right = minorThanEquals.getRightExpression();
        setCol(getCol(left, right));

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        Expression left = notEqualsTo.getLeftExpression();
        Expression right = notEqualsTo.getRightExpression();
        setCol(getCol(left, right));

    }

    @Override
    public void visit(Column tableColumn) {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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

	@Override
	public void visit(XorExpression orExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OverlapsCondition overlapsCondition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TryCastExpression cast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SafeCastExpression cast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RowGetExpression rowGetExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayConstructor aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VariableAssignment aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(XMLSerializeExpr aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimezoneExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JsonAggregateFunction aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JsonFunction aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ConnectByRootOperator aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OracleNamedFunctionParameter aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllColumns allColumns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllValue allValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IsDistinctExpression isDistinctExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(GeometryDistance geometryDistance) {
		// TODO Auto-generated method stub
		
	}
}
