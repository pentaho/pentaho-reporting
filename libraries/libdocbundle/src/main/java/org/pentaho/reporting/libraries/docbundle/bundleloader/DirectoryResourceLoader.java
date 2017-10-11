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

package org.pentaho.reporting.libraries.docbundle.bundleloader;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A resource-loader that marks directories as a valid resource. This is a backend-loader for resource-bundles that are
 * loaded from a directory and cannot be used as standalone resource-loader.
 *
 * @author Thomas Morgner
 */
public class DirectoryResourceLoader implements ResourceLoader {
  /**
   * Default-Constructor.
   */
  public DirectoryResourceLoader() {
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

    if ( DirectoryResourceLoader.class.getName().equals( key.getSchema() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Creates a new resource key from the given object and the factory keys.
   *
   * @param value
   * @param factoryKeys
   * @return the created key or null, if the value object was not supported.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException if creating the key failed.
   */
  public ResourceKey createKey( final Object value, final Map factoryKeys )
    throws ResourceKeyCreationException {
    if ( value == null ) {
      throw new NullPointerException();
    }

    if ( value instanceof File ) {
      final File f = (File) value;
      if ( f.exists() && f.isDirectory() ) {
        return new ResourceKey( DirectoryResourceLoader.class.getName(), f, factoryKeys );
      }
    } else if ( value instanceof String ) {
      final File f = new File( String.valueOf( value ) );
      if ( f.exists() && f.isDirectory() ) {
        return new ResourceKey( DirectoryResourceLoader.class.getName(), f, factoryKeys );
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
  public ResourceKey deriveKey( final ResourceKey parent,
                                final String path,
                                final Map factoryKeys )
    throws ResourceKeyCreationException {
    if ( parent == null ) {
      throw new NullPointerException();
    }

    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException( "Assertation: Unsupported parent key type" );
    }

    try {
      final File target;
      if ( path != null ) {
        final File parentResource = (File) parent.getIdentifier();
        target = new File( parentResource.getCanonicalFile().getParentFile(), path );
        if ( target.exists() == false || target.isDirectory() == false ) {
          throw new ResourceKeyCreationException( "Malformed value: " + path + " (" + target + ')' );
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

    final File file = (File) key.getIdentifier();
    try {
      return file.toURI().toURL();
    } catch ( MalformedURLException e ) {
      return null;
    }
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    throw new ResourceLoadingException( "This resource Loader cannot be used to create a ResourceData object." );
  }

  public ResourceKey deserialize( final ResourceKey bundleKey, final String stringKey )
    throws ResourceKeyCreationException {
    throw new ResourceKeyCreationException( "This resource Loader cannot be used to deserialize ReousrceKeys" );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    throw new ResourceException( "This resource Loader cannot be used to serialize ResourceKeys" );
  }

  public boolean isSupportedDeserializer( final String data ) {
    return false;
  }

  public int hashCode() {
    return getClass().hashCode();
  }

  public boolean equals( final Object obj ) {
    if ( obj == this ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }

    return obj.getClass() == getClass();
  }

}
