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
package org.apache.sqoop.repository.derby;

import static org.apache.sqoop.repository.derby.DerbySchemaConstants.*;

/**
 * DDL queries that create the Sqoop repository schema in Derby database. These
 * queries create the following tables:
 *
 * <p>
 * <strong>SQ_CONNECTOR</strong>: Connector registration.
 * <pre>
 *    +--------------------------+
 *    | SQ_CONNECTOR             |
 *    +--------------------------+
 *    | SQC_NAME: VARCHAR(64) PK |
 *    | SQC_CLASS: VARCHAR(255)  |
 *    +--------------------------+
 * </pre>
 * </p>
 * <p>
 * <strong>SQ_FORM</strong>: Form details.
 * <pre>
 *    +-----------------------------+
 *    | SQ_FORM                     |
 *    +-----------------------------+
 *    | SQF_ID: BIGINT PK AUTO-GEN  |
 *    | SQF_CONNECTOR: VARCHAR(64)  | FK SQ_CONNECTOR(SQC_NAME)
 *    | SQF_NAME: VARCHAR(64)       |
 *    | SQF_TYPE: VARCHAR(32)       | "CONNECTION"|"JOB"
 *    | SQF_INDEX: SMALLINT         |
 *    +-----------------------------+
 * </pre>
 * </p>
 * <p>
 * <strong>SQ_INPUT</strong>: Input details
 * <pre>
 *    +----------------------------+
 *    | SQ_INPUT                   |
 *    +----------------------------+
 *    | SQI_ID: BIGINT PK AUTO-GEN |
 *    | SQI_NAME: VARCHAR(64)      |
 *    | SQI_FORM: BIGINT           | FK SQ_FORM(SQF_ID)
 *    | SQI_INDEX: SMALLINT        |
 *    | SQI_TYPE: VARCHAR(32)      | "STRING"|"MAP"
 *    | SQI_STRMASK: BOOLEAN       |
 *    | SQI_STRLENGTH: SMALLINT    |
 *    +----------------------------+
 * </pre>
 * </p>
 */
public final class DerbySchemaQuery {

  public static final String QUERY_CREATE_SCHEMA_SQOOP =
      "CREATE SCHEMA " + SCHEMA_SQOOP;

  public static final String QUERY_CREATE_TABLE_SQ_CONNECTOR =
      "CREATE TABLE " + TABLE_SQ_CONNECTOR + " (" + COLUMN_SQC_NAME
      + " VARCHAR(64) PRIMARY KEY, " + COLUMN_SQC_CLASS + " VARCHAR(255))";

  public static final String QUERY_CREATE_TABLE_SQ_FORM =
      "CREATE TABLE " + TABLE_SQ_FORM + " (" + COLUMN_SQF_ID
      + " BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) "
      + "PRIMARY KEY, " + COLUMN_SQF_CONNECTOR + " VARCHAR(64), "
      + COLUMN_SQF_NAME + " VARCHAR(64), " + COLUMN_SQF_TYPE + " VARCHAR(32), "
      + COLUMN_SQF_INDEX + " SMALLINT, " + " FOREIGN KEY ("
      + COLUMN_SQF_CONNECTOR+ ") REFERENCES " + TABLE_SQ_CONNECTOR + " ("
      + COLUMN_SQC_NAME + "))";

  public static final String QUERY_CREATE_TABLE_SQ_INPUT =
      "CREATE TABLE " + TABLE_SQ_INPUT + " (" + COLUMN_SQI_ID
      + " BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) "
      + "PRIMARY KEY, " + COLUMN_SQI_NAME + " VARCHAR(64), "
      + COLUMN_SQI_FORM + " BIGINT, " + COLUMN_SQI_INDEX + " SMALLINT, "
      + COLUMN_SQI_TYPE + " VARCHAR(32), " + COLUMN_SQI_STRMASK + " BOOLEAN, "
      + COLUMN_SQI_STRLENGTH + " SMALLINT, FOREIGN KEY (" + COLUMN_SQI_FORM
      + ") REFERENCES " + TABLE_SQ_FORM + " (" + COLUMN_SQF_ID + "))";

  private DerbySchemaQuery() {
    // Disable explicit object creation
  }
}
