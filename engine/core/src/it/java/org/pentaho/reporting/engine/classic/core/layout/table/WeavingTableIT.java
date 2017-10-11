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
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ChildMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.ElementMatcher;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.NodeMatcher;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class WeavingTableIT extends TestCase {
  public WeavingTableIT() {
  }

  public WeavingTableIT( final String name ) {
    super( name );
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSimpleTable() throws ReportProcessingException, ContentProcessingException {
    System.out.println( ObjectUtilities.getResource( "simplelog.properties", getClass() ) );

    final Element label = TableTestUtil.createDataItem( "Cell" );

    final Band tableCell = new Band();
    tableCell.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_CELL );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableCell.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    tableCell.addElement( label );

    final Band autoRow = TableTestUtil.createAutoBox( tableCell );
    autoRow.setName( "AutoRow" );
    final Band tableRow = TableTestUtil.createRow( autoRow );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    final Band autoBody = TableTestUtil.createAutoBox( tableRow );
    autoBody.setName( "AutoBody" );
    tableBody.addElement( autoBody );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    final Band autoTable = TableTestUtil.createAutoBox( tableBody );
    autoTable.setName( "AutoTable" );
    table.addElement( autoTable );

    final MasterReport report = new MasterReport();
    report.setStrictLegacyMode( false );
    final ReportHeader band = report.getReportHeader();
    band.addElement( table );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band, false, false );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final NodeMatcher matcher = new ChildMatcher( new ElementMatcher( "TableCellRenderBox" ) );
    final RenderNode[] all = MatchFactory.matchAll( logicalPageBox, matcher );

    // assert that the direct childs of a table-cell-render-box have the same width as the table-cell render box

    assertEquals( 1, all.length );
    for ( final RenderNode renderNode : all ) {
      assertEquals( 0l, renderNode.getX() );
      assertEquals( 46800000l, renderNode.getWidth() );
      assertEquals( 20000000l, renderNode.getHeight() );
      assertEquals( 0l, renderNode.getY() );
    }
  }
}
