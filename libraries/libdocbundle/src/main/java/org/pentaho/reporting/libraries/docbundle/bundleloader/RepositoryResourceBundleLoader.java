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

import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A internal resource-bundle loader that handles all requests for loading resources from the current repository.
 *
 * @author Thomas Morgner
 */
public class RepositoryResourceBundleLoader implements ResourceBundleLoader {
  private static final String INNER_SCHEMA = RepositoryResourceBundleLoader.class.getName();
  public static final String SCHEMA = INNER_SCHEMA + ":bundlekey";

  private Repository repository;
  private ResourceKey bundleKey;
  private ResourceKey mainKey;

  public RepositoryResourceBundleLoader( final Repository repository,
                                         final ResourceKey outsideContextKey ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }

    this.repository = repository;

    final HashMap<FactoryParameterKey, Object> map = new HashMap<FactoryParameterKey, Object>();
    map.put( new FactoryParameterKey( "repository" ), repository );
    map.put( new FactoryParameterKey( "repository-loader" ), this );

    this.bundleKey = new ResourceKey( outsideContextKey, SCHEMA, new Object(), map );
    final String bundleType = BundleUtilities.getBundleType( repository );
    final String bundleMapping = BundleUtilities.getBundleMapping( bundleType );
    if ( bundleMapping == null ) {
      throw new IllegalStateException( "Invalid configuration: No Bundle-Mapping for the bundle-type " + bundleType );
    }


    this.mainKey = new ResourceKey( bundleKey, INNER_SCHEMA, bundleMapping, null );
  }

  /**
   * Tries to load the bundle. If the key does not point to a usable resource-bundle, this method returns null. The
   * Exception is only thrown if the bundle is not readable because of IO-Errors.
   * <p/>
   * A resource-bundle loader should only load the bundle for the key itself, never for any of the derived subkeys. It
   * is the ResourceManager's responsibility to search the key's hierachy for the correct key.
   *
   * @param key the resource key pointing to the bundle.
   * @return the loaded bundle or null, if the resource was not understood.
   * @throws ResourceLoadingException if something goes wrong.
   */
  public ResourceBundleData loadBundle( final ResourceManager resourceManager,
                                        final ResourceKey key ) throws ResourceLoadingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    if ( bundleKey.equals( key ) == false ) {
      return null;
    }

    return new RepositoryResourceBundleData( bundleKey, repository, mainKey, false );
  }

  public ResourceKey getMainKey() {
    return mainKey;
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key the key that should be tested.
   * @return true, if the key is supported.
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( bundleKey.equals( key ) ) {
      return true;
    }
    if ( bundleKey.equals( key.getParent() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Derives a new resource key from the given key. If neither a path nor new factory-keys are given, the parent key is
   * returned.
   *
   * @param parent      the parent
   * @param path        the derived path (can be null).
   * @param factoryKeys the optional factory keys (can be null).
   * @return the derived key.
   * @throws ResourceKeyCreationException if the key cannot be derived for any reason.
   */
  public ResourceKey deriveKey( final ResourceKey parent,
                                final String path,
                                final Map<? extends ParameterKey, ? extends Object> factoryKeys )
    throws ResourceKeyCreationException {
    if ( parent == null ) {
      throw new NullPointerException();
    }

    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException( "Assertation: Unsupported parent key type" );
    }

    final String entry;
    final String identifier = (String) parent.getIdentifier();
    if ( path != null ) {
      if ( path.length() > 0 && path.charAt( 0 ) == '/' ) {
        entry = LoaderUtils.stripLeadingSlashes( path );
      } else {
        entry = LoaderUtils.mergePaths( identifier, path );
      }
    } else {
      entry = identifier;
    }

    final Map<ParameterKey, Object> map;
    if ( factoryKeys != null ) {
      map = new HashMap<ParameterKey, Object>();
      map.putAll( parent.getFactoryParameters() );
      map.putAll( factoryKeys );
    } else {
      map = parent.getFactoryParameters();
    }

    if ( "true".equals( LibDocBundleBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.docbundle.bundleloader.repository.StrictKeyCheck", "true" ) ) ) {
      try {
        final String[] name = RepositoryUtilities.split( entry, "/" );
        if ( RepositoryUtilities.isExistsEntity( repository, name ) == false ) {
          throw new ResourceKeyCreationException( "The derived entry does not exist in this bundle." );
        }
      } catch ( ContentIOException e ) {
        throw new ResourceKeyCreationException( "Error checking whether the derived entry exists in the bundle." );
      }
    }

    return new ResourceKey( parent.getParent(), parent.getSchema(), entry, map );
  }

  /**
   * Serializes the resource key to a String representation which can be recreated using the
   * <code>deserialize(ResourceKey)<code> method.
   *
   * @param bundleKey
   * @param key
   * @return a <code>String<code> which is a serialized version of the <code>ResourceKey</code>
   * @throws ResourceException indicates an error serializing the resource key
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
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a byte[] object" );
    }

    final String data = (String) key.getIdentifier();
    return ResourceKeyUtils.createStringResourceKey
      ( String.valueOf( key.getSchema() ), data, key.getFactoryParameters() );
  }

  /**
   * Creates a <code>ResourceKey</code> based off the <code>String</code> representation of the key. The
   * <code>String</code> should have been created using the <code>serialize</code> method.
   *
   * @param bundleKey
   * @param stringKey the <code>String</code> representation of the <code>ResourceKey</code>  @return a
   *                  <code>ResourceKey</code> which matches the <code>String</code> representation
   * @throws ResourceKeyCreationException indicates an error occurred in the creation or deserialization of the
   *                                      <code>ResourceKey</code>
   */
  public ResourceKey deserialize( final ResourceKey bundleKey,
                                  final String stringKey ) throws ResourceKeyCreationException {
    final ResourceKeyData resourceKeyData = ResourceKeyUtils.parse( stringKey );

    // Validate the data
    if ( INNER_SCHEMA.equals( resourceKeyData.getSchema() ) == false ) {
      throw new ResourceKeyCreationException( "Serialized version of key does not contain correct schema" );
    }

    return new ResourceKey( bundleKey, resourceKeyData.getSchema(),
      resourceKeyData.getIdentifier(), resourceKeyData.getFactoryParameters() );
  }

  public boolean isSupportedDeserializer( final String data ) throws ResourceKeyCreationException {
    final ResourceKeyData resourceKeyData = ResourceKeyUtils.parse( data );

    // Validate the data
    return ( INNER_SCHEMA.equals( resourceKeyData.getSchema() ) );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final RepositoryResourceBundleLoader that = (RepositoryResourceBundleLoader) o;

    if ( !repository.equals( that.repository ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return repository.hashCode();
  }
}
