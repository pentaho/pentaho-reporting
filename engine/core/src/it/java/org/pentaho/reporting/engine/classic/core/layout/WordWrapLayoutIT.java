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

package org.pentaho.reporting.engine.classic.core.layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.mockito.internal.matchers.LessOrEqual;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY;

/*
 * A tip for debugging:
 * ModelPrinter.INSTANCE.print() is useful for printing layout to console
 */
public class WordWrapLayoutIT {

  private static final String LONG_WORD = "a-long-word-where-parts-are-separated-with-hyphen";
  private static final String SHORT_WORD = "word";

  private static final float LABEL_MAX_WIDTH_NORMAL = 40;
  private static final float LABEL_MAX_WIDTH_TINY = 1;

  private static final String PARAGRAPH_NAME = "paragraph-for-picking-up";

  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void isBreakingWords_WhenWordBreakPropertyIsTrue() throws Exception {
    MasterReport report = createReport( true );
    ParagraphRenderBox paragraph = pickupParagraph( report );
    List<RenderableText> textNodes = pickupTextNodes( paragraph );
    assertTextNodesLayInsideBoxBounds( paragraph, textNodes );
  }

  @Test
  public void isNotBreakingWords_WhenWordBreakPropertyIsFalse() throws Exception {
    MasterReport report = createReport( false );
    ParagraphRenderBox paragraph = pickupParagraph( report );
    List<RenderableText> textNodes = pickupTextNodes( paragraph );
    assertEquals( "The only word should not be split", 1, textNodes.size() );
  }

  private static MasterReport createReport( boolean allowWordBreak ) {
    return createReport( LONG_WORD, allowWordBreak, LABEL_MAX_WIDTH_NORMAL );
  }

  @Test
  public void isNotBreakingWords_WhenWordFitsIntoBounds() throws Exception {
    MasterReport report = createReport( SHORT_WORD );
    ParagraphRenderBox paragraph = pickupParagraph( report );
    List<RenderableText> textNodes = pickupTextNodes( paragraph );
    assertTextNodesLayInsideBoxBounds( paragraph, textNodes );
    assertEquals( "The word is too short to be split", 1, textNodes.size() );
  }

  private static MasterReport createReport( String word ) {
    return createReport( word, true, LABEL_MAX_WIDTH_NORMAL );
  }

  @Test
  public void isNotBreakingWords_WhenThereIsNoEnoughSpaceEvenForOneChar() throws Exception {
    MasterReport report = createReport( LONG_WORD, true, LABEL_MAX_WIDTH_TINY );
    ParagraphRenderBox paragraph = pickupParagraph( report );
    List<RenderableText> textNodes = pickupTextNodes( paragraph );
    assertEquals( "The split should be not done as label's max width is not enough to contain even one char", 1,
        textNodes.size() );
  }

  @Test
  public void isBreakingWords_SimilarlyForAllAlignments_LeftVsRight() throws Exception {
    testBreakingWordsIsSimilarForLeftAnd( ElementAlignment.RIGHT );
  }

  @Test
  public void isBreakingWords_SimilarlyForAllAlignments_LeftVsCenter() throws Exception {
    testBreakingWordsIsSimilarForLeftAnd( ElementAlignment.CENTER );
  }

  @Test
  public void isBreakingWords_SimilarlyForAllAlignments_LeftVsJustify() throws Exception {
    testBreakingWordsIsSimilarForLeftAnd( ElementAlignment.JUSTIFY );
  }

  /*
   * Word breaks are the last action done over a word to cram it into its container's bounds. Hence word-breaking should
   * be done over the word similarly regardless its alignment.
   * 
   * Important! The assumption above is true for the ideal case, which means that the text should be a single word.
   * Otherwise it is difficult to predict final layout
   */
  private void testBreakingWordsIsSimilarForLeftAnd( ElementAlignment alignment ) throws Exception {
    ParagraphRenderBox paragraphLeft = pickupParagraph( createReport( ElementAlignment.LEFT ) );
    List<RenderableText> textNodesLeft = pickupTextNodes( paragraphLeft );

    ParagraphRenderBox paragraphA = pickupParagraph( createReport( alignment ) );
    List<RenderableText> textNodesA = pickupTextNodes( paragraphA );

    assertTextNodesAreSimilar( textNodesLeft, textNodesA );
  }

  private static MasterReport createReport( ElementAlignment alignment ) {
    return createReport( LONG_WORD, true, LABEL_MAX_WIDTH_NORMAL, alignment );
  }

  private void assertTextNodesAreSimilar( List<RenderableText> first, List<RenderableText> second ) throws Exception {
    // similarity means:
    // 1) amounts of chunks are equal
    // 2) each pair of chunks refers to the same chars
    assertEquals( first.size(), second.size() );
    for ( int i = 0; i < first.size(); i++ ) {
      assertEquals( first.get( i ).getRawText(), second.get( i ).getRawText() );
    }
  }

  private static MasterReport createReport( String word, boolean allowWordBreak, float labelMaxWidth ) {
    return createReport( word, allowWordBreak, labelMaxWidth, ElementAlignment.LEFT );
  }

  private static MasterReport createReport( String word, boolean allowWordBreak, float labelMaxWidth,
      ElementAlignment alignment ) {
    MasterReport report = new MasterReport();
    // force not to use complex text processing
    report.getReportConfiguration().setConfigProperty( COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "false" );

    PageFormatFactory pff = PageFormatFactory.getInstance();
    Paper paper = pff.createPaper( PageSize.A4 );
    pff.setBorders( paper, 36.0, 36.0, 36.0, 36.0 );
    PageFormat format = pff.createPageFormat( paper, PageFormat.PORTRAIT );
    report.setPageDefinition( new SimplePageDefinition( format ) );

    Band pageHeader = report.getPageHeader();
    pageHeader.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 10.0f );

    // the main heading is just a fixed label
    LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setText( word );
    labelFactory.setFontName( "SansSerif" );
    labelFactory.setFontSize( 10 );
    labelFactory.setDynamicHeight( true );
    // will be useful when printing the page on a real monitor as it highlights paragraph's area
    labelFactory.setBackgroundColor( Color.YELLOW );
    labelFactory.setAbsolutePosition( new Point2D.Double( 15, 10 ) );
    labelFactory.setMinimumSize( new FloatDimension( 40, 10 ) );
    labelFactory.setMaximumWidth( labelMaxWidth );
    labelFactory.setHorizontalAlignment( alignment );

    Element element = labelFactory.createElement();
    element.setName( PARAGRAPH_NAME );
    element.getStyle().setStyleProperty( TextStyleKeys.WORDBREAK, allowWordBreak );
    pageHeader.addElement( element );

    return report;
  }

  private static ParagraphRenderBox pickupParagraph( MasterReport report ) throws Exception {
    LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand( report, report.getPageHeader(), false, false );
    RenderNode elementByName = MatchFactory.findElementByName( pageBox, PARAGRAPH_NAME );

    assertThat( elementByName, is( instanceOf( ParagraphRenderBox.class ) ) );
    return (ParagraphRenderBox) elementByName;
  }

  private static List<RenderableText> pickupTextNodes( RenderBox box ) {
    List<RenderableText> textNodes = new ArrayList<RenderableText>();
    RenderNode firstChild = box.getFirstChild();
    while ( firstChild != null ) {
      if ( firstChild instanceof RenderBox ) {
        textNodes.addAll( pickupTextNodes( (RenderBox) firstChild ) );
      } else if ( firstChild instanceof RenderableText ) {
        textNodes.add( (RenderableText) firstChild );
      }

      firstChild = firstChild.getNext();
    }
    return textNodes;
  }

  private void assertTextNodesLayInsideBoxBounds( RenderBox box, List<RenderableText> texts ) {
    assertFalse( texts.isEmpty() );

    GreaterOrEqual<Long> greaterThanBoxX = new GreaterOrEqual<Long>( box.getX() );
    LessOrEqual<Long> lessThanBoxWidth = new LessOrEqual<Long>( box.getWidth() );
    for ( RenderableText text : texts ) {
      assertThat( text.getX(), is( greaterThanBoxX ) );
      assertThat( text.getWidth(), is( lessThanBoxWidth ) );
    }
  }

}
