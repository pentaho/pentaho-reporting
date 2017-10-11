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
import java.net.URL;

public class SVGLoadingTest extends TestCase {
  public SVGLoadingTest() {
  }

  public SVGLoadingTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  public void testSVGLoading() throws ResourceException {
    final URL resource = SVGLoadingTest.class.getResource( "SVG.svg" );
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource directly = manager.createDirectly( resource, DrawableWrapper.class );
    assertNotNull( directly.getResource() );
  }

  public void testSVGLoading2() throws ResourceException {
    final URL resource = SVGLoadingTest.class.getResource( "SVG.svg" );
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource directly = manager.createDirectly( resource, Image.class );
    assertNotNull( directly.getResource() );
  }
}
