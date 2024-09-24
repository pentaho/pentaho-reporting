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
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.net.URL;

public class Prd3479IT extends TestCase {
  public Prd3479IT() {
    // The inner band's child have a y=1pt and height = 12pt while the outer parent has a height of 12 @ y = 0
  }

  public Prd3479IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ResourceException {
    final URL url = getClass().getResource( "Prd-3479.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    DebugReportRunner.execGraphics2D( report );

    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    final PreviewDialog previewDialog = new PreviewDialog( report );
    previewDialog.pack();
    previewDialog.setModal( true );
    previewDialog.setVisible( true );
  }

  private Band createBand( final String name, final float y, final float height ) {
    final Band b = new Band();
    b.setName( name );
    b.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 100f );
    b.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, height );
    b.getStyle().setStyleProperty( ElementStyleKeys.POS_Y, y );
    return b;
  }

  public void testCanvasLayout() throws ReportProcessingException, ContentProcessingException {

    final MasterReport report = new MasterReport();
    final ReportHeader reportHeader = report.getReportHeader();
    reportHeader.addElement( createBand( "large", 0, 100 ) );
    reportHeader.addElement( createBand( "rel", -25, -50 ) );

    // Each character (regarless of font or font-size) will be 8pt high and 4pt wide.
    // this makes this test independent of the fonts installed on the system we run on.

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, reportHeader );

    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );
  }

  private static class ValidateRunner extends IterateSimpleStructureProcessStep {
    protected boolean startBox( final RenderBox node ) {
      final String s = node.getName();
      if ( "large".equals( s ) ) {
        // inline elements take the intrinsinc width/height unless explicitly defined otherwise
        assertEquals( "Rect height=100pt; " + node.getName(), StrictGeomUtility.toInternalValue( 100 ), node
            .getCachedHeight() );
      } else if ( "rel".equals( s ) ) {
        assertEquals( "Rect height=50pt; " + node.getName(), StrictGeomUtility.toInternalValue( 50 ), node
            .getCachedHeight() );
        assertEquals( "Rect y=25pt; " + node.getName(), StrictGeomUtility.toInternalValue( 25 ), node.getCachedY() );
      }
      return true;
    }

    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }
  }

  public void testRowInCanvas() throws ReportProcessingException, ContentProcessingException {
    final Element label = new Element();
    label.setName( "Label" );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, -100f );
    label.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -33f );
    label.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, true );
    label.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label" );
    label.setElementType( LabelType.INSTANCE );

    final Band rowBand = new Band();
    rowBand.setName( "RowBand" );
    rowBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.MIDDLE );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
    rowBand.addElement( label );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 2f );
    band.addElement( rowBand );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band );
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final RenderNode labelRenderBox = MatchFactory.findElementByName( logicalPageBox, "Label" );
    final RenderNode rowRenderBox = MatchFactory.findElementByName( logicalPageBox, "RowBand" );
    assertEquals( StrictGeomUtility.toInternalValue( 468 ), rowRenderBox.getWidth() );
    assertEquals( StrictGeomUtility.toInternalValue( 154.44 ), labelRenderBox.getWidth() );
  }

  public void testEmptyCanvasBoxInRow() throws ReportProcessingException, ContentProcessingException {
    final Band canvasBand = new Band();
    canvasBand.setName( "CanvasBand" );
    canvasBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "canvas" );
    canvasBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 1f );

    final Band rowBand = new Band();
    rowBand.setName( "RowBand" );
    rowBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 1f );
    rowBand.addElement( canvasBand );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 2f );
    band.setLayout( "block" );
    band.addElement( rowBand );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band );
    final RenderNode canvasBandRenderBox = MatchFactory.findElementByName( logicalPageBox, "CanvasBand" );
    assertEquals( 0, canvasBandRenderBox.getWidth() );
  }

  public void testEmptyCanvasBoxInCanvas() throws ReportProcessingException, ContentProcessingException {
    final Band canvasBand = new Band();
    canvasBand.setName( "CanvasBand" );
    canvasBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "canvas" );
    canvasBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 1f );

    final Band rowBand = new Band();
    rowBand.setName( "RowBand" );
    rowBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "canvas" );
    rowBand.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 1f );
    rowBand.addElement( canvasBand );

    final MasterReport report = new MasterReport();
    final ReportHeader band = report.getReportHeader();
    band.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 2f );
    band.setLayout( "block" );
    band.addElement( rowBand );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, band );
    final RenderNode canvasBandRenderBox = MatchFactory.findElementByName( logicalPageBox, "CanvasBand" );
    assertEquals( 0, canvasBandRenderBox.getWidth() );
  }
}
