/*
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
* Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import junit.framework.TestCase;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class ResourceKeyTest extends TestCase {
  public ResourceKeyTest() {
  }

  public ResourceKeyTest( final String string ) {
    super( string );
  }


  protected void setUp()
    throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  public void testResourceKeyCreation()
    throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    final ResourceKey key = manager.createKey
      ( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties" );
    assertNotNull( key );
    final ResourceKey key1 = manager.deriveKey( key, "test2.properties" );
    assertNotNull( key1 );
  }

  public void testURLKeyCreation()
    throws ResourceKeyCreationException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    final URL url = ResourceKeyTest.class.getResource
      ( "/org/pentaho/reporting/libraries/resourceloader/test1.properties" );
    assertNotNull( url );
    final ResourceKey key = manager.createKey( url );
    assertNotNull( key );
    final ResourceKey key1 = manager.deriveKey( key, "test2.properties" );
    assertNotNull( key1 );
  }

  public void testFileKeyCreation()
    throws ResourceKeyCreationException, IOException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    final File f1 = File.createTempFile( "junit-test", ".tmp" );
    f1.deleteOnExit();
    final File f2 = File.createTempFile( "junit-test", ".tmp" );
    f2.deleteOnExit();

    assertNotNull( f1 );
    final ResourceKey key = manager.createKey( f1 );
    assertNotNull( key );
    final ResourceKey key1 = manager.deriveKey( key, f2.getName() );
    assertNotNull( key1 );
  }

  public void testFileObjectKeyCreation()
          throws ResourceKeyCreationException, IOException {
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    final FileObject fileObject =
            VFS.getManager().resolveFile(
                    Paths.get( "src/test/resources/org/pentaho/reporting/libraries/resourceloader/SVG.svg" ).toAbsolutePath().toString() );
    assertNotNull( fileObject );
    final ResourceKey key = manager.createKey( fileObject );
    assertNotNull( key );
  }

  public void testMixedKeyDerivation()
    throws ResourceKeyCreationException, IOException {
    final File f1 = File.createTempFile( "junit-test", ".tmp" );
    f1.deleteOnExit();
    assertNotNull( f1 );

    final URL url = ResourceKeyTest.class.getResource
      ( "/org/pentaho/reporting/libraries/resourceloader/test1.properties" );
    assertNotNull( url );

    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final ResourceKey key = manager.createKey( f1 );
    assertNotNull( key );

    final ResourceKey key2 = manager.deriveKey( key, url.toString() );
    assertNotNull( key2 );

    final ResourceKey key3 = manager.createKey( url );
    assertNotNull( key3 );

    final ResourceKey key4 = manager.deriveKey( key3, f1.getAbsolutePath() );
    assertNotNull( key4 );
  }
}
