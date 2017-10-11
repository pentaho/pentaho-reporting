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

package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.SeparateRowModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;

public class TableRowModelTest extends TestCase {
  public TableRowModelTest() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPruneAll() {
    final SeparateRowModel rowModel = new SeparateRowModel();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();

    updateTableRow( rowModel, 0, 1000 );
    updateTableRow( rowModel, 1, 1000, 2000 );
    updateTableRow( rowModel, 2, 1000, 0, 2000 );
    updateTableRow( rowModel, 3, 1000 );
    updateTableRow( rowModel, 4, 1000 );

    rowModel.validateActualSizes();
    rowModel.prune( 5 );

    assertEquals( 1, rowModel.getRowCount() );
    assertEquals( 1, rowModel.getRow( 0 ).getMaximumRowSpan() );
    assertEquals( 5000, rowModel.getRow( 0 ).getPreferredSize( 1 ) );
    assertEquals( 5000, rowModel.getRow( 0 ).getValidatedTrailingSize( 1 ) );
  }

  public void testPruneMiddle() {
    final SeparateRowModel rowModel = new SeparateRowModel();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();

    updateTableRow( rowModel, 0, 1000 );
    updateTableRow( rowModel, 1, 1000, 2000 );
    updateTableRow( rowModel, 2, 1000 );
    updateTableRow( rowModel, 3, 1000, 0, 2000 );
    updateTableRow( rowModel, 4, 1000 );
    updateTableRow( rowModel, 5, 1000 );

    rowModel.validateActualSizes();

    assertEquals( 3, rowModel.getRow( 3 ).getMaximumRowSpan() );
    assertEquals( 1000, rowModel.getRow( 3 ).getPreferredSize( 1 ) );
    assertEquals( 1000, rowModel.getRow( 3 ).getValidatedTrailingSize( 1 ) );
    assertEquals( 0, rowModel.getRow( 3 ).getPreferredSize( 2 ) );
    assertEquals( 0, rowModel.getRow( 3 ).getValidatedTrailingSize( 2 ) );
    assertEquals( 2000, rowModel.getRow( 3 ).getPreferredSize( 3 ) );
    assertEquals( 2000, rowModel.getRow( 3 ).getValidatedTrailingSize( 3 ) );

    rowModel.prune( 2 );

    // assert that nothing has been done. This model is actually not breakable after the second row, as this
    // row spans further into the third row.
    assertEquals( 6, rowModel.getRowCount() );

    rowModel.prune( 3 );
    assertEquals( 4, rowModel.getRowCount() );
    assertEquals( 1, rowModel.getRow( 0 ).getMaximumRowSpan() );
    assertEquals( 3000, rowModel.getRow( 0 ).getPreferredSize( 1 ) );
    assertEquals( 3000, rowModel.getRow( 0 ).getValidatedTrailingSize( 1 ) );

    assertEquals( 3, rowModel.getRow( 1 ).getMaximumRowSpan() );
    assertEquals( 1000, rowModel.getRow( 1 ).getPreferredSize( 1 ) );
    assertEquals( 1000, rowModel.getRow( 1 ).getValidatedTrailingSize( 1 ) );
    assertEquals( 0, rowModel.getRow( 1 ).getPreferredSize( 2 ) );
    assertEquals( 0, rowModel.getRow( 1 ).getValidatedTrailingSize( 2 ) );
    assertEquals( 2000, rowModel.getRow( 1 ).getPreferredSize( 3 ) );
    assertEquals( 2000, rowModel.getRow( 1 ).getValidatedTrailingSize( 3 ) );
  }

  public void testPruneNotBreaking() {
    final SeparateRowModel rowModel = new SeparateRowModel();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();
    rowModel.addRow();

    updateTableRow( rowModel, 0, 1000 );
    updateTableRow( rowModel, 1, 1000, 2000 );
    updateTableRow( rowModel, 2, 1000, 0, 2000 );
    updateTableRow( rowModel, 3, 1000 );
    updateTableRow( rowModel, 4, 1000 );
    rowModel.validateActualSizes();
    rowModel.prune( 2 );

    // assert that nothing has been done. This model is actually not breakable.
    assertEquals( 5, rowModel.getRowCount() );
  }

  private void updateTableRow( final TableRowModel model, int rowNumber, final long... rowHeights ) {
    for ( int i = 0; i < rowHeights.length; i++ ) {
      final long rowHeight = rowHeights[i];
      model.updateDefinedSize( rowNumber, i + 1, rowHeight );
      model.updateValidatedSize( rowNumber, i + 1, 0, rowHeight );
    }
  }
}
