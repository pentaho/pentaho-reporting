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


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelTableModule;

import java.util.Arrays;

public class ValidateProcessTasksIT extends TestCase {
  public ValidateProcessTasksIT() {
  }

  public ValidateProcessTasksIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBoot() {
    final String[] exportTypes = ReportProcessTaskRegistry.getInstance().getExportTypes();
    Arrays.sort( exportTypes );
    for ( int i = 0; i < exportTypes.length; i++ ) {
      final String exportType = exportTypes[i];
      System.out.println( exportType );
    }

    assertEquals( 21, exportTypes.length );
    ReportProcessTaskRegistry.getInstance().createProcessTask( ExcelTableModule.EXCEL_FLOW_EXPORT_TYPE );
  }
}
