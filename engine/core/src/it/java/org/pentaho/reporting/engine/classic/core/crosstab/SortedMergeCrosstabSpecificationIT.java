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

package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class SortedMergeCrosstabSpecificationIT extends TestCase {
  public SortedMergeCrosstabSpecificationIT() {
  }

  public SortedMergeCrosstabSpecificationIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testMinimalSpecification() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C0", 1 );
    model.addRow( "R1", "C1", 2 );
    model.addRow( "R2", "C2", 3 );
    model.addRow( "R3", "C3", 4 );

    final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillSortedCrosstabSpec( model );
    assertEquals( 4, crosstabSpecification.size() );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C0" }, crosstabSpecification.getKeyAt( 0 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C1" }, crosstabSpecification.getKeyAt( 1 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C2" }, crosstabSpecification.getKeyAt( 2 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C3" }, crosstabSpecification.getKeyAt( 3 ) );
  }

  public void testCompleteLateSpecification() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C0", 1 );
    model.addRow( "R1", "C1", 2 );
    model.addRow( "R2", "C2", 3 );
    model.addRow( "R3", "C3", 4 );
    model.addRow( "R4", "C3", 4 );
    model.addRow( "R4", "C2", 4 );
    model.addRow( "R4", "C1", 4 );
    model.addRow( "R4", "C0", 4 );

    final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillSortedCrosstabSpec( model );
    assertEquals( 4, crosstabSpecification.size() );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C0" }, crosstabSpecification.getKeyAt( 3 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C1" }, crosstabSpecification.getKeyAt( 2 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C2" }, crosstabSpecification.getKeyAt( 1 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C3" }, crosstabSpecification.getKeyAt( 0 ) );
  }

  public void testOverlappingSpecification() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C0", 4 );
    model.addRow( "R0", "C1", 4 );
    model.addRow( "R1", "C1", 4 );
    model.addRow( "R1", "C2", 4 );
    model.addRow( "R2", "C0", 5 );
    model.addRow( "R2", "C2", 5 );

    final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillSortedCrosstabSpec( model );
    assertEquals( 3, crosstabSpecification.size() );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C0" }, crosstabSpecification.getKeyAt( 0 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C1" }, crosstabSpecification.getKeyAt( 1 ) );
    CrosstabTestUtil.assertEqualsArray( new Object[] { "C2" }, crosstabSpecification.getKeyAt( 2 ) );
  }

  public void testConflictingSpecification() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R4", "C3", 4 );
    model.addRow( "R4", "C2", 4 );
    model.addRow( "R4", "C1", 4 );
    model.addRow( "R4", "C0", 4 );
    model.addRow( "R5", "C0", 5 );
    model.addRow( "R5", "C1", 5 );
    model.addRow( "R5", "C2", 5 );
    model.addRow( "R5", "C3", 5 );

    try {
      final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillSortedCrosstabSpec( model );
      fail();
    } catch ( InvalidReportStateException rse ) {
      // good catch ..
    }
  }

  public void testDiagonalMasterDatarow() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C0", 1 );
    model.addRow( "R1", "C1", 2 );
    model.addRow( "R2", "C2", 3 );
    model.addRow( "R3", "C3", 4 );

    final String[][] validateData =
        new String[][] { { "R0", "C0" }, { "R0", "C1" }, { "R0", "C2" }, { "R0", "C3" }, { "R1", "C0" },
          { "R1", "C1" }, { "R1", "C2" }, { "R1", "C3" }, { "R2", "C0" }, { "R2", "C1" }, { "R2", "C2" },
          { "R2", "C3" }, { "R3", "C0" }, { "R3", "C1" }, { "R3", "C2" }, { "R3", "C3" }, };

    final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillSortedCrosstabSpec( model );
    final int itCount = CrosstabTestUtil.advanceCrosstab( crosstabSpecification, model, validateData );
    assertEquals( 16, itCount );
  }

}
