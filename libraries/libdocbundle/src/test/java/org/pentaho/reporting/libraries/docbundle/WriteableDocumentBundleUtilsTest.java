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
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for the WritableDocumentBundleUtils class
 */
public class WriteableDocumentBundleUtilsTest extends TestCase {
  // Initialize the resource manager
  private ResourceManager resourceManager;
  private File tempFile;
  private ResourceKey tempKey;

  public WriteableDocumentBundleUtilsTest() {
    super();
  }

  public WriteableDocumentBundleUtilsTest( String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    super.setUp();
    resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    tempFile = File.createTempFile( "junit", "tmp" );
    tempFile.deleteOnExit();
    tempKey = resourceManager.createKey( tempFile );
  }

  public void testRemoveResource() throws IOException, ResourceException {
    // Create a document bundle
    final WriteableDocumentBundle docBundle = new MemoryDocumentBundle();

    // Create a temp file for processing purposes
    final File tempFile = File.createTempFile( "junit", "tmp" );
    tempFile.deleteOnExit();
    final ResourceKey tempKey = resourceManager.createKey( tempFile );

    // Add the temp file to the bundle
    final ResourceKey result = WriteableDocumentBundleUtils
      .embedResource( docBundle, resourceManager, tempKey, "test/testfile-{0}.tmp", "application/data", null );
    assertNotNull( result );
    assertTrue( result.getIdentifierAsString().startsWith( "test/testfile-" ) );
    assertTrue( docBundle.isEntryExists( result.getIdentifierAsString() ) );

    // Remove the temp file with the same key
    assertTrue( WriteableDocumentBundleUtils.removeResource( docBundle, result ) );
    assertFalse( docBundle.isEntryExists( result.getIdentifierAsString() ) );

    // Remove the key again - no error but false return
    assertFalse( WriteableDocumentBundleUtils.removeResource( docBundle, result ) );
  }

  public void testRemoveResourceWithNewKey() throws IOException, ResourceException {
    // Create a document bundle
    final WriteableDocumentBundle docBundle = new MemoryDocumentBundle();

    // Create a temp file for processing purposes
    final File tempFile = File.createTempFile( "junit", "tmp" );
    tempFile.deleteOnExit();
    final ResourceKey tempKey = resourceManager.createKey( tempFile );

    // Add the temp file to the bundle
    final ResourceKey result = WriteableDocumentBundleUtils
      .embedResource( docBundle, resourceManager, tempKey, "test/testfile-{0}.tmp", "application/data", null );
    assertNotNull( result );
    assertTrue( result.getIdentifierAsString().startsWith( "test/testfile-" ) );
    assertTrue( docBundle.isEntryExists( result.getIdentifierAsString() ) );

    // Create a new key with the same name
    final ResourceKey newKey = docBundle.createResourceKey( result.getIdentifierAsString(), null );

    // Remove the temp file with the new key
    assertTrue( WriteableDocumentBundleUtils.removeResource( docBundle, newKey ) );
    assertFalse( docBundle.isEntryExists( result.getIdentifierAsString() ) );

    // Remove the key again - no error but false return
    assertFalse( WriteableDocumentBundleUtils.removeResource( docBundle, newKey ) );
  }

  public void testEmbedResource() throws IOException, ResourceException {
    // Create a document bundle
    final WriteableDocumentBundle docBundle = new MemoryDocumentBundle();

    // Create a temp file for processing purposes
    final File tempFile = File.createTempFile( "junit", "tmp" );
    tempFile.deleteOnExit();
    final ResourceKey tempKey = resourceManager.createKey( tempFile );

    // Add the temp file to the bundle
    final ResourceKey result = WriteableDocumentBundleUtils
      .embedResource( docBundle, resourceManager, tempKey, "test/testfile-{0}.tmp", "application/data", null );
    assertNotNull( result );
    assertTrue( result.getIdentifierAsString().startsWith( "test/testfile-" ) );
    assertTrue( docBundle.isEntryExists( result.getIdentifierAsString() ) );

    // Add the file again, and set some factory parameters
    final Map factoryParameters = new HashMap();
    factoryParameters.put( "this", "that" );
    final ResourceKey result2 = WriteableDocumentBundleUtils
      .embedResource( docBundle, resourceManager, tempKey, "test/testfile-{0}.tmp", "application/data",
        factoryParameters );
    assertNotNull( result2 );
    assertTrue( result2.getIdentifierAsString().startsWith( "test/testfile-" ) );
    assertTrue( docBundle.isEntryExists( result2.getIdentifierAsString() ) );
    assertNotNull( result2.getFactoryParameters() );
    assertEquals( 1, result2.getFactoryParameters().size() );
    assertTrue( result2.getFactoryParameters().containsKey( "this" ) );
  }
}
