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

package org.pentaho.reporting.libraries.docbundle;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class MemDocBundleUpdateTest extends TestCase {
  public MemDocBundleUpdateTest() {
    super();
  }

  public MemDocBundleUpdateTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    LibDocBundleBoot.getInstance().start();
  }

  public void testBug() throws IOException, ResourceException, InterruptedException {
    final Properties p1 = new Properties();
    p1.setProperty( "key", "value1" );

    final MemoryDocumentBundle bundle = new MemoryDocumentBundle();
    bundle.getWriteableDocumentMetaData().setBundleType( "text/plain" );
    final OutputStream outputStream = bundle.createEntry( "test.properties", "text/plain" );
    p1.store( outputStream, "run 1" );
    outputStream.close();

    final ResourceManager resourceManager = bundle.getResourceManager();
    final ResourceKey key = resourceManager.deriveKey( bundle.getBundleMainKey(), "test.properties" );
    final Resource res1 = resourceManager.create( key, null, Properties.class );
    assertEquals( p1, res1.getResource() );

    bundle.removeEntry( "test.properties" );

    final Properties p2 = new Properties();
    p2.setProperty( "key", "value2" );

    final OutputStream outputStream2 = bundle.createEntry( "test.properties", "text/plain" );
    p2.store( outputStream2, "run 2" );
    outputStream2.close();

    final Resource res2 = resourceManager.create( key, null, Properties.class );
    assertEquals( p2, res2.getResource() );
  }


  public void testPrd4680() throws IOException, ResourceException, InterruptedException {
    final Properties p1 = new Properties();
    p1.setProperty( "key", "value1" );

    final MemoryDocumentBundle bundle = new MemoryDocumentBundle();
    bundle.getWriteableDocumentMetaData().setBundleType( "text/plain" );
    final OutputStream outputStream = bundle.createEntry( "test.properties", "text/plain" );
    p1.store( outputStream, "run 1" );
    outputStream.close();

    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG ) );
    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG ) );

    bundle.getWriteableDocumentMetaData().setEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG, "true" );
    assertEquals( "true", bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG ) );
    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG ) );

    bundle.getWriteableDocumentMetaData().setEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG, "false" );
    assertEquals( "true", bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG ) );
    assertEquals( "false", bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG ) );

    bundle.getWriteableDocumentMetaData().setEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG, null );
    assertEquals( "true", bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG ) );
    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG ) );

    bundle.getWriteableDocumentMetaData().setEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG, null );
    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.HIDDEN_FLAG ) );
    assertNull( bundle.getMetaData().getEntryAttribute( "test.properties", BundleUtilities.STICKY_FLAG ) );
  }
}
