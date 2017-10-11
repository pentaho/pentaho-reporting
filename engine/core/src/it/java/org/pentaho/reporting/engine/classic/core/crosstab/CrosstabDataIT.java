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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

/**
 * @noinspection HardCodedStringLiteral
 */
public class CrosstabDataIT extends TestCase {
  private static final Log logger = LogFactory.getLog( CrosstabMultiFactDataIT.class );

  public CrosstabDataIT( final String name ) {
    super( name );
  }

  public CrosstabDataIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
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

  private static CrosstabGroup createCrosstab() {
    final CrosstabGroup crosstabGroup = new CrosstabGroup();

    final CrosstabRowGroupBody rowBody = (CrosstabRowGroupBody) crosstabGroup.getBody();
    final CrosstabRowGroup rowGroup = rowBody.getGroup();
    rowGroup.setField( "Rows" );
    rowGroup.getTitleHeader().addElement( createDataItem( "Rows" ) );
    rowGroup.getHeader().addElement( createFieldItem( "Rows" ) );

    final CrosstabColumnGroupBody columnGroupBody = (CrosstabColumnGroupBody) rowGroup.getBody();
    final CrosstabColumnGroup columnGroup = columnGroupBody.getGroup();
    columnGroup.setField( "Cols" );
    columnGroup.getTitleHeader().addElement( createDataItem( "Cols" ) );
    columnGroup.getHeader().addElement( createFieldItem( "Cols" ) );

    final CrosstabCellBody body = (CrosstabCellBody) columnGroup.getBody();
    final CrosstabCell cell = new CrosstabCell();
    cell.addElement( createFieldItem( "Data" ) );
    body.addElement( cell );
    return crosstabGroup;
  }

  public static Element createDataItem( final String text ) {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    return label;
  }

  public static Element createFieldItem( final String text ) {
    final Element label = new Element();
    label.setElementType( TextFieldType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    return label;
  }

  public void testDiagonalReverseMasterRow() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C3", 1 );
    model.addRow( "R1", "C2", 2 );
    model.addRow( "R2", "C1", 3 );
    model.addRow( "R3", "C0", 4 );

    final String[][] validateData =
        new String[][] { { "R0", "C3" }, { "R0", "C2" }, { "R0", "C1" }, { "R0", "C0" }, { "R1", "C3" },
          { "R1", "C2" }, { "R1", "C1" }, { "R1", "C0" }, { "R2", "C3" }, { "R2", "C2" }, { "R2", "C1" },
          { "R2", "C0" }, { "R3", "C3" }, { "R3", "C2" }, { "R3", "C1" }, { "R3", "C0" }, };

    final CrosstabSpecification crosstabSpecification = CrosstabTestUtil.fillOrderedCrosstabSpec( model );
    final int itCount = CrosstabTestUtil.advanceCrosstab( crosstabSpecification, model, validateData );
    assertEquals( 16, itCount );
  }

  public void testDiagonalReportProcessing() throws Exception {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C0", 1 );
    model.addRow( "R1", "C1", 2 );
    model.addRow( "R2", "C2", 3 );
    model.addRow( "R3", "C3", 4 );

    final MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setRootGroup( createCrosstab() );
    report.addExpression( new ValidateExpression( false ) );

    DebugReportRunner.showDialog( report );
    DebugReportRunner.layoutPage( report, 0 );
  }

  public void testDiagonalReverseReportProcessing() throws Exception {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Cols", "Data" } );
    model.addRow( "R0", "C3", 4 );
    model.addRow( "R1", "C2", 3 );
    model.addRow( "R2", "C1", 2 );
    model.addRow( "R3", "C0", 1 );

    final MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setRootGroup( createCrosstab() );
    report.addExpression( new ValidateExpression( true ) );

    DebugReportRunner.showDialog( report );
    DebugReportRunner.layoutPage( report, 0 );
  }

  private static class ValidateExpression extends AbstractExpression {
    private Object[][] validateData;

    private ValidateExpression( final boolean reverse ) {
      setName( "Validate" );
      if ( reverse ) {
        validateData =
            new Object[][] { { "R0", "C0", null }, { "R0", "C1", null }, { "R0", "C2", null },
              { "R0", "C3", Integer.valueOf( 4 ) }, { "R1", "C0", null }, { "R1", "C1", null },
              { "R1", "C2", Integer.valueOf( 3 ) }, { "R1", "C3", null }, { "R2", "C0", null },
              { "R2", "C1", Integer.valueOf( 2 ) }, { "R2", "C2", null }, { "R2", "C3", null },
              { "R3", "C0", Integer.valueOf( 1 ) }, { "R3", "C1", null }, { "R3", "C2", null }, { "R3", "C3", null }, };
      } else {
        validateData =
            new Object[][] { { "R0", "C0", Integer.valueOf( 1 ) }, { "R0", "C1", null }, { "R0", "C2", null },
              { "R0", "C3", null }, { "R1", "C0", null }, { "R1", "C1", Integer.valueOf( 2 ) }, { "R1", "C2", null },
              { "R1", "C3", null }, { "R2", "C0", null }, { "R2", "C1", null }, { "R2", "C2", Integer.valueOf( 3 ) },
              { "R2", "C3", null }, { "R3", "C0", null }, { "R3", "C1", null }, { "R3", "C2", null },
              { "R3", "C3", Integer.valueOf( 4 ) }, };
      }
    }

    public Object getValue() {
      if ( getRuntime().getProcessingContext().getProcessingLevel() == LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING ) {
        return false;
      }
      if ( getRuntime().isCrosstabActive() == false ) {
        return false;
      }
      final int currentRow = getRuntime().getCurrentRow();

      final Object row = getDataRow().get( "Rows" );
      final Object col = getDataRow().get( "Cols" );
      final Object data = getDataRow().get( "Data" );
      try {
        assertEquals( validateData[currentRow][0], row );
        assertEquals( validateData[currentRow][1], col );
        assertEquals( validateData[currentRow][2], data );
        return currentRow;
      } catch ( AssertionFailedError afe ) {
        // throw afe;
        return afe;
      }
    }
  }

  public void testInvalidReportProcessing() throws Exception {
    final TypedTableModel model = new TypedTableModel();
    model.addRow();
    model.addRow();
    model.addRow();

    final MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", model ) );
    report.setRootGroup( createCrosstab() );
    report.addExpression( new ValidateExpression( false ) );

    DebugReportRunner.layoutPage( report, 0 );
  }

}
