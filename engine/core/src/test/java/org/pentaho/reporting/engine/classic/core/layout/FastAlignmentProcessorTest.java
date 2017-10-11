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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.DefaultPageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.FastAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.DefaultSequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SpacerSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.layout.text.RenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;

import java.awt.print.PageFormat;

public class FastAlignmentProcessorTest extends TestCase {
  private DebugOutputProcessorMetaData outputProcessorMetaData;
  private PageGrid pageGrid;

  public FastAlignmentProcessorTest() {
  }

  public FastAlignmentProcessorTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    outputProcessorMetaData = new DebugOutputProcessorMetaData();
    pageGrid = new DefaultPageGrid( new SimplePageDefinition( new PageFormat() ) );
  }

  private RenderNode createText( String text ) {
    final CodePointBuffer buffer = Utf16LE.getInstance().decodeString( text, null );
    final int[] bufferArray = buffer.getBuffer( new int[0] );

    final RenderableTextFactory textFactory = new DefaultRenderableTextFactory( outputProcessorMetaData );
    textFactory.startText();

    final RenderNode[] renderNodes =
        textFactory.createText( bufferArray, 0, buffer.getLength(), SimpleStyleSheet.EMPTY_STYLE,
            AutoLayoutBoxType.INSTANCE, new InstanceID(), ReportAttributeMap.EMPTY_MAP );
    final RenderNode[] finishNodes = textFactory.finishText();
    if ( renderNodes.length > 0 ) {
      return renderNodes[0];
    }
    return finishNodes[0];
  }

  public void testSimpleCase() {
    final DefaultSequenceList list = new DefaultSequenceList();
    list.add( StartSequenceElement.INSTANCE, new InlineRenderBox() );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode() );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( EndSequenceElement.INSTANCE, new InlineRenderBox() );

    final FastAlignmentProcessor p = new FastAlignmentProcessor();
    p.initialize( outputProcessorMetaData, list, StrictGeomUtility.toInternalValue( 10 ), StrictGeomUtility
        .toInternalValue( 90 ), pageGrid, false );
    assertTrue( p.hasNext() );
    final RenderBox next = p.next();

    // ModelPrinter.print(next);
  }

  public void testComplexCase2() {
    final DefaultSequenceList list = new DefaultSequenceList();
    list.add( StartSequenceElement.INSTANCE, new InlineRenderBox() );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( EndSequenceElement.INSTANCE, new InlineRenderBox() );

    final FastAlignmentProcessor p = new FastAlignmentProcessor();
    p.initialize( outputProcessorMetaData, list, StrictGeomUtility.toInternalValue( 10 ), StrictGeomUtility
        .toInternalValue( 60 ), pageGrid, false );
    int count = 0;
    while ( p.hasNext() ) {
      count += 1;
      final RenderBox box = p.next();
      assertNotNull( box.getFirstChild() );
      assertFalse( box.getFirstChild() instanceof SpacerRenderNode );
      assertFalse( box.getLastChild() instanceof SpacerRenderNode );
      // ModelPrinter.print(box);
    }
    assertEquals( 4, count );
  }

  public void testComplexCase() {
    final DefaultSequenceList list = new DefaultSequenceList();
    list.add( StartSequenceElement.INSTANCE, new InlineRenderBox() );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( SpacerSequenceElement.INSTANCE, new SpacerRenderNode( StrictGeomUtility.toInternalValue( 10 ),
        StrictGeomUtility.toInternalValue( 10 ), false, 1 ) );
    list.add( TextSequenceElement.INSTANCE, createText( "Test" ) );
    list.add( EndSequenceElement.INSTANCE, new InlineRenderBox() );

    final FastAlignmentProcessor p = new FastAlignmentProcessor();
    p.initialize( outputProcessorMetaData, list, StrictGeomUtility.toInternalValue( 10 ), StrictGeomUtility
        .toInternalValue( 40 ), pageGrid, false );
    int count = 0;
    while ( p.hasNext() ) {
      count += 1;
      p.next();
    }
    assertEquals( 8, count );
  }
}
