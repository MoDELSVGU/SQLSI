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

package org.vgu.sqlsi.sql.proc;

import java.util.HashMap;
import java.util.Stack;

import org.vgu.sqlsi.sql.temptable.CreateTemporaryTable;
import org.vgu.sqlsi.sql.temptable.DropTemporaryTable;
import org.vgu.sqlsi.sql.temptable.SQLTemporaryTable;
import org.vgu.sqlsi.utils.Template;

public class SQLSIStoredProcedure extends SQLStoredProcedure {
    private String name;
    private HashMap<String, String> parameters;
    private Stack<SQLTemporaryTable> temps;
    private String action;
    private String comments;

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

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return String.format(Template.PROC, printTemporaryTables(temps),
            temps.peek().getName());
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
