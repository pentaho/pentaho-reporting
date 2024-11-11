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


package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.SeparateRowModel;

public class SeparateRowModelTest extends TestCase {
  public SeparateRowModelTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBasicValidateSizes() {
    SeparateRowModel model = new SeparateRowModel();
    model.addRow();
    model.addRow();
    model.addRow();

    model.updateDefinedSize( 0, 1, 10000 );
    model.updateDefinedSize( 0, 2, 5000 );
    model.updateDefinedSize( 1, 1, 10000 );
    model.updateDefinedSize( 1, 2, 30000 );
    model.updateDefinedSize( 2, 1, 10000 );

    model.validatePreferredSizes();

    assertEquals( "Not all rows validated.", 3, model.getValidatedRowCount() );
    assertEquals( "Row-0 size is 10000.", 10000, model.getPreferredRowSize( 0 ) );
    assertEquals( "Row-1 size is 15000.", 15000, model.getPreferredRowSize( 1 ) );
    assertEquals( "Row-2 size is 15000.", 15000, model.getPreferredRowSize( 2 ) );
  }

  public void testMissingLastRowsValidateSizes() {
    SeparateRowModel model = new SeparateRowModel();
    model.addRow();
    model.addRow();
    model.addRow();

    model.updateDefinedSize( 0, 1, 10000 );
    model.updateDefinedSize( 0, 2, 5000 );
    model.updateDefinedSize( 1, 1, 10000 );
    model.updateDefinedSize( 1, 2, 30000 );
    model.updateDefinedSize( 2, 3, 10000 );

    model.validatePreferredSizes();

    assertEquals( "Last row cannot be validated.", 2, model.getValidatedRowCount() );
    assertEquals( "Row-0 size is 10000.", 10000, model.getPreferredRowSize( 0 ) );
    assertEquals( "Row-1 size is 20000.", 20000, model.getPreferredRowSize( 1 ) );
    assertEquals( "Row-2 size is 10000.", 10000, model.getPreferredRowSize( 2 ) );

    model.addRow();
    model.addRow();
    model.updateDefinedSize( 3, 1, 10000 );
    model.updateDefinedSize( 4, 1, 10000 );

    model.validatePreferredSizes();

    assertEquals( "Last row cannot be validated.", 5, model.getValidatedRowCount() );
    assertEquals( "Row-0 size is 10000.", 10000, model.getPreferredRowSize( 0 ) );
    assertEquals( "Row-1 size is 20000.", 20000, model.getPreferredRowSize( 1 ) );
    assertEquals( "Row-2 size is 10000.", 10000, model.getPreferredRowSize( 2 ) );
    assertEquals( "Row-3 size is 10000.", 10000, model.getPreferredRowSize( 3 ) );
    assertEquals( "Row-4 size is 10000.", 10000, model.getPreferredRowSize( 4 ) );
  }
}
