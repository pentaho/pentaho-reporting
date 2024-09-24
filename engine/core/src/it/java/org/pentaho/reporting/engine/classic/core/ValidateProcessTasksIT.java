/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
