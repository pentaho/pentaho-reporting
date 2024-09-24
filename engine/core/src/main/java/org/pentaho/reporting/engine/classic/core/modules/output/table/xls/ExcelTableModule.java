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

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the Excel table export module.
 *
 * @author Thomas Morgner
 */
public class ExcelTableModule extends AbstractModule {
  /**
   * The configuration prefix when reading the configuration settings from the report configuration.
   *
   * @deprecated The configuration prefix should not be needed anymore. Always provide the full name.
   */
  public static final String CONFIGURATION_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.xls";

  public static final String EXCEL_STREAM_EXPORT_TYPE = "table/excel;page-mode=stream";
  public static final String EXCEL_FLOW_EXPORT_TYPE = "table/excel;page-mode=flow";
  public static final String EXCEL_PAGE_EXPORT_TYPE = "table/excel;page-mode=page";
  public static final String XLSX_STREAM_EXPORT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;page-mode=stream";
  public static final String XLSX_FLOW_EXPORT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;page-mode=flow";
  public static final String XLSX_PAGE_EXPORT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;page-mode=page";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public ExcelTableModule() throws ModuleInitializeException {
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
    if ( AbstractModule.isClassLoadable( "org.apache.poi.hssf.usermodel.HSSFWorkbook", ExcelTableModule.class ) == false ) {
      throw new ModuleInitializeException( "Unable to load POI classes." );
    }

    ElementMetaDataParser
        .initializeOptionalReportProcessTaskMetaData( "org/pentaho/reporting/engine/classic/core/modules/output/table/xls/meta-report-process-tasks.xml" );
  }
}
