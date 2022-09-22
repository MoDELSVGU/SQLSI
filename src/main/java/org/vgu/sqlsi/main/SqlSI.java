package org.vgu.sqlsi.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vgu.dm2schema.DM2Schema;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.sqlsi.sec.QueryModel;
import org.vgu.sqlsi.sec.model.SecurityModel;
import org.vgu.sqlsi.sql.func.AuthFunc;
import org.vgu.sqlsi.sql.proc.SQLSIStoredProcedure;
import org.vgu.sqlsi.sql.temptable.SQLTemporaryTable;
import org.vgu.sqlsi.sql.visitor.SecQueryVisitor;
import org.vgu.sqlsi.utils.FunctionUtils;
import org.vgu.sqlsi.utils.PrintingUtils;
import org.vgu.sqlsi.utils.SQLSIUtils;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

public class SqlSI {
  private DataModel dataModel;
  private SecurityModel securityModel;
  private List<QueryModel> queriesModel;

  public void setDataModel(String url) throws FileNotFoundException, IOException, ParseException, Exception {
    this.dataModel = transformDataModel(url);
  }

  public void setSecurityModel(String url) throws FileNotFoundException, IOException, ParseException, Exception {
    this.securityModel = transformSecurityModel(url);
  }

  public void setQueryModel(String url) throws FileNotFoundException, IOException, ParseException, Exception {
    this.queriesModel = transformQueriesModel(url);
  }

  private List<QueryModel> transformQueriesModel(String url)
      throws FileNotFoundException, IOException, ParseException {
    File queryFile = new File(url);
    JSONArray queries = (JSONArray) new JSONParser().parse(new FileReader(queryFile));

    List<QueryModel> queriesModel = new ArrayList<QueryModel>();

    for (Object object : (JSONArray) queries) {
      JSONObject jsonQuery = (JSONObject) object;
      String name = (String) jsonQuery.get("name");
      JSONArray pars = new JSONArray();
      JSONArray vars = new JSONArray();
      JSONArray body = new JSONArray();
      if (jsonQuery.containsKey("pars")) {
        pars = (JSONArray) jsonQuery.get("pars");
      }
      if (jsonQuery.containsKey("vars")) {
        vars = (JSONArray) jsonQuery.get("vars");
      }
      if (jsonQuery.containsKey("body")) {
        body = (JSONArray) jsonQuery.get("body");
      }
      for (int i = 0; i < body.size(); i++) {
        String statement = (String) body.get(i);
        QueryModel queryModel = new QueryModel();
        queryModel.setName(name);
        queryModel.setPars(pars);
        queryModel.setVars(vars);
        queryModel.setStatement(statement);
        queriesModel.add(queryModel);
      }
    }

    return queriesModel;
  }

  public void run(String queryModelURI, String schemaoutputURI, String authFuncOutputURI, String authProcOutputURI)
      throws FileNotFoundException, IOException, ParseException, Exception {
    SqlSIGenDatabase(schemaoutputURI); // db.sql
    SqlSIGenAuthFunc(authFuncOutputURI); // secVGU
    SqlSIGenSecQuery(authProcOutputURI); // secProc.sql
  }

  public void SqlSIGenSecQuery(String sqlstoredprocedureoutputuri) throws Exception {
    File secQueryFile = new File(sqlstoredprocedureoutputuri);
    FileWriter fileWriter = new FileWriter(secQueryFile);
    for (QueryModel qm : this.queriesModel) {
      Stack<SQLTemporaryTable> temps = genSecProc(qm.getStatement(), qm.getVars(), qm.getVars());
      SQLSIStoredProcedure storedProcedure = new SQLSIStoredProcedure();
      storedProcedure.setName(qm.getName());
      storedProcedure.setComments(qm.getStatement());
      storedProcedure.setTemps(temps);
      String sqlProc = PrintingUtils.printProc(storedProcedure);
      try {
        fileWriter.write(sqlProc);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    fileWriter.flush();
    fileWriter.close();
  }

  public void SqlSIGenSecQuery(String sqlstoredprocedureoutputuri, String statement) throws Exception {
    File secQueryFile = new File(sqlstoredprocedureoutputuri);
    FileWriter fileWriter = new FileWriter(secQueryFile);
    Stack<SQLTemporaryTable> temps = genSecProc(statement, null, null);
    SQLSIStoredProcedure storedProcedure = new SQLSIStoredProcedure();
    storedProcedure.setName(SQLSIConfiguration.SECQUERYNAME);
    storedProcedure.setComments(statement);
    storedProcedure.setTemps(temps);
    String sqlProc = PrintingUtils.printProc(storedProcedure);
    try {
      fileWriter.write(sqlProc);
    } catch (IOException e) {
      e.printStackTrace();
    }
    fileWriter.flush();
    fileWriter.close();
  }

  private Stack<SQLTemporaryTable> genSecProc(String statement, JSONArray vars, JSONArray pars) throws Exception {

    Statement statementSql = CCJSqlParserUtil.parse(statement);

    SQLSIUtils.classify(statementSql, dataModel);

    SecQueryVisitor secQuery = new SecQueryVisitor();
    secQuery.setFunctions(FunctionUtils.printAuthFun(dataModel, securityModel));
    secQuery.setParameters(pars);
    secQuery.setDataModel(dataModel);
    statementSql.accept(secQuery);

    return secQuery.getResult();
  }

  private SecurityModel transformSecurityModel(String securityModelURI)
      throws IOException, ParseException, FileNotFoundException {
    File policyFile = new File(securityModelURI);
    JSONArray secureUMLJSONArray = (JSONArray) new JSONParser().parse(new FileReader(policyFile));

    org.vgu.sqlsi.sec.model.SecurityModel secureUML = new org.vgu.sqlsi.sec.model.SecurityModel(secureUMLJSONArray);
    return secureUML;
  }

  private DataModel transformDataModel(String dataModelURI)
      throws IOException, ParseException, FileNotFoundException, Exception {
    File dataModelFile = new File(dataModelURI);
    JSONArray dataModelJSONArray = (JSONArray) new JSONParser().parse(new FileReader(dataModelFile));
    DataModel context = new DataModel(dataModelJSONArray);
    return context;
  }

  public void SqlSIGenDatabase(String sqlschemaoutputuri) throws IOException {
    File dbGenFile = new File(sqlschemaoutputuri);
    FileWriter fileWriter = new FileWriter(dbGenFile);
    String sqlScript = DM2Schema.generateDatabase(this.dataModel, SQLSIConfiguration.SCHEMANAME);
    try {
      fileWriter.write(sqlScript);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      fileWriter.flush();
      fileWriter.close();
    }
  }

  public List<AuthFunc> SqlSIGenAuthFunc(String sqlauthfunctionoutputuri) throws Exception {
    File funGenFile = new File(sqlauthfunctionoutputuri);
    FileWriter fileWriter = new FileWriter(funGenFile);
    String throwErrorFunc = PrintingUtils.printThrowErrorFunc();
    List<AuthFunc> functions = FunctionUtils.printAuthFun(dataModel, securityModel);
    try {
      fileWriter.write(throwErrorFunc);
      for (AuthFunc function : functions) {
        String secFunc = PrintingUtils.printAuthFunc(function);
        fileWriter.write(secFunc);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      fileWriter.flush();
      fileWriter.close();
    }
    return functions;
  }
}
