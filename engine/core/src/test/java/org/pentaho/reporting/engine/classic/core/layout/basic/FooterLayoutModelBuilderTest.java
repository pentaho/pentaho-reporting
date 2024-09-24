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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.basic;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultLayoutBuilderStrategy;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultLayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.build.DefaultRenderNodeFactory;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutBuilderStrategy;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.build.RichTextStyleResolver;

import java.util.ArrayList;

public class FooterLayoutModelBuilderTest extends TestCase {
  public FooterLayoutModelBuilderTest() {
  }

  public FooterLayoutModelBuilderTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testComplexFooter() throws ReportProcessingException {
    final DefaultLayoutModelBuilder builder = new DefaultLayoutModelBuilder();
    builder.setLimitedSubReports( true );
    builder.setCollapseProgressMarker( false );

    final RichTextStyleResolver resolver =
            new RichTextStyleResolver( new DefaultProcessingContext(), new MasterReport() );

    final LayoutBuilderStrategy builderStrategy = new DefaultLayoutBuilderStrategy( resolver );
    final DefaultRenderNodeFactory renderNodeFactory = new DefaultRenderNodeFactory();
    renderNodeFactory.initialize( new DebugOutputProcessorMetaData() );

    final RenderBox parentBox = new BlockRenderBox();
    builder.initialize( new DefaultProcessingContext(), parentBox, renderNodeFactory );
    builder.startSection();

    ReportFooter reportFooter = new ReportFooter();
    reportFooter.setComputedStyle( new SimpleStyleSheet( reportFooter.getDefaultStyleSheet() ) );
    builderStrategy.add( new DebugExpressionRuntime(), builder, reportFooter, new ArrayList<InlineSubreportMarker>() );
    builderStrategy.add( new DebugExpressionRuntime(), builder, reportFooter, new ArrayList<InlineSubreportMarker>() );
    builderStrategy.add( new DebugExpressionRuntime(), builder, reportFooter, new ArrayList<InlineSubreportMarker>() );

    builder.endSection();

    assertEquals( 3, countChilds( (RenderBox) parentBox.getFirstChild() ) );
  }

  public void testMergingProgressMarker() throws ReportProcessingException {
    final DefaultLayoutModelBuilder builder = new DefaultLayoutModelBuilder();
    builder.setLimitedSubReports( true );
    builder.setCollapseProgressMarker( true );

    final DefaultRenderNodeFactory renderNodeFactory = new DefaultRenderNodeFactory();
    renderNodeFactory.initialize( new DebugOutputProcessorMetaData() );

    final RenderBox parentBox = new BlockRenderBox();
    builder.initialize( new DefaultProcessingContext(), parentBox, renderNodeFactory );
    builder.startSection();

    builder.addProgressMarkerBox();
    builder.addProgressMarkerBox();
    builder.addProgressMarkerBox();

    builder.endSection();

    assertEquals( 1, countChilds( (RenderBox) parentBox.getFirstChild() ) );
  }

  public int countChilds( RenderBox b ) {
    int count = 0;
    RenderNode child = b.getFirstChild();
    while ( child != null ) {
      count += 1;
      child = child.getNext();
    }
    return count;
  }

}
