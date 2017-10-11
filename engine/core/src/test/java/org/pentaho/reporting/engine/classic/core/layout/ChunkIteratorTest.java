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
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.AlignmentChunk;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.ChunkIterator;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.DefaultSequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;

import java.util.NoSuchElementException;

public class ChunkIteratorTest extends TestCase {
  private static class DummyRenderNode extends RenderNode {
    private DummyRenderNode() {
      super( NodeLayoutProperties.GENERIC_PROPERTIES );
    }

    public int getNodeType() {
      return LayoutNodeTypes.TYPE_NODE_TEXT;
    }
  }

  public ChunkIteratorTest() {
  }

  public ChunkIteratorTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testIterateEmpty() {
    final ChunkIterator it = new ChunkIterator( new DefaultSequenceList(), 0 );
    assertFalse( it.hasNext() );
    try {
      it.next();
      fail();
    } catch ( NoSuchElementException e ) {
      // expected
    } catch ( Throwable t ) {
      fail();
    }
  }

  private void assertNext( final ChunkIterator it, final int start, final int length, final long width ) {
    assertTrue( it.hasNext() );

    final AlignmentChunk next = it.next();
    assertNotNull( next );
    assertEquals( length, next.getLength() );
    assertEquals( start, next.getStart() );
    assertEquals( start + length, next.getEnd() );
    assertEquals( width, next.getWidth() );
  }

  public void testSequenceBuilding1() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 2, 20 );
    assertNext( it, 2, 2, 20 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuilding3() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 3, 30 );
    assertNext( it, 3, 3, 30 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuilding2() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 2, 20 );
    assertNext( it, 2, 1, 10 );
    assertNext( it, 3, 2, 20 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuildingSpacer() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 2, 20 );
    assertNext( it, 2, 3, 30 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuilding4() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 2, 20 );
    assertNext( it, 2, 1, 10 );
    assertNext( it, 3, 3, 30 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuilding5() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 3, 30 );
    assertNext( it, 3, 3, 30 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuildingSingle1() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 2, 20 );
    assertFalse( it.hasNext() );
  }

  public void testSequenceBuildingSingle2() {
    final SequenceList list = new DefaultSequenceList();
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.START ), new SpacerRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.CONTENT ), new DummyRenderNode() );
    list.add( new TestInlineSequenceElement( InlineSequenceElement.Classification.END ), new SpacerRenderNode() );

    final ChunkIterator it = new ChunkIterator( list, 0 );
    assertNext( it, 0, 3, 30 );
    assertFalse( it.hasNext() );
  }
}
