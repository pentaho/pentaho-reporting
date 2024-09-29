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


package org.pentaho.reporting.libraries.resourceloader;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class DrawableWrapperTest extends TestCase {
  public static class D1 {
    public void draw( final Graphics2D g2, final Rectangle2D bounds ) {

    }

    public Dimension getPreferredSize() {
      return new Dimension( 10, 10 );
    }

    public boolean isPreserveAspectRatio() {
      return true;
    }
  }

  public static class D2 {
    public void draw( final Graphics2D g2, final Rectangle2D bounds ) {

    }

    public Dimension2D getPreferredSize() {
      return new Dimension( 10, 10 );
    }

    public boolean isPreserveAspectRatio() {
      return true;
    }
  }

  public void testValidObject() {
    final D1 drawable = new D1();
    assertTrue( DrawableWrapper.isDrawable( drawable ) );
    final DrawableWrapper w = new DrawableWrapper( drawable );
    assertEquals( new Dimension( 10, 10 ), w.getPreferredSize() );
    assertEquals( true, w.isPreserveAspectRatio() );
  }

  public void testInvalidObject() {
    final D2 drawable = new D2();
    assertTrue( DrawableWrapper.isDrawable( drawable ) );
    final DrawableWrapper w = new DrawableWrapper( drawable );
    assertNull( w.getPreferredSize() );
    assertEquals( true, w.isPreserveAspectRatio() );
  }
}
