/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
