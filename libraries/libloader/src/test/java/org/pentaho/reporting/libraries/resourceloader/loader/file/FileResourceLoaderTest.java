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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.loader.file;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for the FileResourceLoader class.
 *
 * @author David M. Kincade
 */
public class FileResourceLoaderTest extends TestCase {
  private static final String STRING_SERIALIZATION_PREFIX = "resourcekey:"; //$NON-NLS-1$
  private static final String DESERIALIZE_PREFIX = STRING_SERIALIZATION_PREFIX + FileResourceLoader.class.getName();
  private static final String TEMP_FILENAME = "tmp"; //$NON-NLS-1$

  private File tempFile = null;
  private File tempSubFile = null;
  private File tempSubDir = null;
  private String tempRelativePath = null;
  private String tempRelativeFilename = null;

  public FileResourceLoaderTest() {
  }

  public FileResourceLoaderTest( final String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
    setupTempFiles();
  }

  private void setupTempFiles() {
    try {
      final File tmpDir = new File( "bin/test-tmp" );
      tmpDir.mkdirs();

      // Create a temporary file
      tempFile = new File( tmpDir, TEMP_FILENAME + ".tmp" ); //$NON-NLS-1$
      tempFile.createNewFile();
      tempFile.deleteOnExit();

      // Create a temporary directory in the same directory as the temp file
      tempRelativePath = "." + File.separatorChar + TEMP_FILENAME;
      tempSubDir = new File( tempFile.getParent(), tempRelativePath );
      if ( !tempSubDir.exists() ) {
        tempSubDir.mkdir();
      }
      tempSubDir.deleteOnExit();

      // Create a temp file in the new subdirectory
      tempSubFile = new File( tempSubDir, TEMP_FILENAME + ".tmp" ); //$NON-NLS-1$
      tempSubFile.createNewFile();
      tempSubFile.deleteOnExit();
      tempRelativeFilename = tempRelativePath + File.separatorChar + tempSubFile.getName();
    } catch ( IOException ioe ) {
      throw new RuntimeException( "Could not create temp files", ioe ); //$NON-NLS-1$
    }
  }

  /**
   * Tests the serialization of File based resource keys
   */
  public void testSerialize() throws Exception {
    final FileResourceLoader fileResourceLoader = new FileResourceLoader();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    Map<ParameterKey, Object> factoryParameters = new HashMap<ParameterKey, Object>();
    String serializedVersion = null;

    // Test with null parameter
    try {
      serializedVersion = fileResourceLoader.serialize( null, key );
      fail( "Serialization with a null paramter should throw a NullPointerException" ); //$NON-NLS-1$
    } catch ( NullPointerException npe ) {
      // success
    }

    // Test with a resource key instead of a file key
    try {
      key = manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/test1.properties" ); //$NON-NLS-1$
      serializedVersion = fileResourceLoader.serialize( key, key );
      fail( "The resource key should not handles by the file resource loader" ); //$NON-NLS-1$
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    // Create a key from the temp file
    key = manager.createKey( tempFile );
    serializedVersion = fileResourceLoader.serialize( key, key );
    assertNotNull( "The returned key should not be null", key ); //$NON-NLS-1$
    assertTrue( "Serialized verison does not start with the correct header", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX + fileResourceLoader.getClass().getName() + ';' ) );
    assertTrue( "Serialized version should contain the filename",
      serializedVersion.endsWith( tempFile.getName() ) ); //$NON-NLS-1$

    // Create a key as a relative path from the above key
    key = manager.deriveKey( key, tempRelativeFilename );
    assertNotNull( key );
    serializedVersion = fileResourceLoader.serialize( key, key );
    assertNotNull( serializedVersion );
    assertTrue( "Serialized verison does not start with the correct header", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX + fileResourceLoader.getClass().getName() + ';' ) );
    assertTrue(
      "Serialized version should contain the filename",
      serializedVersion.endsWith( tempSubFile.getCanonicalPath() ) ); //$NON-NLS-1$

    // Create a key with factory parameters
    factoryParameters.put( new FactoryParameterKey( "this" ), "that" );
    factoryParameters.put( new FactoryParameterKey( "null" ), null );
    key = manager.createKey( tempFile, factoryParameters );
    serializedVersion = fileResourceLoader.serialize( key, key );

    assertNotNull( "The returned key should not be null", key ); //$NON-NLS-1$
    assertTrue( "Serialized verison does not start with the correct header", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX + fileResourceLoader.getClass().getName() + ';' ) );
    assertTrue(
      "Serialized version should contain the filename",
      serializedVersion.indexOf( ";" + tempFile.getCanonicalPath() + ";" ) > -1 ); //$NON-NLS-1$
    assertTrue( "Serialized version should contain factory parameters", serializedVersion.indexOf( "this=that" ) > -1 );
    assertTrue( "Serialized version should contain factory parameters", serializedVersion.indexOf( ':' ) > -1 );
  }

  /**
   * Tests the deserialization of File based resource keys
   */
  public void testDeserializer() throws Exception {
    final FileResourceLoader fileResourceLoader = new FileResourceLoader();

    // Test deserializing invalid strings
    try {
      fileResourceLoader.deserialize( null, null );
      fail( "deserialize of a null string should throw an exception" );
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    try {
      fileResourceLoader.deserialize( null, "" );
      fail( "deserialize of an empty string should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      fileResourceLoader.deserialize( null,
        STRING_SERIALIZATION_PREFIX + this.getClass().getName() + ';' + tempFile.getCanonicalPath() );
      fail( "deserialize with an invalid resource class name should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      fileResourceLoader.deserialize( null, DESERIALIZE_PREFIX + ":/tmp" );
      fail( "deserialize with an invalid file should thrown an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    final ResourceKey key1 =
      fileResourceLoader.deserialize( null, DESERIALIZE_PREFIX + ';' + tempFile.getCanonicalPath() +
        ";\"\"\"f:this=that\"\":\"\"f:invalid\"\":\"\"f:null=\"\"\"" );
    assertNotNull( key1 );
    assertTrue( key1.getIdentifier() instanceof File );
    assertEquals( FileResourceLoader.class.getName(), key1.getSchema() );
    assertEquals( tempFile.getCanonicalPath(), ( (File) key1.getIdentifier() ).getCanonicalPath() );
    assertEquals( 2, key1.getFactoryParameters().size() );
    assertTrue( !key1.getFactoryParameters().containsKey( new FactoryParameterKey( "invalid" ) ) );
    assertTrue( key1.getFactoryParameters().containsKey( new FactoryParameterKey( "null" ) ) );
    assertNull( key1.getFactoryParameters().get( new FactoryParameterKey( "null" ) ) );
    assertEquals( "that", key1.getFactoryParameters().get( new FactoryParameterKey( "this" ) ) );
  }

  /**
   * This is a happy path "round-trip" test which should demonstrate the serializing and deserializing a resource key
   * should produce the same key
   */
  public void testSerializeDeserializeRoundtrip() throws Exception {
    final FileResourceLoader fileResourceLoader = new FileResourceLoader();
    final Map<ParameterKey, Object> factoryParams = new HashMap<ParameterKey, Object>();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    factoryParams.put( new FactoryParameterKey( "this" ), "that" );
    factoryParams.put( new FactoryParameterKey( "null" ), null );
    final ResourceKey originalKey = manager.createKey( tempFile, factoryParams );

    final String serializedVersion = fileResourceLoader.serialize( null, originalKey );
    final ResourceKey duplicateKey = fileResourceLoader.deserialize( null, serializedVersion );
    assertNotNull( duplicateKey );
    assertTrue( originalKey.equals( duplicateKey ) );
  }
}
