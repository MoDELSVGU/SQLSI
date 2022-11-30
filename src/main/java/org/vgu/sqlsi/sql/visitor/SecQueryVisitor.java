package org.vgu.sqlsi.sql.visitor;

import java.util.List;
import java.util.Stack;
import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.DeclareStatement;
import net.sf.jsqlparser.statement.DescribeStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.ShowStatement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.json.simple.JSONArray;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.sqlsi.sql.func.AuthFunc;
import org.vgu.sqlsi.sql.temptable.SQLTemporaryTable;

public class SecQueryVisitor implements StatementVisitor {
  private DataModel dataModel;

  public DataModel getDataModel() {
    return dataModel;
  }

  public void setDataModel(DataModel dataModel) {
    this.dataModel = dataModel;
  }

  private List<AuthFunc> functions;
  private JSONArray parameters;
  private Stack<SQLTemporaryTable> result = new Stack<SQLTemporaryTable>();

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

  public Stack<SQLTemporaryTable> getResult() {
    return result;
  }

  public void setResult(Stack<SQLTemporaryTable> result) {
    this.result = result;
  }

  @Override
  public void visit(Commit commit) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Delete delete) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Update update) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Insert insert) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Replace replace) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Drop drop) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Truncate truncate) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CreateIndex createIndex) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CreateTable createTable) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(CreateView createView) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(AlterView alterView) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Alter alter) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Statements stmts) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Execute execute) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(SetStatement set) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Merge merge) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Select select) {
    SelectInjector injector = new SelectInjector();
    injector.setAction("READ");
    injector.setFunctions(functions);
    injector.setParameters(this.getParameters());
    injector.setDataModel(dataModel);
    SelectBody selectBody = select.getSelectBody();
    selectBody.accept(injector);
    this.getResult().addAll(injector.getResults());
  }

  @Override
  public void visit(Upsert upsert) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(UseStatement use) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Comment arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ShowColumnsStatement arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(Block arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ValuesStatement arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(DescribeStatement arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ExplainStatement arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(ShowStatement arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(DeclareStatement aThis) {
    // TODO Auto-generated method stub

  }
}
