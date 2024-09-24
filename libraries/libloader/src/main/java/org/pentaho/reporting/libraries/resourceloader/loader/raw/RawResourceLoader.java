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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.loader.raw;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 12.04.2006, 15:19:03
 *
 * @author Thomas Morgner
 */
public class RawResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = RawResourceLoader.class.getName();

  public RawResourceLoader() {
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key
   * @return
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    if ( SCHEMA_NAME.equals( key.getSchema() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Creates a new resource key from the given object and the factory keys.
   *
   * @param value
   * @param factoryKeys
   * @return the created key.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException if creating the key failed.
   */
  public ResourceKey createKey( final Object value, final Map factoryKeys ) throws ResourceKeyCreationException {
    if ( value instanceof byte[] == false ) {
      return null;
    }

    return new ResourceKey( SCHEMA_NAME, value, factoryKeys );
  }

  /**
   * Derives a new resource key from the given key. If neither a path nor new factory-keys are given, the parent key is
   * returned.
   *
   * @param parent      the parent
   * @param path        the derived path (can be null).
   * @param factoryKeys the optional factory keys (can be null).
   * @return the derived key.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException if the key cannot be derived
   *                                                                                     for any reason.
   */
  public ResourceKey deriveKey( final ResourceKey parent, final String path, final Map factoryKeys )
    throws ResourceKeyCreationException {
    if ( path != null ) {
      throw new ResourceKeyCreationException( "Unable to derive key for new path." );
    }
    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException( "Assertation: Unsupported parent key type" );
    }

    if ( factoryKeys == null ) {
      return parent;
    }

    final HashMap map = new HashMap();
    map.putAll( parent.getFactoryParameters() );
    map.putAll( factoryKeys );
    return new ResourceKey( parent.getSchema(), parent.getIdentifier(), map );
  }

  public URL toURL( final ResourceKey key ) {
    // not supported ..
    return null;
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "The key type is not supported." );
    }
    return new RawResourceData( key );
  }

  /**
   * Creates a String version of the resource key that can be used to generate a new ResourceKey object via
   * deserialization
   *
   * @param bundleKey
   * @param key
   */
  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    // Validate the parameter
    if ( key == null ) {
      throw new NullPointerException( "The ResourceKey can not be null" );
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }
    if ( !( key.getIdentifier() instanceof byte[] ) ) {
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a byte[] object" );
    }

    final byte[] data = (byte[]) key.getIdentifier();
    final char[] cdata = new char[ data.length ];
    for ( int i = 0; i < data.length; i++ ) {
      cdata[ i ] = (char) ( data[ i ] & 0xFF );
    }
    return ResourceKeyUtils.createStringResourceKey
      ( String.valueOf( key.getSchema() ), new String( cdata ), key.getFactoryParameters() );
  }

  /**
   * Parses the input string and returns a newly created ResourceKey based on the string data
   */
  public ResourceKey deserialize( final ResourceKey bundleKey, String stringKey ) throws ResourceKeyCreationException {
    // Parse the data
    final ResourceKeyData keyData = ResourceKeyUtils.parse( stringKey );

    // Validate the data
    if ( SCHEMA_NAME.equals( keyData.getSchema() ) == false ) {
      throw new ResourceKeyCreationException( "Serialized version of key does not contain correct schema" );
    }

    final String identifier = keyData.getIdentifier();
    final char[] chars = identifier.toCharArray();
    final byte[] data = new byte[ chars.length ];
    for ( int i = 0; i < chars.length; i++ ) {
      data[ i ] = (byte) chars[ i ];
    }
    return createKey( data, keyData.getFactoryParameters() );
  }

  public boolean isSupportedDeserializer( String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
