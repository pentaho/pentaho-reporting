/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.table.csv;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the Html table export module.
 *
 * @author Thomas Morgner
 */
public class CSVTableModule extends AbstractModule {
  /**
   * The default value for the separator string (",").
   */
  public static final String SEPARATOR_DEFAULT = ",";

  public static final String STRICT_LAYOUT =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.StrictLayout";
  public static final String ENCODING = "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.Encoding";
  public static final String SEPARATOR = "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.Separator";
  public static final String TABLE_CSV_STREAM_EXPORT_TYPE = "table/csv;page-mode=stream";
  public static final String TABLE_CSV_FLOW_EXPORT_TYPE = "table/csv;page-mode=flow";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public CSVTableModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementMetaDataParser
        .initializeOptionalReportProcessTaskMetaData( "org/pentaho/reporting/engine/classic/core/modules/output/table/csv/meta-report-process-tasks.xml" );
  }
}
