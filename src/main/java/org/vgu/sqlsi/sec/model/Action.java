/**************************************************************************
 * Copyright 2020 Vietnamese-German-University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author: ngpbh
 ***************************************************************************/

package org.vgu.sqlsi.sec.model;

public enum Action {
  READ,
  CREATE,
  UPDATE,
  DELETE;

  public static Action getAction(String action) {
    if ("READ".equalsIgnoreCase(action)) return READ;
    if ("CREATE".equalsIgnoreCase(action)) return CREATE;
    if ("UPDATE".equalsIgnoreCase(action)) return UPDATE;
    if ("DELETE".equalsIgnoreCase(action)) return DELETE;
    else return null;
  }

  @Override
  public String toString() {
    switch (this) {
      case READ:
        return "READ";
      case CREATE:
        return "CREATE";
      case UPDATE:
        return "UPDATE";
      case DELETE:
        return "DELETE";
      default:
        return null;
    }
  }
}
