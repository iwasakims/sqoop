.. Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


=============================
Sqoop 2 Connector Development
=============================

This document describes you how to implement connector for Sqoop 2.


What is Connector?
++++++++++++++++++

Connector provides access to external databases.
Connector reads data from databases for import,
and write data to databases for export.
Interaction with Hadoop is taken cared by common modules of Sqoop framework.


Connector Implementation
++++++++++++++++++++++++

The SqoopConnector class defines functionality
which must be provided by Connectors.
Each Connector implements methods shown below.
::

  public abstract String getVersion();
  public abstract ResourceBundle getBundle(Locale locale);
  public abstract Class getConnectionConfigurationClass();
  public abstract Class getJobConfigurationClass(MJob.Type jobType);
  public abstract Importer getImporter();
  public abstract Exporter getExporter();
  public abstract Validator getValidator();
  public abstract MetadataUpgrader getMetadataUpgrader();

The most significant parts are Importer and Exporter.


Importer
========

Importer is a placeholder for the modules needed for import.
Built-in GenericJdbcConnector uses Importer like this.
::

  private static final Importer IMPORTER = new Importer(
      GenericJdbcImportInitializer.class,
      GenericJdbcImportPartitioner.class,
      GenericJdbcImportExtractor.class,
      GenericJdbcImportDestroyer.class);
  
  ...
  
  @Override
  public Importer getImporter() {
    return IMPORTER;
  }


Extractor
---------

Extractor (E for ETL) extracts data from external database and
writes it to Sqoop framework for import.

Exrtractor must implements extract method.
::

  public abstract void extract(ExtractorContext context,
                               ConnectionConfiguration connectionConfiguration,
                               JobConfiguration jobConfiguration,
                               Partition partition);

The extract method extracts data from database in some way and
writes it to DataWriter (provided by context) as `Intermediate representation`_ .

Extractor must iterates in the extract method
until the data from database exausts.
::

  while (resultSet.next()) {
    ...
    context.getDataWriter().writeArrayRecord(array);
    ...
  }


Partitioner
-----------





Initializer and Destroyer
-------------------------


Exporter
========

Similar to Importer,
Exporter is a placeholder for the modules needed for export.
Built-in GenericJdbcConnector uses Importer like this.
::

  private static final Exporter EXPORTER = new Exporter(
      GenericJdbcExportInitializer.class,
      GenericJdbcExportLoader.class,
      GenericJdbcExportDestroyer.class);
  
  ...
  
  @Override
  public Exporter getExporter() {
    return EXPORTER;
  }


Loader
------

Loader (L for ETL) receives data from Sqoop framework and
loads it to external database.

Loader must implements load method.
::

  public abstract void load(LoaderContext context,
                            ConnectionConfiguration connectionConfiguration,
                            JobConfiguration jobConfiguration) throws Exception;

The load method reads data from DataReader (provided by context)
in `Intermediate representation`_ and loads it to database in some way.

Loader must iterates in the load method
until the data from DataReader exausts.
::

  while ((array = context.getDataReader().readArrayRecord()) != null) {
    ...
  }


Initializer and Destroyer
-------------------------



Configuration for Connector
+++++++++++++++++++++++++++



Internal of Sqoop2 MapReduce Job
++++++++++++++++++++++++++++++++

Sqoop 2 provides common MapReduce modules such as SqoopMapper and SqoopReducer
for the both of import and export.

- InputFormat create splits using Partitioner.

- SqoopMapper invokes Exporter's export method.

- (Not SqoopReducer but) OutputFormat invokes Loader's load method.

.. todo: sequence diagram like figure.

For import, Extractor provided by Connector extracts data from databases,
and Loader provided by Sqoop2 loads data into Hadoop.
For export, Extractor provided Sqoop2 exracts data from Hadoop,
and Loader provided by Connector loads data into databases.


.. _`Intermediate representation`: https://cwiki.apache.org/confluence/display/SQOOP/Sqoop2+Intermediate+representation
