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

package org.apache.sqoop.mapreduce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.NullWritable;
import org.apache.sqoop.lib.SqoopRecord;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.sqoop.mapreduce.db.DBConfiguration;
import org.apache.sqoop.util.LoggingUtils;
import org.apache.sqoop.util.PostgreSQLUtils;
import org.apache.sqoop.util.Executor;
import org.apache.sqoop.util.JdbcUrl;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.copy.CopyIn;


/**
 * Mapper that starts a 'pg_bulkload' process and uses that to export rows from
 * HDFS to a PostgreSQL database at high speed.
 *
 * map() methods are actually provided by subclasses that read from
 * SequenceFiles (containing existing SqoopRecords) or text files
 * (containing delimited lines) and deliver these results to the stream
 * used to interface with pg_bulkload.
 */
public class PostgreSQLCopyMapper
    extends AutoProgressMapper<LongWritable, Writable,
                               NullWritable, NullWritable> {
  private Configuration conf;
  private DBConfiguration dbConf;
  private CopyIn copyin = null;


  public PostgreSQLCopyMapper() {
  }


  protected void setup(Context context)
    throws IOException, InterruptedException {
    super.setup(context);
    conf = context.getConfiguration();
    dbConf = new DBConfiguration(conf);
    Connection conn = null;
    CopyManager cm = null;
    try {
      conn = dbConf.getConnection();
      cm = ((PGConnection)conn).getCopyAPI();
    } catch (ClassNotFoundException ex) {
      LOG.error("Unable to load JDBC driver class", ex);
      throw new IOException(ex);
    } catch (SQLException ex) {
      LoggingUtils.logAll(LOG, "Unable to execute statement", ex);
      throw new IOException(ex);
    } finally {
      try {
        conn.close();
      } catch (SQLException ex) {
        LoggingUtils.logAll(LOG, "Unable to close connection", ex);
      }
    }
    StringBuilder sql = new StringBuilder();
    sql.append("COPY ");
    sql.append(dbConf.getOutputTableName());
    sql.append(" FROM STDIN");
    try {
      copyin = cm.copyIn(sql.toString());
    } catch (SQLException ex) {
      LoggingUtils.logAll(LOG, "Unable to execute copy", ex);
      throw new IOException(ex);
    }
  }


  public void map(LongWritable key, Writable value, Context context)
    throws IOException, InterruptedException {
  }


  protected void cleanup(Context context)
    throws IOException, InterruptedException {
    try {
      copyin.endCopy();
    } catch (SQLException ex) {
      LoggingUtils.logAll(LOG, "Unable to execute copy", ex);
      throw new IOException(ex);
    }
  }
}
