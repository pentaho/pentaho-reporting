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

package org.pentaho.reporting.libraries.resourceloader.loader.resource;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassloaderResourceLoaderTest extends TestCase {
  private static final String STRING_SERIALIZATION_PREFIX = "resourcekey:"; //$NON-NLS-1$
  private static final String DESERIALIZE_PREFIX =
    STRING_SERIALIZATION_PREFIX + ClassloaderResourceLoader.class.getName() + ';';

  public ClassloaderResourceLoaderTest() {
    super();
  }

  public ClassloaderResourceLoaderTest( String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testSerialize() throws Exception {
    final ResourceLoader resourceLoader = new ClassloaderResourceLoader();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    // Test failure - null key
    try {
      resourceLoader.serialize( null, null );
      fail( "Serialization of a null key should throw an exception" );
    } catch ( NullPointerException npe ) {
      // success 
    }

    // Test failure - not a Classloader resource key
    try {
      final File tempFile = File.createTempFile( "junit-test", ".tmp" );
      final ResourceKey tempKey = manager.createKey( tempFile );
      resourceLoader.serialize( null, tempKey );
      fail( "The Classloader Resource Loader should fail when handling a non-classloader resource key" );
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    // Create key
    final String key1source = "res://org/pentaho/reporting/libraries/resourceloader/test1.properties";
    final ResourceKey key1 = manager.createKey( key1source );
    assertNotNull( key1 );

    // Serialize the key
    final String serKey1 = resourceLoader.serialize( null, key1 );
    assertNotNull( "The returned key should not be null", serKey1 ); //$NON-NLS-1$
    assertTrue( "Serialized verison does not start with the correct header",
      serKey1.startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information",
      serKey1.startsWith( DESERIALIZE_PREFIX ) );
    assertTrue( "Serialized version should contain the identifier intact",
      serKey1.endsWith( key1.getIdentifier().toString() ) );

    // Serialize a key created from a derived key
    final String key2source = "test2.properties";
    final ResourceKey key2 = manager.deriveKey( key1, key2source );
    assertNotNull( key2 );

    final String serKey2 = resourceLoader.serialize( null, key2 );
    assertNotNull( "The returned key should not be null", serKey2 ); //$NON-NLS-1$
    assertTrue( "Serialized verison does not start with the correct header",
      serKey2.startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information",
      serKey2.startsWith( DESERIALIZE_PREFIX ) );
    assertTrue( "Serialized version should contain the identifier intact",
      serKey2.endsWith( ";res://org/pentaho/reporting/libraries/resourceloader/test2.properties" ) );

    // Serialize a key with factory parameters
    final Map<ParameterKey, Object> factoryParams = new LinkedHashMap<ParameterKey, Object>();
    factoryParams.put( new FactoryParameterKey( "this" ), "that" );
    factoryParams.put( new FactoryParameterKey( "null" ), null );
    final ResourceKey key3 = manager.createKey( key1source, factoryParams );
    assertNotNull( key3 );

    final String serKey3 = resourceLoader.serialize( null, key3 );
    assertEquals( "resourcekey:org.pentaho.reporting.libraries.resourceloader" +
      ".loader.resource.ClassloaderResourceLoader;" +
      "res://org/pentaho/reporting/libraries/resourceloader/" +
      "test1.properties;\"\"\"f:this=that\"\":\"\"f:null=\"\"\"", serKey3 );

  }

  public void testDeserializer() throws Exception {
    final ResourceLoader resourceLoader = new ClassloaderResourceLoader();

    // Test failure - null input
    try {
      resourceLoader.deserialize( null, null );
      fail( "Deserialize should throw an exception on null parameter" );
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    // Test failure - invalid input
    try {
      resourceLoader.deserialize( null, "junk" );
      fail( "Deserialize should throw an exception on bad parameter" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    // Test failure - wrong schema
    try {
      resourceLoader
        .deserialize( null, "resourcekey:org.pentaho.reporting.libraries.resourceloader.loader.resource.Junk;stuff" );
      fail( "Deserialize should throw an exception on bad parameter" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    // Test successful case w/o factory parameters

  }
}
