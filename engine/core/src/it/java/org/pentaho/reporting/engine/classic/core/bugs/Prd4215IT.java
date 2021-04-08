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
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.LogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontRegistry;
import org.pentaho.reporting.engine.classic.core.testsupport.graphics.TestGraphics2D;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Prd4215IT extends TestCase {
  public Prd4215IT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

//  @Ignore
//  public void testRichText() throws ReportProcessingException, ContentProcessingException {
//    final Element e = new Element();
//    e.setElementType( LabelType.INSTANCE );
//    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE, "text/html" );
//    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
//        "Hi I am trying to use the <b>rich text type = text/html</b> in PRD version - 3.7." );
//    e.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, 12 );
//    e.getStyle().setStyleProperty( TextStyleKeys.FONT, "Arial" );
//    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, 285f );
//    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 20f );
//
//    final MasterReport report = new MasterReport();
//    report.getReportHeader().addElement( e );
//    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
//        "false" );
//
//    final LogicalPageBox logicalPageBox =
//        DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );
//    logicalPageBox.getRepeatFooterArea().setY( logicalPageBox.getContentArea().getHeight() );
//    logicalPageBox.getFooterArea().setY( logicalPageBox.getContentArea().getHeight() );
//
//    // ModelPrinter.INSTANCE.print(logicalPageBox);
//
//    final RenderNode[] elementsByNodeType =
//        MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_NODE_TEXT );
//    assertEquals( 17, elementsByNodeType.length ); // quick and easy way to see that all elements are there.
//
//    // debugPrintText(elementsByNodeType);
//
//    final LocalFontRegistry registry = new LocalFontRegistry();
//    registry.initialize();
//
//    final GraphicsOutputProcessorMetaData metaData =
//        new GraphicsOutputProcessorMetaData( new DefaultFontStorage( registry ) );
//    metaData.initialize( report.getConfiguration() );
//
//    final LogicalPageDrawable drawable = new LogicalPageDrawable();
//    drawable.init( logicalPageBox, metaData, report.getResourceManager() );
//
//    final TracingGraphics g2 = new TracingGraphics();
//    drawable.draw( g2, new Rectangle2D.Double( 0, 0, 500, 500 ) );
//    /*
//     * for (int i = 0; i < g2.records.size(); i++) { final TextTraceRecord record = g2.records.get(i);
//     * System.out.println ("goldenSamples.add(new TextTraceRecord(" + record.x + ", " + record.y + ", \"" + record .text
//     * +"\"));"); }
//     */
//    assertEquals( getSamples(), g2.records );
//  }

  private List<TextTraceRecord> getSamples() {
    ArrayList<TextTraceRecord> goldenSamples = new ArrayList<TextTraceRecord>();
    goldenSamples.add( new TextTraceRecord( 0, 12, "Hi" ) );
    goldenSamples.add( new TextTraceRecord( 15, 12, "I" ) );
    goldenSamples.add( new TextTraceRecord( 21, 12, "am" ) );
    goldenSamples.add( new TextTraceRecord( 41, 12, "trying" ) );
    goldenSamples.add( new TextTraceRecord( 74, 12, "to" ) );
    goldenSamples.add( new TextTraceRecord( 87, 12, "use" ) );
    goldenSamples.add( new TextTraceRecord( 110, 12, "the" ) );
    goldenSamples.add( new TextTraceRecord( 130, 12, "rich" ) );
    goldenSamples.add( new TextTraceRecord( 153, 12, "text" ) );
    goldenSamples.add( new TextTraceRecord( 175, 12, "type" ) );
    goldenSamples.add( new TextTraceRecord( 201, 12, "=" ) );
    goldenSamples.add( new TextTraceRecord( 212, 12, "text/html" ) );
    goldenSamples.add( new TextTraceRecord( 260, 12, "in" ) );
    goldenSamples.add( new TextTraceRecord( 0, 24, "PRD" ) );
    goldenSamples.add( new TextTraceRecord( 29, 24, "version" ) );
    goldenSamples.add( new TextTraceRecord( 71, 24, "-" ) );
    goldenSamples.add( new TextTraceRecord( 78, 24, "3.7." ) );
    return goldenSamples;
  }

  private static class TextTraceRecord {
    public int x;
    public int y;
    public String text;

    private TextTraceRecord( final int x, final int y, final String text ) {
      this.x = x;
      this.y = y;
      this.text = text;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final TextTraceRecord that = (TextTraceRecord) o;

      if ( x != that.x ) {
        return false;
      }
      if ( y != that.y ) {
        return false;
      }
      if ( !text.equals( that.text ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = x;
      result = 31 * result + y;
      result = 31 * result + text.hashCode();
      return result;
    }

    public String toString() {
      return "TextTraceRecord{" + "text='" + text + '\'' + ", x=" + x + ", y=" + y + '}';
    }
  }

  private static class TracingGraphics extends TestGraphics2D {
    private ArrayList<TextTraceRecord> records;

    private TracingGraphics() {
      records = new ArrayList<TextTraceRecord>();
    }

    public void drawString( final String str, final float x, final float y ) {
      records.add( new TextTraceRecord( StrictMath.round( x ), StrictMath.round( y ), str ) );
    }
  }

  private void debugPrintText( final RenderNode[] elementsByNodeType ) {
    for ( int i = 0; i < elementsByNodeType.length; i++ ) {
      final RenderableText renderNode = (RenderableText) elementsByNodeType[i];
      DebugLog.log( renderNode.getRawText() );
    }
  }
}
