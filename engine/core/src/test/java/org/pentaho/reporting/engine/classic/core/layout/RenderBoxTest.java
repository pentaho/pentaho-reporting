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
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;

/**
 * Creation-Date: 06.07.2007, 14:39:48
 *
 * @author Thomas Morgner
 */
public class RenderBoxTest extends TestCase {
  public RenderBoxTest( String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSingleElementReplacement() {
    final BlockRenderBox parent = new BlockRenderBox();
    final BlockRenderBox first = new BlockRenderBox();
    final BlockRenderBox second = new BlockRenderBox();
    final BlockRenderBox third = new BlockRenderBox();
    final BlockRenderBox fourth = new BlockRenderBox();

    parent.addChild( first );
    parent.addChild( second );
    parent.addChild( third );
    parent.addChild( fourth );

    final SpacerRenderNode replacementForFirst = new SpacerRenderNode();
    parent.replaceChild( first, replacementForFirst );
    assertNull( first.getNext() );
    assertNull( first.getPrev() );
    assertNull( first.getParent() );

    final SpacerRenderNode replacementForSecond = new SpacerRenderNode();
    parent.replaceChild( second, replacementForSecond );
    assertNull( second.getNext() );
    assertNull( second.getPrev() );
    assertNull( second.getParent() );

    final SpacerRenderNode replacementForThird = new SpacerRenderNode();
    parent.replaceChild( third, replacementForThird );
    assertNull( third.getNext() );
    assertNull( third.getPrev() );
    assertNull( third.getParent() );

    final SpacerRenderNode replacementForFourth = new SpacerRenderNode();
    parent.replaceChild( fourth, replacementForFourth );
    assertNull( fourth.getNext() );
    assertNull( fourth.getPrev() );
    assertNull( fourth.getParent() );
  }

  public void testMore() {
    final BlockRenderBox parent = new BlockRenderBox();
    final BlockRenderBox first = new BlockRenderBox();
    final BlockRenderBox second = new BlockRenderBox();
    final BlockRenderBox third = new BlockRenderBox();
    final BlockRenderBox fourth = new BlockRenderBox();

    parent.addChild( first );
    parent.addChild( second );
    parent.addChild( third );
    parent.addChild( fourth );

    final SpacerRenderNode replacementForFirst = new SpacerRenderNode();
    parent.replaceChilds( first, new RenderNode[] { first, replacementForFirst } );
    assertNull( first.getPrev() );
    assertTrue( replacementForFirst.getNext() == second );
    assertTrue( second.getPrev() == replacementForFirst );

    final SpacerRenderNode replacementForSecond = new SpacerRenderNode();
    parent.replaceChilds( second, new RenderNode[] { second, replacementForSecond } );

    assertTrue( replacementForFirst.getNext() == second );
    assertTrue( second.getPrev() == replacementForFirst );

    assertTrue( replacementForSecond.getNext() == third );
    assertTrue( replacementForSecond == third.getPrev() );

    final SpacerRenderNode replacementForThird = new SpacerRenderNode();
    parent.replaceChilds( third, new RenderNode[] { third, replacementForThird } );

    final SpacerRenderNode replacementForFourth = new SpacerRenderNode();
    parent.replaceChilds( fourth, new RenderNode[] { fourth, replacementForFourth } );
    assertNull( replacementForFourth.getNext() );
  }
}
