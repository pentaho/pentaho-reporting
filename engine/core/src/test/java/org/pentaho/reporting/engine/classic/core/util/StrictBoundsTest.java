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

package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

public class StrictBoundsTest extends TestCase {
  public void testIntersection() {
    final StrictBounds testArea = new StrictBounds( 100, 100, 100, 100 );

    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 0, 0, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 150, 0, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 210, 0, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 0, 150, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 210, 150, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 0, 210, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 150, 210, 10, 10 ) ) );
    assertFalse( StrictBounds.intersects( testArea, new StrictBounds( 210, 210, 10, 10 ) ) );

    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 95, 95, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 95, 150, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 95, 195, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 150, 95, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 150, 150, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 150, 195, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 195, 95, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 195, 150, 10, 10 ) ) );
    assertTrue( StrictBounds.intersects( testArea, new StrictBounds( 195, 195, 10, 10 ) ) );

  }
}
