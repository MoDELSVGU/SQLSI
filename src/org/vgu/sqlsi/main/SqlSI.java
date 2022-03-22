package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vgu.dm2schema.DM2Schema;
import org.vgu.dm2schema.dm.DataModel;
import org.vgu.sqlsi.sec.SecPolicyModel;
import org.vgu.sqlsi.sec.SecurityMode;
import org.vgu.sqlsi.sql.func.SQLSIAuthFunction;
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
	private SecPolicyModel securityModel;
	private List<SQLSIAuthFunction> functions;
	private SecurityMode mode = SecurityMode.NON_TRUMAN;
	static final String SECQUERY = "SecQuery";

	public void setUpDataModelFromURL(String url) throws FileNotFoundException, IOException, ParseException, Exception {
		dataModel = transformToDataModel(url);
	}

	public void generateDBSchema(String outputUrl, String schemaName) throws IOException {
		SqlSIGenDatabase(dataModel, schemaName, outputUrl);
	}

	public void setUpSecurityModelFromURL(String url)
			throws FileNotFoundException, IOException, ParseException, Exception {
		securityModel = transformToSecurityModel(url);
		functions = FunctionUtils.printAuthFun(dataModel, securityModel, mode);
	}
	
	public void setUpSecurityModel(File url)
			throws FileNotFoundException, IOException, ParseException, Exception {
		securityModel = transformToSecurityModel(url);
		functions = FunctionUtils.printAuthFun(dataModel, securityModel, mode);
	}

	public void generateSQLAuthFunctions(String outputUrl) throws Exception {
		SqlSIGenAuthFunc(dataModel, securityModel, mode, outputUrl);
	}

	public String getSecQuery(String query) throws Exception {
		Stack<SQLTemporaryTable> temps = genSecProc(query, null, null, dataModel, functions);
		SQLSIStoredProcedure storedProcedure = new SQLSIStoredProcedure();
		storedProcedure.setName(SECQUERY);
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(String.format("%s%s", Configuration.PARAM_PREFIX, Configuration.CALLER),
				Configuration.PARAM_TYPE);
		parameters.put(String.format("%s%s", Configuration.PARAM_PREFIX, Configuration.ROLE), Configuration.PARAM_TYPE);
		storedProcedure.setComments(query);
		storedProcedure.setParameters(parameters);
		storedProcedure.setTemps(temps);
		String sqlProc = PrintingUtils.printProc(storedProcedure);
		return sqlProc;
	}

	public void generateSQLSecureQuery(String query, String outputUrl) throws Exception {
		File secQueryFile = new File(outputUrl);
		FileWriter fileWriter = new FileWriter(secQueryFile);
		try {
			fileWriter.write(getSecQuery(query));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileWriter.flush();
		fileWriter.close();
	}

	public void run(String dataModelURI, String policyModelURI, String schemaName, String queryModelURI,
			String schemaoutputURI, String authFuncOutputURI, String authProcOutputURI)
			throws FileNotFoundException, IOException, ParseException, Exception {
//    	final long dmStartNanoTime = System.nanoTime();
		DataModel dataModel = transformToDataModel(dataModelURI);
		SqlSIGenDatabase(dataModel, schemaName, schemaoutputURI);
//        final long dmEndNanoTime = System.nanoTime();
//        double dmconvert = ((double) dmEndNanoTime - dmStartNanoTime)/ 1_000_000_000;
//        System.out.println(dmconvert);
//        final long smStartNanoTime = System.nanoTime();
		SecPolicyModel securityModel = transformToSecurityModel(policyModelURI);
		List<SQLSIAuthFunction> functions = SqlSIGenAuthFunc(dataModel, securityModel, mode, authFuncOutputURI);
//        final long smEndNanoTime = System.nanoTime();
//        double smconvert = ((double) smEndNanoTime - smStartNanoTime)/ 1_000_000_000;
//        System.out.println(smconvert);
		SqlSIGenSecQuery(dataModel, functions, authProcOutputURI, queryModelURI);
	}

	private void SqlSIGenSecQuery(DataModel dataModel, List<SQLSIAuthFunction> functions,
			String sqlstoredprocedureoutputuri, String querytobeenforcedinputuri) throws Exception {
		File secQueryFile = new File(sqlstoredprocedureoutputuri);
		FileWriter fileWriter = new FileWriter(secQueryFile);

		File queryFile = new File(querytobeenforcedinputuri);
		JSONArray queries = (JSONArray) new JSONParser().parse(new FileReader(queryFile));

		for (Object object : (JSONArray) queries) {
//        	final long smStartNanoTime = System.nanoTime();
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

			Stack<SQLTemporaryTable> temps;
			for (int i = 0; i < body.size(); i++) {
				String statement = (String) body.get(i);
				temps = genSecProc(statement, vars, pars, dataModel, functions);
				SQLSIStoredProcedure storedProcedure = new SQLSIStoredProcedure();
				storedProcedure.setName(name);
				HashMap<String, String> parameters = new HashMap<String, String>();
				parameters.put(String.format("%s%s", Configuration.PARAM_PREFIX, Configuration.CALLER),
						Configuration.PARAM_TYPE);
				parameters.put(String.format("%s%s", Configuration.PARAM_PREFIX, Configuration.ROLE),
						Configuration.PARAM_TYPE);
				storedProcedure.setComments(statement);
				storedProcedure.setParameters(parameters);
				storedProcedure.setTemps(temps);
				String sqlProc = PrintingUtils.printProc(storedProcedure);
				try {
					fileWriter.write(sqlProc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//            final long smEndNanoTime = System.nanoTime();
//    		double smconvert = ((double) smEndNanoTime - smStartNanoTime) / 1_000_000_000;
//    		System.out.println(smconvert);
		}
		fileWriter.flush();
		fileWriter.close();
	}

	private Stack<SQLTemporaryTable> genSecProc(String statement, JSONArray vars, JSONArray pars, DataModel dataModel,
			List<SQLSIAuthFunction> functions) throws Exception {
		Statement statementSql = CCJSqlParserUtil.parse(statement);

		SQLSIUtils.classify(statementSql, dataModel);

		SecQueryVisitor secQuery = new SecQueryVisitor();
		secQuery.setFunctions(functions);
		secQuery.setParameters(pars);
		secQuery.setDataModel(dataModel);
		statementSql.accept(secQuery);

		return secQuery.getResult();
	}

	private SecPolicyModel transformToSecurityModel(String securityModelURI)
			throws IOException, ParseException, FileNotFoundException {
		File policyFile = new File(securityModelURI);
		JSONArray secureUMLJSONArray = (JSONArray) new JSONParser().parse(new FileReader(policyFile));
		SecPolicyModel secureUML = new SecPolicyModel(secureUMLJSONArray);
		return secureUML;
	}

	public SecPolicyModel transformToSecurityModel(File policyFile)
			throws IOException, ParseException, FileNotFoundException {
		JSONArray secureUMLJSONArray = (JSONArray) new JSONParser().parse(new FileReader(policyFile));
		SecPolicyModel secureUML = new SecPolicyModel(secureUMLJSONArray);
		return secureUML;
	}

	private DataModel transformToDataModel(String dataModelURI)
			throws IOException, ParseException, FileNotFoundException, Exception {
		File dataModelFile = new File(dataModelURI);
		JSONArray dataModelJSONArray = (JSONArray) new JSONParser().parse(new FileReader(dataModelFile));
		DataModel context = new DataModel(dataModelJSONArray);
		return context;
	}

	public DataModel transformToDataModel(File DataModel) throws Exception {
		JSONArray dataModelJSONArray = (JSONArray) new JSONParser().parse(new FileReader(DataModel));
		DataModel context = new DataModel(dataModelJSONArray);
		return context;
	}
	
	public void setUpDataModel(File DataModel) throws Exception {
		this.dataModel = transformToDataModel(DataModel);
	}

	private void SqlSIGenDatabase(DataModel context, String databaseName, String sqlschemaoutputuri)
			throws IOException {
		File dbGenFile = new File(sqlschemaoutputuri);
		FileWriter fileWriter = new FileWriter(dbGenFile);
		String sqlScript = DM2Schema.generateDatabase(context, databaseName);
		try {
			fileWriter.write(sqlScript);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileWriter.flush();
			fileWriter.close();
		}
	}

	public List<SQLSIAuthFunction> SqlSIGenAuthFunc(DataModel dataModel, SecPolicyModel securityModel,
			SecurityMode secMode, String sqlauthfunctionoutputuri) throws Exception {
		File funGenFile = new File(sqlauthfunctionoutputuri);
		FileWriter fileWriter = new FileWriter(funGenFile);
		String throwErrorFunc = PrintingUtils.printThrowErrorFunc(secMode);
		List<SQLSIAuthFunction> functions = FunctionUtils.printAuthFun(dataModel, securityModel, secMode);
		try {
			fileWriter.write(throwErrorFunc);
			for (SQLSIAuthFunction function : functions) {
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

	public void setMode(SecurityMode mode) {
		this.mode = mode;
	}

}
