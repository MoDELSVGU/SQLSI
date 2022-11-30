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

package org.vgu.sqlsi.sql.func;

import org.vgu.sqlsi.utils.Template;

/**
 * Follow the Factory method design pattern.
 *
 * <p>This class is the Creator class.
 */
public class CreateFunction {
  private SQLFunction function;

  public SQLFunction getFunction() {
    return function;
  }

  public void setFunction(SQLFunction function) {
    this.function = function;
  }

  /**
   * This is the createProduct() method of the creator class in the Factory method design pattern.
   */
  public String toString() {
    return String.format(
        Template.CREATE_FUNC,
        function.getFunctionName(),
        function.getAuthFunParametersWithType(),
        function.toString());
  }
}
