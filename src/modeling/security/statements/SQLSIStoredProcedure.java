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

package modeling.security.statements;

import java.util.HashMap;
import java.util.Stack;

import modeling.api.SQLSIConfiguration;
import modeling.data.templates.SQLTemplate;
import modeling.statements.SQLStoredProcedure;
import modeling.statements.SQLTemporaryTable;
import modeling.statements.create.CreateTemporaryTable;
import modeling.statements.drop.DropTemporaryTable;

public class SQLSIStoredProcedure extends SQLStoredProcedure {
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    private String name;
    private HashMap<String, String> parameters;
    private Stack<SQLTemporaryTable> temps;
    private String action;
    private String comments;
    private String query;

    public SQLSIStoredProcedure() {
        this.name = "SecQuery";
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
        parameters.put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
        setParameters(parameters);
    }

    public Stack<SQLTemporaryTable> getTemps() {
        return temps;
    }

    public void setTemps(Stack<SQLTemporaryTable> temp) {
        this.temps = temp;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    private void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return String.format(SQLTemplate.PROC, printTemporaryTables(temps),
                query);
    }

    private String printTemporaryTables(Stack<SQLTemporaryTable> temps) {
        String printTemp = "";
        for (SQLTemporaryTable temp : temps) {
            DropTemporaryTable dropTemporaryTable = new DropTemporaryTable();
            dropTemporaryTable.setIfExists(true);
            dropTemporaryTable.setTemporaryTable(temp);
            CreateTemporaryTable createTemporaryTable = new CreateTemporaryTable();
            createTemporaryTable.setTemporaryTable(temp);
            printTemp = printTemp.concat(dropTemporaryTable.toString())
                    .concat(";\r\n").concat(createTemporaryTable.toString())
                    .concat(";\r\n");
        }
        return printTemp;
    }

    public String printParameters() {
        String para = "";
        int i = 0;
        for (String key : parameters.keySet()) {
            if (i == 0) {
                para = para.concat(
                        String.format("in %s %s", key, parameters.get(key)));
            } else {
                para = para.concat(
                        String.format(", in %s %s", key, parameters.get(key)));
            }
            i++;
        }
        return para;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
