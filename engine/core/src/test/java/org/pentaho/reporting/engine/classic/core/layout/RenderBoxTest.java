/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
