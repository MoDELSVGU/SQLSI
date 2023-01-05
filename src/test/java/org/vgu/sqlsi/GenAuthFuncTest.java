package org.vgu.sqlsi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vgu.sqlsi.main.SqlSI;

/**
 * @author HuMiTriet
 */
class GenAuthFuncTest {

  static final String dataModelUri = "sncs2021_evaluation/assoClas_dm.json";
  static final String policyModelUri = "sncs2021_evaluation/secVGU#C3.json";
  static final String queryModelUri = "sncs2021_evaluation/vgu_query.json";

  // oupstatic ut
  static final String databaseSchemaUri = "sncs2021_evaluation/result/db.sql";
  static final String sqlAuthFuncUri = "sncs2021_evaluation/result/secVGU#X3.sql";
  static final String authProcOutputUri = "sncs2021_evaluation/result/secProc.sql";
  static SqlSI sqlsi;

  @BeforeAll
  static void loadPolicyModel() {
    sqlsi = new SqlSI();

    try {
      sqlsi.setDataModel(dataModelUri);
      sqlsi.setQueryModel(queryModelUri);
      sqlsi.setSecurityModel(policyModelUri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void generate() {
    try {
      sqlsi.run("mydb", queryModelUri, databaseSchemaUri, sqlAuthFuncUri, authProcOutputUri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
