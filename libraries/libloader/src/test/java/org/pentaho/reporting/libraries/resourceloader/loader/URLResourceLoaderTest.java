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
* Copyright (c) 2002-2022 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.loader;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class URLResourceLoaderTest {
  private static final String STRING_SERIALIZATION_PREFIX = "resourcekey:"; //$NON-NLS-1$
  private static final String DESERIALIZE_PREFIX = STRING_SERIALIZATION_PREFIX + URLResourceLoader.class.getName();
  private static final String URL1 = "http://www.pentaho.com/index.html";
  private static final String URL2 = "http://www.pentaho.com/images/pentaho_logo.png";


  @Before
  public void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  /**
   * Tests the serialization of File based resource keys
   */
  @Test
  public void testSerialize() throws Exception {
    final URLResourceLoader resourceLoader = new URLResourceLoader();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    ResourceKey key = null;
    Map<ParameterKey, Object> factoryParameters = new LinkedHashMap<ParameterKey, Object>();
    String serializedVersion = null;

    // Test with null parameter
    try {
      serializedVersion = resourceLoader.serialize( null, key );
      fail( "Serialization with a null paramter should throw a NullPointerException" ); //$NON-NLS-1$
    } catch ( NullPointerException npe ) {
      // success
    }

    // Test with a file instead of a URL
    try {
      final File tempFile = File.createTempFile( "unittest", "test" );
      tempFile.deleteOnExit();
      key = manager.createKey( tempFile );
      serializedVersion = resourceLoader.serialize( key, key );
      fail( "The resource key should not handled by the URL resource loader" ); //$NON-NLS-1$
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    // Create a key from the temp file
    key = manager.createKey( new URL( URL1 ) );
    serializedVersion = resourceLoader.serialize( key, key );
    assertNotNull( "The returned key should not be null", key ); //$NON-NLS-1$
    assertTrue( "Serialized verison does not start with the correct header", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX + resourceLoader.getClass().getName() + ';' ) );
    assertTrue( "Serialized version should contain the filename", serializedVersion.endsWith( URL1 ) ); //$NON-NLS-1$

    // Create a key as a relative path from the above key
    key = manager.deriveKey( key, "images/pentaho_logo.png" );
    assertNotNull( key );
    serializedVersion = resourceLoader.serialize( key, key );
    assertNotNull( serializedVersion );
    assertTrue( "Serialized verison does not start with the correct header", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
      .startsWith( STRING_SERIALIZATION_PREFIX + resourceLoader.getClass().getName() + ';' ) );
    assertTrue( "Serialized version should contain the filename", serializedVersion.endsWith( URL2 ) ); //$NON-NLS-1$

    // Create a key with factory parameters
    factoryParameters.put( new FactoryParameterKey( "this" ), "that" );
    factoryParameters.put( new FactoryParameterKey( "null" ), null );
    key = manager.createKey( new URL( URL1 ), factoryParameters );
    serializedVersion = resourceLoader.serialize( key, key );

    assertEquals( "resourcekey:org.pentaho.reporting.libraries.resourceloader.loader.URLResourceLoader;" +
      "http://www.pentaho.com/index.html;\"\"\"f:this=that\"\":\"\"f:null=\"\"\"", serializedVersion );
  }

  /**
   * Tests the deserialization of File based resource keys
   */
  @Test
  public void testDeserializer() throws Exception {
    final URLResourceLoader resourceLoader = new URLResourceLoader();

    // Test deserializing invalid strings
    try {
      resourceLoader.deserialize( null, null );
      fail( "deserialize of a null string should throw an exception" );
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    try {
      resourceLoader.deserialize( null, "" );
      fail( "deserialize of an empty string should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      resourceLoader.deserialize( null, STRING_SERIALIZATION_PREFIX + this.getClass().getName() + ';' + URL1 );
      fail( "deserialize with an invalid resource class name should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      resourceLoader.deserialize( null, DESERIALIZE_PREFIX + ":/tmp" );
      fail( "deserialize with an invalid file should thrown an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    final ResourceKey key1 = resourceLoader.deserialize( null, DESERIALIZE_PREFIX + ';' + URL1 +
      ";\"\"\"f:this=that\"\":\"\"f:invalid\"\":\"\"f:null=\"\"\"" );
    assertNotNull( key1 );
    assertTrue( key1.getIdentifier() instanceof URL );
    assertEquals( URLResourceLoader.class.getName(), key1.getSchema() );
    assertEquals( new URL( URL1 ), key1.getIdentifier() );
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
  @Test
  public void testSerializeDeserializeRoundtrip() throws Exception {
    final URLResourceLoader resourceLoader = new URLResourceLoader();
    final Map<ParameterKey, Object> factoryParams = new HashMap<ParameterKey, Object>();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    factoryParams.put( new FactoryParameterKey( "this" ), "that" );
    factoryParams.put( new FactoryParameterKey( "null" ), null );
    final ResourceKey originalKey = manager.createKey( URL1, factoryParams );

    final String serializedVersion = resourceLoader.serialize( null, originalKey );
    final ResourceKey duplicateKey = resourceLoader.deserialize( null, serializedVersion );
    assertNotNull( duplicateKey );
    assertTrue( originalKey.equals( duplicateKey ) );
  }

  @Test
  public void testCreateKeyPassingURLAsVariable() throws ResourceKeyCreationException, MalformedURLException {
    // Test object instance
    URLResourceLoader resourceLoader = new URLResourceLoader();

    //Mock objects
    Map factoryKeys = mock( Map.class );
    URL value = new URL( "http://hitachivantara.com" );

    ResourceKey key = resourceLoader.createKey( value, factoryKeys );
    assertEquals( URLResourceLoader.SCHEMA_NAME, key.getSchema() );
    assertEquals( value, key.getIdentifier() );
  }

  @Test
  public void testCreateKeyPassingNonSupportedObject() throws ResourceKeyCreationException {
    // Test object instance
    URLResourceLoader resourceLoader = new URLResourceLoader();

    //Mock objects
    Map factoryKeys = mock( Map.class );
    Object value = new Object();

    ResourceKey key = resourceLoader.createKey( value, factoryKeys );
    assertEquals( null, key );
  }

  @Test
  public void testCreateKeyPassingStringAsVariable() throws ResourceKeyCreationException {
    // Test object instance
    URLResourceLoader resourceLoader = new URLResourceLoader();

    //Mock objects
    Map factoryKeys = mock( Map.class );
    String value = "http://hitachivantara.com?param=XPTO XPTO";

    ResourceKey key = resourceLoader.createKey( value, factoryKeys );
    assertEquals( URLResourceLoader.SCHEMA_NAME, key.getSchema() );
    assertTrue( key.getIdentifier() instanceof URL );
    assertEquals( "http://hitachivantara.com?param=XPTO%20XPTO", key.getIdentifier().toString() );
  }

  @Test( expected = ResourceKeyCreationException.class )
  public void testCreateKeyPassingInvalidURLStringAsVariable() throws ResourceKeyCreationException {
    // Test object instance
    URLResourceLoader resourceLoader = new URLResourceLoader();

    //Mock objects
    Map factoryKeys = mock( Map.class );
    String value = "invalid://url";

    resourceLoader.createKey( value, factoryKeys );
  }

  @Test
  public void testCreateKeyPassingInvalidString() throws ResourceKeyCreationException {
    // Test object instance
    URLResourceLoader resourceLoader = new URLResourceLoader();

    //Mock objects
    Map factoryKeys = mock( Map.class );
    String value = "";

    ResourceKey key = resourceLoader.createKey( value, factoryKeys );
    assertEquals( null, key );
  }
}
