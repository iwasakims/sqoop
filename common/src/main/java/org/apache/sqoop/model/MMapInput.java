/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.sqoop.utils.UrlSafeUtils;

public final class MMapInput extends MInput<Map<String, String>> {

  public MMapInput(String name) {
    super(name);
  }

  @Override
  public String getUrlSafeValueString() {
    Map<String, String> valueMap = getValue();
    if (valueMap == null || valueMap.size() == 0) {
      return "";
    }
    boolean first = true;
    StringBuilder vsb = new StringBuilder();
    for (String key : valueMap.keySet()) {
      if (first) {
        first = false;
      } else {
        vsb.append("&");
      }
      String value = valueMap.get(key);
      vsb.append(UrlSafeUtils.urlEncode(key)).append("=");
      vsb.append(UrlSafeUtils.urlEncode(value));
    }
    return vsb.toString();
  }

  @Override
  public void restoreFromUrlSafeValueString(String valueString) {
    Map<String, String> valueMap = null;
    if (valueString != null && valueString.trim().length() > 0) {
      valueMap = new HashMap<String, String>();
      String[] valuePairs = valueString.split("&");
      for (String pair : valuePairs) {
        String[] nameAndVal = pair.split("=");
        if (nameAndVal.length > 0) {
          String name = nameAndVal[0];
          String value = null;
          if (nameAndVal.length > 1) {
            value = nameAndVal[1];
          }

          valueMap.put(name, value);
        }
      }
    }
    setValue(valueMap);
  }
}
