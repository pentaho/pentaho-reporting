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
