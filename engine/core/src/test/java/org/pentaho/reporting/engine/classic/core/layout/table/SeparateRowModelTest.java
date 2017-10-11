/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
