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

/**
 * Represents a parameter input used by the connector for creating a connection
 * or a job object.
 * @param <T> the value type associated with this parameter
 */
public abstract class MInput<T> extends MNamedElement {
  private T value;

  protected MInput(String name) {
    super(name);
  }

  /**
   * @param value the value to be set for this parameter
   */
  public void setValue(T value) {
    this.value = value;
  }

  /**
   * @return any previously set value for this parameter
   */
  public T getValue() {
    return value;
  }

  /**
   * @return a URL-safe string representation of the value
   */
  public abstract String getUrlSafeValueString();

  /**
   * Overrides the associated value of this input by the value represented by
   * the provided URL-safe value string.
   * @param valueString the string representation of the value from which the
   * value must be restored.
   */
  public abstract void restoreFromUrlSafeValueString(String valueString);

  @Override
  public final String toString() {
    StringBuilder sb = new StringBuilder("input-");
    sb.append(getClass().getSimpleName()).append(":").append(getName());

    return sb.toString();
  }
}
