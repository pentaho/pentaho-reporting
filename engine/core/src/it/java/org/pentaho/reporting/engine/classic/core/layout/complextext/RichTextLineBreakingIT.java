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

package org.pentaho.reporting.engine.classic.core.layout.complextext;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import static org.junit.Assert.*;

public class RichTextLineBreakingIT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testWeirdTocLayoutComplex() throws ReportProcessingException, ContentProcessingException {

    if ( !DebugReportRunner.isSafeToTestComplexText() ) {
      return;
    }

    Element textField = new Element();
    textField.setName( "textField" );
    textField.getStyle().setStyleProperty( TextStyleKeys.FONT, "Arial" );
    textField.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 14 );
    textField.getStyle().setStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.NONE );
    textField.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 97f );
    textField.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f );
    textField.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 0f );
    textField.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );
    textField.setElementType( LabelType.INSTANCE );
    textField.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Classic Cars\n" );

    Element dotField = new Element();
    dotField.setName( "dotField" );
    dotField.getStyle().setStyleProperty( TextStyleKeys.FONT, "Arial" );
    dotField.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 14 );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.TOP );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.POS_X, 97f );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, 0f );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 628.463f );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 20f );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, 100f );
    dotField.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, 100f );
    dotField.setElementType( LabelType.INSTANCE );
    dotField.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, " Some more data" );

    Band band = new Band();
    band.setName( "outer-box" );
    band.setLayout( "inline" );
    band.getStyle().setStyleProperty( ElementStyleKeys.BOX_SIZING, BoxSizing.CONTENT_BOX );
    band.getStyle().setStyleProperty( ElementStyleKeys.OVERFLOW_X, false );
    band.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, true );
    band.getStyle().setStyleProperty( ElementStyleKeys.OVERFLOW_Y, false );
    band.getStyle().setStyleProperty( TextStyleKeys.LINEHEIGHT, 1f );
    band.getStyle().setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, WhitespaceCollapse.PRESERVE_BREAKS );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 708f );
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 12f );
    band.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, 20f );
    band.addElement( textField );
    band.addElement( dotField );

    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement( band );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    report.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, true );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );

    ParagraphRenderBox outerBox = (ParagraphRenderBox) MatchFactory.findElementByName( logicalPageBox, "outer-box" );
    assertNotNull( outerBox );
    assertEquals( 0, outerBox.getY() );
    assertTrue( outerBox.getHeight() > StrictGeomUtility.toInternalValue( 14 ) );
    assertTrue( outerBox.isComplexParagraph() );

    BlockRenderBox pool = (BlockRenderBox) outerBox.getLineboxContainer();
    assertTrue( pool.getFirstChild() != pool.getLastChild() );
    assertTrue( pool.getFirstChild().getNext() == pool.getLastChild() );
    assertStructure( (RenderBox) pool.getFirstChild() );
    assertStructure( (RenderBox) pool.getLastChild() );

  }

  private void assertStructure( RenderBox box ) {
    assertTrue( box.getFirstChild() instanceof InlineRenderBox );
    assertTrue( box.getFirstChild() == box.getLastChild() );

    RenderBox child = (RenderBox) box.getFirstChild();
    assertTrue( child.getFirstChild() instanceof RenderableComplexText );
    assertTrue( child.getFirstChild() == child.getLastChild() );

  }
}
