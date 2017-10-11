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

package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class GroupRowLayoutIT extends TestCase {
  public GroupRowLayoutIT() {
  }

  public GroupRowLayoutIT( final String name ) {
    super( name );
  }

  public static Element createDataItem( final String text ) {
    final Element label = new Element();
    label.setElementType( LabelType.INSTANCE );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    return label;
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGroupPrinting() throws Exception {
    final MasterReport report = new MasterReport();
    final RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
    rootGroup.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_ROW );
    rootGroup.getBody().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_BLOCK );

    rootGroup.getHeader().setName( "GH" );
    rootGroup.getHeader().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_CANVAS );
    rootGroup.getHeader().getStyle().setStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH, Boolean.TRUE );
    rootGroup.getHeader().addElement( createDataItem( "Header" ) );

    report.getNoDataBand().setName( "NDB" );
    report.getNoDataBand().addElement( createDataItem( "No-DataBand" ) );
    report.getNoDataBand().getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );

    rootGroup.getFooter().setName( "GF" );
    rootGroup.getFooter().addElement( createDataItem( "Footer" ) );
    rootGroup.getFooter().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_CANVAS );
    rootGroup.getFooter().getStyle().setStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH, Boolean.TRUE );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    final RenderNode[] nodes = MatchFactory.matchAll( logicalPageBox, "RowRenderBox > *" );
    assertEquals( "Number of nodes is 3", 3, nodes.length );
    for ( int i = 0; i < nodes.length; i++ ) {
      final RenderNode node = nodes[i];
      assertEquals( "Node: " + node, StrictGeomUtility.toInternalValue( 100 ), node.getWidth() );
    }

    // XmlPageReportUtil.createXml(report, System.out);
  }

  public void testLayoutStrict() throws Exception {
    final MasterReport report = new MasterReport();
    report.setStrictLegacyMode( true );
    final Band band = new Band();
    band.setName( "Band" );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, 1f );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, 1f );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 101f );
    report.getReportHeader().addElement( band );

    band.addElement( createDataItem( "Header" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );

    final RenderNode nodes = MatchFactory.match( logicalPageBox, "CanvasRenderBox > CanvasRenderBox" );
    assertNotNull( nodes );

    assertEquals( "MinChunk", StrictGeomUtility.toInternalValue( 102 ), nodes.getMinimumChunkWidth() );
    assertEquals( "Width", StrictGeomUtility.toInternalValue( 101 ), nodes.getWidth() );
  }

  public void testLayout() throws Exception {
    final MasterReport report = new MasterReport();
    report.setStrictLegacyMode( false );
    final Band band = new Band();
    band.setName( "Band" );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, 1f );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, 1f );
    band.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 101f );
    report.getReportHeader().addElement( band );

    band.addElement( createDataItem( "Header" ) );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );

    final RenderNode nodes = MatchFactory.match( logicalPageBox, "CanvasRenderBox > CanvasRenderBox" );
    assertNotNull( nodes );

    assertEquals( "MinChunk", StrictGeomUtility.toInternalValue( 102 ), nodes.getMinimumChunkWidth() );
    assertEquals( "Width", StrictGeomUtility.toInternalValue( 102 ), nodes.getWidth() );
  }
}
