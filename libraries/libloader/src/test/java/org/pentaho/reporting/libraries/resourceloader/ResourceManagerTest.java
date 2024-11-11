/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.resourceloader.loader.resource.ClassloaderResourceData;
import org.pentaho.reporting.libraries.resourceloader.loader.resource.ClassloaderResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.modules.factory.svg.SVGDrawable;

public class ResourceManagerTest extends TestCase {
  public ResourceManagerTest() {
  }

  protected void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  public void testCreateKey() throws ResourceKeyCreationException {
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    ResourceKey k = mgr.createKey( "res://org/pentaho/reporting/libraries/resourceloader/SVG.svg" );
    Assert.assertNotNull( k );
    Assert.assertEquals( ClassloaderResourceLoader.class.getName(), k.getSchema() );
    Assert.assertEquals( "res://org/pentaho/reporting/libraries/resourceloader/SVG.svg", k.getIdentifierAsString() );
  }

  public void testLoadData() throws ResourceException {
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    ResourceKey k = mgr.createKey( "res://org/pentaho/reporting/libraries/resourceloader/SVG.svg" );
    ResourceData resourceData = mgr.load( k );
    Assert.assertNotNull( resourceData );
    Assert.assertEquals( k, resourceData.getKey() );
    Assert.assertEquals( ClassloaderResourceData.class, resourceData.getClass() );
    Assert.assertEquals( -1, resourceData.getLength() ); // for this case we cannot know the size until we load the data
    Assert.assertTrue(
      resourceData.getResource( mgr ).length > 33000 ); // Git mangles the line-endings and the file length changes
  }

  public void testLoadImage() throws ResourceException {
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    ResourceKey k = mgr.createKey( "res://org/pentaho/reporting/libraries/resourceloader/SVG.svg" );
    Resource resource = mgr.create( k, null, DrawableWrapper.class );
    Assert.assertNotNull( resource );
    Assert.assertEquals( k, resource.getSource() );
    // for SVGs we do a bit of cheating, as drawables have no common interface.
    // we use the DrawableWrapper as a signaling interface instead.
    Assert.assertEquals( SVGDrawable.class, resource.getResource().getClass() );
  }
}
