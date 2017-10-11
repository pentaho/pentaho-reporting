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

package org.pentaho.reporting.libraries.resourceloader.loader.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 05.04.2006, 14:40:59
 *
 * @author Thomas Morgner
 */
public class ClassloaderResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = ClassloaderResourceLoader.class.getName();
  private static final Log logger = LogFactory.getLog( ClassloaderResourceLoader.class );
  private static final String RES_PREFIX = "res://";

  public ClassloaderResourceLoader() {
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
    if ( value instanceof String ) {
      final String valueString = (String) value;
      if ( valueString.startsWith( RES_PREFIX ) ) {
        final String resourcePath = valueString.substring( 6 );
        if ( ObjectUtilities.getResource( resourcePath, ClassloaderResourceData.class ) != null ) {
          return new ResourceKey( SCHEMA_NAME, value, factoryKeys );
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

    final String resource;
    if ( path.startsWith( RES_PREFIX ) ) {
      resource = path;
    } else if ( path.length() > 0 && path.charAt( 0 ) == '/' ) {
      resource = "res:/" + path;
    } else {
      resource = LoaderUtils.mergePaths( (String) parent.getIdentifier(), path );
    }
    final Map map;
    if ( factoryKeys != null ) {
      map = new HashMap();
      map.putAll( parent.getFactoryParameters() );
      map.putAll( factoryKeys );
    } else {
      map = parent.getFactoryParameters();
    }
    return new ResourceKey( parent.getSchema(), resource, map );
  }

  public URL toURL( final ResourceKey key ) {
    return null;
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "Key format is not recognized." );
    }
    return new ClassloaderResourceData( key );
  }

  /**
   * A helper method to make it easier to create resource descriptions.
   *
   * @param c
   * @param resource
   * @return
   */
  public static String createResourceKey( final Class c, final String resource ) {
    if ( c == null ) {
      // the resource given should already be absolute ..
      return RES_PREFIX + resource;
    }
    final String className = c.getName();
    final int lastDot = className.lastIndexOf( '.' );
    if ( lastDot < 0 ) {
      return RES_PREFIX + resource;
    } else {
      final String packageName = className.substring( 0, lastDot );
      final String packagePath = packageName.replace( '.', '/' );
      return RES_PREFIX + packageName + '/' + packagePath;
    }
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
    if ( !( key.getIdentifier() instanceof String ) ) {
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a String object" );
    }

    // Log information
    logger.debug( "Serializing a Classloader Resource Key..." );
    if ( key.getParent() != null ) {
      throw new ResourceException
        ( "Cannot serialize this key, it contains a parent, but should not contain one at all." );
    }

    // Serialize the key
    final String result = ResourceKeyUtils.createStringResourceKey( key.getSchema().toString(),
      (String) key.getIdentifier(), key.getFactoryParameters() );
    logger.debug( "Serialized Classloader Resource Key: [" + result + "]" );
    return result;
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

    // Create and return a new key
    return createKey( keyData.getIdentifier(), keyData.getFactoryParameters() );
  }

  public boolean isSupportedDeserializer( final String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
