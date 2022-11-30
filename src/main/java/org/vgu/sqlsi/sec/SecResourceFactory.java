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

package org.vgu.sqlsi.sec;

import org.json.simple.JSONObject;
import org.vgu.sqlsi.sec.model.AssociationClassResource;
import org.vgu.sqlsi.sec.model.AssociationResource;
import org.vgu.sqlsi.sec.model.AttributeResource;
import org.vgu.sqlsi.sec.model.Resource;

/**
 * follows the factory design pattern that returns the correct type of concrete Resource. This class
 * is the creator that will return the concrete product class that all inherited from the abstract
 * Resource class.
 */
public class SecResourceFactory {

  /**
   * Return the correct type of concrete Resource based on on the value of the key type.
   *
   * @param object one json object of the resource key in the policy model file. For example:
   *     <p>{ "entity": "Student", "attribute": "age" }
   */
  public static Resource create(Object object) {
    JSONObject resourceJSON = (JSONObject) object;
    if (resourceJSON.containsKey("association")) {
      return new AssociationResource(resourceJSON);
    } else if (resourceJSON.containsKey("association-class")) {
      return new AssociationClassResource(resourceJSON);
    } else {
      return new AttributeResource(resourceJSON);
    }
  }
}
