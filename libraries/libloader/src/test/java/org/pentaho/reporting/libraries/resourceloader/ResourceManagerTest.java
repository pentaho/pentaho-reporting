/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
