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
* Copyright (c) 2006 - 2022 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 05.04.2006, 15:32:36
 *
 * @author Thomas Morgner
 */
public class URLResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = URLResourceLoader.class.getName();
  private static final Log logger = LogFactory.getLog( URLResourceLoader.class );

  public URLResourceLoader() {
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
    if ( value instanceof URL ) {
      return new ResourceKey( SCHEMA_NAME, value, factoryKeys );
    }
    if ( value instanceof String ) {
      String valueString = (String) value;
      // the 'file' schema is defined to have double-slashes, but the JDK
      // ignores RFC 1738 in that case. So we have to check for these beasts
      // directly ..
      if ( valueString.indexOf( "://" ) >= 0 || valueString.startsWith( "file:/" ) ) {
        try {
          // query parameters might contain ' ' (space) that must be escaped
          // other 'special' characters shouldn't need escaping, as they are
          // relaxedQueryChars in Tomcat config
          return new ResourceKey( SCHEMA_NAME, new URL( valueString.replace( " ", "%20" ) ), factoryKeys );

        } catch ( MalformedURLException mfue ) {
          // we dont take this easy!
          throw new ResourceKeyCreationException( "Malformed value: " + value );
        }
      }
    }

    return null;
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
    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException( "Assertation: Unsupported parent key type" );
    }

    try {
      final URL url;
      if ( path != null ) {
        url = new URL( (URL) parent.getIdentifier(), path );
      } else {
        url = (URL) parent.getIdentifier();
      }

      final Map map;
      if ( factoryKeys != null ) {
        map = new HashMap();
        map.putAll( parent.getFactoryParameters() );
        map.putAll( factoryKeys );
      } else {
        map = parent.getFactoryParameters();
      }
      return new ResourceKey( parent.getSchema(), url, map );
    } catch ( MalformedURLException e ) {
      throw new ResourceKeyCreationException( "Malformed value: " + path );
    }
  }

  public URL toURL( final ResourceKey key ) {
    return (URL) key.getIdentifier();
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "Key format is not recognized." );
    }
    return new URLResourceData( key );
  }

  /**
   * Creates a String version of the resource key that can be used to generate a new ResourceKey object via
   * deserialization
   *
   * @param bundleKey
   * @param key
   */
  public String serialize( ResourceKey bundleKey, final ResourceKey key ) {
    // Validate the parameter
    if ( key == null ) {
      throw new NullPointerException( "The ResourceKey can not be null" );
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }
    if ( !( key.getIdentifier() instanceof URL ) ) {
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a URL object" );
    }

    // Log information
    logger.debug( "Serializing a Classloader Resource Key..." );
    if ( key.getParent() != null ) {
      logger.warn( "Serializing a Classloader Resource Key which contains a parent: key=[" + bundleKey + "] parent=["
        + key.getParent() + "]" );
    }

    // Serialize the key
    final URL url = (URL) key.getIdentifier();
    final String result = ResourceKeyUtils.createStringResourceKey
      ( key.getSchema().toString(), url.toExternalForm(), key.getFactoryParameters() );
    logger.debug( "Serialized Classloader Resource Key: [" + result + "]" );
    return result;
  }

  /**
   * Parses the input string and returns a newly created ResourceKey based on the string data
   */
  public ResourceKey deserialize( final ResourceKey bundleKey, String stringKey ) throws ResourceKeyCreationException {
    // Parse the data
    ResourceKeyData keyData = ResourceKeyUtils.parse( stringKey );

    // Validate the data
    if ( SCHEMA_NAME.equals( keyData.getSchema() ) == false ) {
      throw new ResourceKeyCreationException( "Serialized version of key does not contain correct schema" );
    }

    // Create and return a new key
    try {
      return createKey( new URL( keyData.getIdentifier() ), keyData.getFactoryParameters() );
    } catch ( MalformedURLException mfue ) {
      throw new ResourceKeyCreationException( "Malformed value: " + keyData.getIdentifier() );
    }
  }

  public boolean isSupportedDeserializer( String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
