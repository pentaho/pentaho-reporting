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
import org.pentaho.reporting.engine.classic.core.layout.model.AutoRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBoxNonAutoIterator;

public class RenderBoxNonAutoIteratorTest extends TestCase {
  public RenderBoxNonAutoIteratorTest() {
  }

  public RenderBoxNonAutoIteratorTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testIteration() {
    BlockRenderBox payload = new BlockRenderBox();

    AutoRenderBox auto = new AutoRenderBox();
    auto.addChild( new AutoRenderBox() );
    auto.addChild( payload );

    BlockRenderBox box = new BlockRenderBox();
    box.addChild( auto );

    RenderBoxNonAutoIterator it = new RenderBoxNonAutoIterator( box );
    assertTrue( it.hasNext() );
    assertSame( payload, it.next() );
  }

  public void testIteration2() {
    BlockRenderBox payload = new BlockRenderBox();

    AutoRenderBox auto = new AutoRenderBox();
    auto.addChild( payload );

    BlockRenderBox box = new BlockRenderBox();
    box.addChild( auto );

    RenderBoxNonAutoIterator it = new RenderBoxNonAutoIterator( box );
    assertTrue( it.hasNext() );
    assertSame( payload, it.next() );
  }

  public void testEmpty() {
    BlockRenderBox box = new BlockRenderBox();

    RenderBoxNonAutoIterator it = new RenderBoxNonAutoIterator( box );
    assertFalse( it.hasNext() );
  }
}
