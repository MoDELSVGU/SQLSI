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

package org.vgu.sqlsi.main;

public class SQLSIConfiguration {
  // public static final String SCHEMANAME = "mydb";
  public static final String SECQUERYNAME = "secquery";
  public static final String PARAM_PREFIX = "_";
  public static final String PARAM_TYPE = "varchar(250)";

  public static final String SELF = PARAM_PREFIX + "self";
  public static final String CALLER = PARAM_PREFIX + "caller";
  public static final String TARGET = PARAM_PREFIX + "target";
  public static final String VALUE = PARAM_PREFIX + "value";
  public static final String ROLE = PARAM_PREFIX + "role";
}
