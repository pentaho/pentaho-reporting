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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4295IT extends TestCase {
  public Prd4295IT() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBold() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-3857-002.prpt" );
    masterReport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    masterReport.setQueryLimit( 1 );
    final CrosstabCellBody crosstabCellBody = masterReport.getCrosstabCellBody();
    final CrosstabCell element = crosstabCellBody.findElement( null, null );
    element.setName( "Cell-Sample" );
    element.getStyle().setStyleProperty( TextStyleKeys.BOLD, true );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( masterReport, 0 );
    final RenderNode[] elementsByName = MatchFactory.findElementsByName( logicalPageBox, "Cell-Sample" );
    assertTrue( elementsByName.length > 0 );
    final RenderNode renderNode = elementsByName[0];
    assertEquals( Boolean.TRUE, renderNode.getNodeLayoutProperties().getStyleSheet().getStyleProperty(
        TextStyleKeys.BOLD ) );

    // ModelPrinter.INSTANCE.print(renderNode);
    final RenderNode[] texts = MatchFactory.findElementsByNodeType( renderNode, LayoutNodeTypes.TYPE_NODE_TEXT );
    assertEquals( Boolean.TRUE, texts[0].getStyleSheet().getStyleProperty( TextStyleKeys.BOLD ) );
    assertEquals( 1945000, texts[0].getMinimumChunkWidth() );

    DebugReportRunner.showDialog( masterReport );
  }

  public void testBoldComplex() throws Exception {
    final MasterReport masterReport = DebugReportRunner.parseGoldenSampleReport( "Prd-3857-002.prpt" );
    masterReport.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "true" );
    masterReport.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, true );
    masterReport.setQueryLimit( 1 );
    final CrosstabCellBody crosstabCellBody = masterReport.getCrosstabCellBody();
    final CrosstabCell element = crosstabCellBody.findElement( null, null );
    element.setName( "Cell-Sample" );
    element.getStyle().setStyleProperty( TextStyleKeys.BOLD, true );
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( masterReport, 0 );
    final RenderNode[] elementsByName = MatchFactory.findElementsByName( logicalPageBox, "Cell-Sample" );
    assertTrue( elementsByName.length > 0 );
    final RenderNode renderNode = elementsByName[0];
    assertEquals( Boolean.TRUE, renderNode.getNodeLayoutProperties().getStyleSheet().getStyleProperty(
        TextStyleKeys.BOLD ) );

    // ModelPrinter.INSTANCE.print(renderNode);
    final RenderNode[] texts = MatchFactory.findElementsByNodeType( renderNode, LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT );
    assertEquals( Boolean.TRUE, texts[0].getStyleSheet().getStyleProperty( TextStyleKeys.BOLD ) );
    assertTrue( texts[0].getWidth() > 0 );
    // complex-text nodes no longer have a min-chunk width after line-breaking
    assertEquals( texts[0].getWidth(), extractOriginalPool( texts[0] ).getMinimumChunkWidth() );

    DebugReportRunner.showDialog( masterReport );
  }

  private RenderNode extractOriginalPool( RenderNode n ) {
    if ( n instanceof ParagraphRenderBox ) {
      ParagraphRenderBox para = (ParagraphRenderBox) n;
      return para.getPool();
    }
    if ( n.getParent() == null ) {
      Assert.fail();
      return null;
    }
    return extractOriginalPool( n.getParent() );
  }

}
