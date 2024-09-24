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
