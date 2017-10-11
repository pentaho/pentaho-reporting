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
