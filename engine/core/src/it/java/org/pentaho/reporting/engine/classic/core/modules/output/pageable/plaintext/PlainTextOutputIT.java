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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;

public class PlainTextOutputIT extends TestCase {
  public static final String LONG_TEXT_LABEL =
      "Customer very concerned about the exact color of the models. There is high risk that he may dispute the order "
          + "because there is a slight color mismatch";

  public PlainTextOutputIT() {
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testFontFactoryCaching() {
    assertEquals( 72 * 1000 / 10, calculateFontMetrics( 6, 10 ).getMaxHeight() );
    assertEquals( 72 * 1000 / 6, calculateFontMetrics( 6, 10 ).getCharWidth( 'm' ) );
    assertEquals( 72 * 1000 / 15, calculateFontMetrics( 9, 15 ).getMaxHeight() );
    assertEquals( 72 * 1000 / 9, calculateFontMetrics( 9, 15 ).getCharWidth( 'm' ) );
  }

  private FontMetrics calculateFontMetrics( final int cpi, final int lpi ) {
    final TextFilePrinterDriver pc = new TextFilePrinterDriver( new NullOutputStream(), cpi, lpi );
    final PageableTextOutputProcessor outputProcessor =
        new PageableTextOutputProcessor( pc, ClassicEngineBoot.getInstance().getGlobalConfig() );
    outputProcessor.setEncoding( "UTF-8" );

    return outputProcessor.getMetaData().getFontMetrics( ElementDefaultStyleSheet.getDefaultStyle() );
  }

  public void testElementSizes() throws Exception {
    final MasterReport report = createStandardReport( LONG_TEXT_LABEL );
    final LogicalPageBox pageBox =
        DebugReportRunner.layoutSingleBand( report, report.getPageHeader(), new DefaultFontStorage(
            new MonospaceFontRegistry( 10, 6 ) ), false );

    final RenderBox labelElement = (RenderBox) MatchFactory.findElementByName( pageBox, "LabelElement" );
    assertEquals( StrictGeomUtility.toInternalValue( 26 ), labelElement.getHeight() );
    assertEquals( StrictGeomUtility.toInternalValue( 4 ), labelElement.getY() );

    // next block: Assert that all lines are properly aligned and not overlapping.
    long expectedY = labelElement.getY();
    RenderNode lineBox = labelElement.getFirstChild();
    assertNotNull( lineBox );
    while ( lineBox != null ) {
      // 10 lines per inch (1 inch == 72 point) makes each line 7.2 point in height.
      assertEquals( StrictGeomUtility.toInternalValue( 72 / 10f ), lineBox.getHeight() );
      assertEquals( expectedY, lineBox.getY() );
      expectedY += lineBox.getHeight();
      lineBox = lineBox.getNext();
    }
  }

  public void testTextExport() throws Exception {
    final int lpi = 10;
    final int cpi = 6;

    final MasterReport report = createStandardReport( LONG_TEXT_LABEL );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    final LogicalPageBox pageBox =
        DebugReportRunner.layoutSingleBand( report, report.getPageHeader(), new DefaultFontStorage(
            new MonospaceFontRegistry( lpi, cpi ) ), false );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final TextFilePrinterDriver pc = new TextFilePrinterDriver( out, cpi, lpi );
    final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor( pc, report.getConfiguration() );
    OutputProcessorMetaData metaData = outputProcessor.getMetaData();
    metaData.initialize( report.getConfiguration() );
    final TextDocumentWriter writer = new TextDocumentWriter( metaData, pc, "ISO-8859-1" );
    writer.open();
    writer.processPhysicalPage( pageBox.getPageGrid(), pageBox, 0, 0, null );
    writer.close();

    final StyleSheet style = pageBox.getStyleSheet();
    final String ellipse = (String) style.getStyleProperty( TextStyleKeys.RESERVED_LITERAL, null );
    final String truncatedString = out.toString( "ISO-8859-1" ).trim().replaceAll( "[\\t\\n\\r|(  )+]+", " " );
    assertTrue( truncatedString.startsWith( "Customer" ) );
    assertTrue( truncatedString.endsWith( "slight " + ellipse ) );
  }

  public void testExportWithLabel() throws Exception {
    final MasterReport report = createStandardReport( LONG_TEXT_LABEL );

    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    PlainTextReportUtil.createPlainText( report, bo, 10, 6 );
    final byte[] data = bo.toByteArray();

    assertEquals( LONG_TEXT_LABEL, new String( data ).trim().replaceAll( "[\\t\\n\\r|(  )+]+", " " ) );
  }

  private MasterReport createStandardReport( final String longTextLabel ) {
    final MasterReport report = new MasterReport();
    report
        .setPageDefinition( new SimplePageDefinition( PageSize.A4, PageFormat.LANDSCAPE, new Insets( 72, 72, 72, 72 ) ) );
    report.setCompatibilityLevel( null );
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );

    final Band pageHeader = report.getPageHeader();
    final LabelElementFactory labelFactory = new LabelElementFactory();
    labelFactory.setName( "LabelElement" );
    labelFactory.setText( longTextLabel );
    labelFactory.setFontName( "Serif" );
    labelFactory.setFontSize( new Integer( 10 ) );
    labelFactory.setBold( Boolean.FALSE );
    labelFactory.setHeight( 26.0F );
    labelFactory.setWidth( 568.0F );
    labelFactory.setWrap( TextWrap.WRAP );
    labelFactory.setAbsolutePosition( new Point2D.Double( 2.0, 4.0 ) );
    labelFactory.setHorizontalAlignment( ElementAlignment.LEFT );
    labelFactory.setVerticalAlignment( ElementAlignment.TOP );
    final Element labelElement = labelFactory.createElement();
    pageHeader.addElement( labelElement );
    return report;
  }
}
