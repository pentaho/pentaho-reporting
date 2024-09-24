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
