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


package org.vgu.sqlsi.sql.func;

import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;

public class DropFunction {
    private String type;
    private boolean ifExists = false;
    private List<String> parameters;
    private SQLFunction function;

    public DropFunction() {
        this.setType("FUNCTION");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SQLFunction getFunction() {
        return function;
    }

    public void setFunction(SQLFunction function) {
        this.function = function;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public void setParameters(List<String> list) {
        parameters = list;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String toString() {
        String sql = "DROP " + type + " " + (ifExists ? "IF EXISTS " : "")
            + function.getFunctionName();

        if (parameters != null && !parameters.isEmpty()) {
            sql += " " + PlainSelect.getStringList(parameters);
        }

        return sql;
    }
}
