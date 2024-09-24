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
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class TableCellSizingIT extends TestCase {
  public TableCellSizingIT() {
  }

  private static class ElementCreator implements TableTestUtil.ElementProducer {
    private ElementCreator() {
    }

    public Band createCell( final int x, final int y ) {
      final Band tableCell = new Band();
      tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
      tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, 1 );
      tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, 1 );
      return tableCell;
    }

    public Element createDataItem( final String text, final int x, final int y ) {
      final Element label = new Element();
      label.setElementType( LabelType.INSTANCE );
      label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
          "asajsdlkasjdalksdjalskdjalsdjalsdkjalsdajlsdjasldkajld" );
      label.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, 20f );
      return label;
    }
  }

  private static class RowSpanElementCreator implements TableTestUtil.ElementProducer {
    private RowSpanElementCreator() {
    }

    public Band createCell( final int row, final int column ) {
      if ( row == 1 && column == 0 ) {
        return null;
      }

      final Band tableCell = new Band();
      tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
      tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, 1 );
      tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.COLSPAN, 1 );
      if ( row == 0 && column == 0 ) {
        tableCell.setAttribute( AttributeNames.Table.NAMESPACE, AttributeNames.Table.ROWSPAN, 2 );
      }
      return tableCell;
    }

    public Element createDataItem( final String text, final int row, final int column ) {
      final Element label = new Element();
      label.setElementType( LabelType.INSTANCE );
      label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Test" );
      label.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, 100f );
      label.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, 20f );
      return label;
    }
  }

  // 42546400
  // 30000000
  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCellSize() throws Exception {
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper format = pff.createPaper( 200, 200 );
    pff.setBorders( format, 0, 0, 0, 0 );

    final SimplePageDefinition p = new SimplePageDefinition( pff.createPageFormat( format, PageFormat.PORTRAIT ) );

    final MasterReport report = new MasterReport();
    report.setPageDefinition( p );
    final Band table = TableTestUtil.createTable( 2, 1, 1, new ElementCreator() );
    table.setName( "table" );
    report.getReportHeader().addElement( table );

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( pageBox, LayoutNodeTypes.TYPE_BOX_TABLE_CELL );
    assertEquals( 4, elementsByNodeType.length );
    for ( int i = 0; i < elementsByNodeType.length; i++ ) {
      final RenderNode renderNode = elementsByNodeType[i];
      final RenderNode prev = renderNode.getPrev();
      if ( prev != null ) {
        assertEquals( renderNode.getX(), prev.getX() + prev.getWidth() );
        assertEquals( renderNode.getX(), renderNode.getMinimumChunkWidth() );
      }
      assertEquals( renderNode.getWidth(), renderNode.getMinimumChunkWidth() );

    }
  }

  public void testCellOnRowSpan() throws Exception {
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper format = pff.createPaper( 200, 200 );
    pff.setBorders( format, 0, 0, 0, 0 );

    final SimplePageDefinition p = new SimplePageDefinition( pff.createPageFormat( format, PageFormat.PORTRAIT ) );

    final MasterReport report = new MasterReport();
    report.setPageDefinition( p );
    final Band table = TableTestUtil.createTable( 2, 2, 1, new RowSpanElementCreator() );
    table.setName( "table" );
    report.getReportHeader().addElement( table );

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode[] elementsByNodeType =
        MatchFactory.findElementsByNodeType( pageBox, LayoutNodeTypes.TYPE_BOX_TABLE_CELL );
    assertEquals( 5, elementsByNodeType.length );

    for ( int i = 0; i < elementsByNodeType.length; i++ ) {
      final RenderNode renderNode = elementsByNodeType[i];
      final RenderNode prev = renderNode.getPrev();
      if ( prev != null ) {
        assertEquals( renderNode.getX(), prev.getX() + prev.getWidth() );
        assertEquals( renderNode.getX(), StrictGeomUtility.toInternalValue( 100 ) );
      }
      assertEquals( renderNode.getWidth(), StrictGeomUtility.toInternalValue( 100 ) );

    }
  }
}
