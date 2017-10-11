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
