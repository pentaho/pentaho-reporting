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
import org.pentaho.reporting.engine.classic.core.layout.process.CleanTableRowsPreparationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CleanTableRowsPreparationStep.Cell;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

public class ComputeSafeCutsTest extends TestCase {
  public ComputeSafeCutsTest() {
  }

  public void testNonSpan() {
    GenericObjectTable<Cell> cells = new GenericObjectTable<Cell>();
    cells.setObject( 0, 0, new Cell( 0, 0, 1, 0 ) );
    cells.setObject( 1, 0, new Cell( 1, 0, 1, 10 ) );
    cells.setObject( 2, 0, new Cell( 2, 0, 1, 20 ) );
    cells.setObject( 3, 0, new Cell( 3, 0, 1, 30 ) );

    int pageOffset = 0;
    assertEquals( 0, CleanTableRowsPreparationStep.computeSafeCut( pageOffset, cells, 4 ) );
  }

  public void testNonSpan2() {
    GenericObjectTable<Cell> cells = new GenericObjectTable<Cell>();
    cells.setObject( 0, 0, new Cell( 0, 0, 1, 0 ) );
    cells.setObject( 1, 0, new Cell( 1, 0, 1, 10 ) );
    cells.setObject( 2, 0, new Cell( 2, 0, 1, 20 ) );
    cells.setObject( 3, 0, new Cell( 3, 0, 1, 30 ) );

    int pageOffset = 20;
    assertEquals( 2, CleanTableRowsPreparationStep.computeSafeCut( pageOffset, cells, 4 ) );
  }

  public void testNonSpan3() {
    GenericObjectTable<Cell> cells = new GenericObjectTable<Cell>();
    cells.setObject( 0, 0, new Cell( 0, 0, 1, 0 ) );
    cells.setObject( 1, 0, new Cell( 1, 0, 2, 10 ) );
    cells.setObject( 3, 0, new Cell( 3, 0, 1, 30 ) );
    cells.setObject( 4, 0, new Cell( 4, 0, 1, 40 ) );

    int pageOffset = 20;
    assertEquals( 1, CleanTableRowsPreparationStep.computeSafeCut( pageOffset, cells, 4 ) );
  }

  private void addCell( GenericObjectTable<Cell> cells, Cell c ) {
    for ( int i = 0; i < c.getRowSpan(); i += 1 ) {
      cells.setObject( c.getRowIndex() + i, c.getColIndex(), c );
    }
  }

  public void testInterleavedSpan() {
    GenericObjectTable<Cell> cells = new GenericObjectTable<Cell>();
    addCell( cells, new Cell( 0, 0, 2, 0 ) );
    addCell( cells, new Cell( 0, 1, 1, 0 ) );
    addCell( cells, new Cell( 1, 1, 2, 10 ) );
    addCell( cells, new Cell( 2, 0, 2, 20 ) );
    addCell( cells, new Cell( 3, 1, 2, 30 ) );
    addCell( cells, new Cell( 4, 0, 1, 40 ) );

    int pageOffset = 35;
    assertEquals( 2, CleanTableRowsPreparationStep.computeSafeCut( pageOffset, cells, 5 ) );

    pageOffset = 25;
    assertEquals( 0, CleanTableRowsPreparationStep.computeSafeCut( pageOffset, cells, 5 ) );
  }

}
