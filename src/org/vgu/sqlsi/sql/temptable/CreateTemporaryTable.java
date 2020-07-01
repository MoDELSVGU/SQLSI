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

package org.vgu.sqlsi.sql.temptable;

public class CreateTemporaryTable {
    private SQLTemporaryTable temporaryTable;

    public SQLTemporaryTable getTemporaryTable() {
        return temporaryTable;
    }

    public void setTemporaryTable(SQLTemporaryTable temporaryTable) {
        this.temporaryTable = temporaryTable;
    }

    public String toString() {
        return String.format("CREATE TEMPORARY TABLE %s AS (\r\n%s\r\n)",
            temporaryTable.getName(), temporaryTable.getSelectBody());
    }
}
