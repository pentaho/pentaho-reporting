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
 * Copyright (c) 2008 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.resourceloader.loader.fileobject;

import junit.framework.TestCase;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Paths;


public class FileObjectResourceLoaderTest extends TestCase {
  private static final String STRING_SERIALIZATION_PREFIX = "resourcekey:"; //$NON-NLS-1$
  private static final String DESERIALIZE_PREFIX = STRING_SERIALIZATION_PREFIX + FileObjectResourceLoader.class.getName();

  public FileObjectResourceLoaderTest() {
  }

  public FileObjectResourceLoaderTest( final String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    LibLoaderBoot.getInstance().start();
  }

  /**
   * Tests the serialization of FileObject based resource keys
   */
  public void testSerializer() throws Exception {
    final FileObjectResourceLoader fileObjectResourceLoader = new FileObjectResourceLoader();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    ResourceKey key = null;
    Map<ParameterKey, Object> factoryParameters = new HashMap<ParameterKey, Object>();
    String serializedVersion = null;

    // Test with null parameter
    try {
      serializedVersion = fileObjectResourceLoader.serialize( null, key );
      fail( "Serialization with a null parameter should throw a NullPointerException" ); //$NON-NLS-1$
    } catch ( NullPointerException npe ) {
      // success
    }

    // Test with a resource key instead of a fileObject key
    try {
      key = manager.createKey( "res://org/pentaho/reporting/libraries/resourceloader/SVG.svg" ); //$NON-NLS-1$
      serializedVersion = fileObjectResourceLoader.serialize( key, key );
      fail( "The resource key should not be handled by the file object resource loader" ); //$NON-NLS-1$
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    // Create a key from a fileObject
    final FileObject fileObject =
            VFS.getManager().resolveFile(
                    Paths.get( "src/test/resources/org/pentaho/reporting/libraries/resourceloader/SVG.svg" ).toAbsolutePath().toString() );

    key = manager.createKey( fileObject );
    serializedVersion = fileObjectResourceLoader.serialize( key, key );
    assertNotNull( "The returned key should not be null", key ); //$NON-NLS-1$
    assertTrue( "Serialized version does not start with the correct header", serializedVersion //$NON-NLS-1$
            .startsWith( STRING_SERIALIZATION_PREFIX ) );
    assertTrue( "Serialized version does not contain the correct schema information", serializedVersion //$NON-NLS-1$
            .startsWith( STRING_SERIALIZATION_PREFIX + fileObjectResourceLoader.getClass().getName() + ';' ) );
    assertTrue( "Serialized version should contain the filename",
            serializedVersion.endsWith( fileObject.getName().getBaseName() ) ); //$NON-NLS-1$
  }

  /**
   * Tests the deserialization of FileObject based resource keys
   */
  public void testDeserializer() throws Exception {
    final FileObjectResourceLoader fileObjectResourceLoader = new FileObjectResourceLoader();

    // Create a key from a fileObject
    final FileObject fileObject = VFS.getManager().resolveFile(
            Paths.get( "src/test/resources/org/pentaho/reporting/libraries/resourceloader/SVG.svg" ).toAbsolutePath().toString() );

    // Test deserializing invalid strings
    try {
      fileObjectResourceLoader.deserialize( null, null );
      fail( "deserialize of a null string should throw an exception" );
    } catch ( IllegalArgumentException iae ) {
      // success
    }

    try {
      fileObjectResourceLoader.deserialize( null, "" );
      fail( "deserialize of an empty string should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      fileObjectResourceLoader.deserialize( null,
              STRING_SERIALIZATION_PREFIX + this.getClass().getName() + ';' + fileObject.getName().getURI() );
      fail( "deserialize with an invalid resource class name should throw an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    try {
      fileObjectResourceLoader.deserialize( null, DESERIALIZE_PREFIX + ":/tmp" );
      fail( "deserialize with an invalid file should thrown an exception" );
    } catch ( ResourceKeyCreationException rkce ) {
      // success
    }

    final ResourceKey key1 =
            fileObjectResourceLoader.deserialize( null, DESERIALIZE_PREFIX + ';' + fileObject.getName().getURI()
                    + ";\"\"\"f:this=that\"\":\"\"f:invalid\"\":\"\"f:null=\"\"\"" );
    assertNotNull( key1 );
    assertTrue( key1.getIdentifier() instanceof FileObject );
    assertEquals( FileObjectResourceLoader.class.getName(), key1.getSchema() );
    assertEquals( fileObject.getName().getURI(), ( (FileObject) key1.getIdentifier() ).getName().getURI() );
    assertEquals( 2, key1.getFactoryParameters().size() );
    assertTrue( !key1.getFactoryParameters().containsKey( new FactoryParameterKey( "invalid" ) ) );
    assertTrue( key1.getFactoryParameters().containsKey( new FactoryParameterKey( "null" ) ) );
    assertNull( key1.getFactoryParameters().get( new FactoryParameterKey( "null" ) ) );
    assertEquals( "that", key1.getFactoryParameters().get( new FactoryParameterKey( "this" ) ) );

  }

}

