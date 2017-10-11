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

package org.pentaho.reporting.libraries.resourceloader.loader.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = FileResourceLoader.class.getName();
  private static final Log logger = LogFactory.getLog( FileResourceLoader.class );

  public FileResourceLoader() {
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key
   * @return
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
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
    if ( value instanceof File ) {
      final File f = (File) value;
      if ( f.exists() && f.isFile() ) {
        return new ResourceKey( SCHEMA_NAME, f, factoryKeys );
      }
    } else if ( value instanceof String ) {
      final File f = new File( String.valueOf( value ) );
      if ( f.exists() && f.isFile() ) {
        return new ResourceKey( SCHEMA_NAME, f, factoryKeys );
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
      final File target;
      if ( path != null ) {
        final File parentResource = (File) parent.getIdentifier();
        final File parentFile = parentResource.getCanonicalFile().getParentFile();
        if ( parentFile == null ) {
          throw new FileNotFoundException( "Parent file does not exist" );
        }
        target = new File( parentFile, path );
        if ( target.exists() == false || target.isFile() == false ) {
          throw new ResourceKeyCreationException(
            "Malformed value: " + path + " (" + target + "): File does not exist." );
        }

      } else {
        target = (File) parent.getIdentifier();
      }

      final Map map;
      if ( factoryKeys != null ) {
        map = new HashMap();
        map.putAll( parent.getFactoryParameters() );
        map.putAll( factoryKeys );
      } else {
        map = parent.getFactoryParameters();
      }
      return new ResourceKey( parent.getSchema(), target, map );
    } catch ( IOException ioe ) {
      throw new ResourceKeyCreationException( "Failed to create key", ioe );
    }
  }

  public URL toURL( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }

    try {
      final File file = (File) key.getIdentifier();
      return file.toURI().toURL();
    } catch ( MalformedURLException e ) {
      return null;
    }
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "Key format is not recognized." );
    }
    return new FileResourceData( key );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    // Validate the parameter
    if ( key == null ) {
      throw new NullPointerException( "The ResourceKey can not be null" );
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }
    if ( !( key.getIdentifier() instanceof File ) ) {
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a File object" );
    }

    // Log information
    logger.debug( "Serializing a File Resource Key..." );
    if ( key.getParent() != null ) {
      throw new ResourceException
        ( "Unable to serialize a File-ResourceKey with a parent. This type is not expected to have a parent." );
    }

    // Create a string version of the identifier
    try {
      final File file = (File) key.getIdentifier();
      final String strIdentifier = file.getCanonicalPath();
      final String result = ResourceKeyUtils.createStringResourceKey
        ( key.getSchema().toString(), strIdentifier, key.getFactoryParameters() );
      logger.debug( "Serialized File Resource Key: [" + result + "]" );
      return result;
    } catch ( IOException ioe ) {
      throw new IllegalArgumentException( "Could not determine cononical path to file specified in ResourceKey: "
        + ioe.getMessage() );
    }
  }

  public ResourceKey deserialize( final ResourceKey bundleKey, final String stringKey )
    throws ResourceKeyCreationException {
    // Parse the data
    final ResourceKeyData keyData = ResourceKeyUtils.parse( stringKey );

    // Validate the data
    if ( SCHEMA_NAME.equals( keyData.getSchema() ) == false ) {
      throw new ResourceKeyCreationException( "Serialized version of key does not contain correct schema" );
    }

    // Create a new file based on the path provided
    final File file = new File( keyData.getIdentifier() );
    return new ResourceKey( SCHEMA_NAME, file, keyData.getFactoryParameters() );
  }

  public boolean isSupportedDeserializer( final String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
