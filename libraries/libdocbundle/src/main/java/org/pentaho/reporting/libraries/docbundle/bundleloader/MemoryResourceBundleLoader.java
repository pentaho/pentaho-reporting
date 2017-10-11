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

package org.pentaho.reporting.libraries.docbundle.bundleloader;

import org.pentaho.reporting.libraries.docbundle.LibDocBundleBoot;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Map;

public class MemoryResourceBundleLoader implements ResourceBundleLoader {
  public MemoryResourceBundleLoader() {
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
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException if something goes wrong.
   */
  public ResourceBundleData loadBundle( final ResourceManager resourceManager,
                                        final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      return null;
    }
    final RepositoryResourceBundleLoader o = (RepositoryResourceBundleLoader)
      key.getFactoryParameters().get( new FactoryParameterKey( "repository-loader" ) );
    return o.loadBundle( resourceManager, key );
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key the key that should be tested.
   * @return true, if the key is supported.
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    if ( RepositoryResourceBundleLoader.SCHEMA.equals( key.getSchema() ) == false ) {
      return false;
    }
    return true;
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
                                final Map factoryKeys ) throws ResourceKeyCreationException {
    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException();
    }
    final RepositoryResourceBundleLoader o = (RepositoryResourceBundleLoader)
      parent.getFactoryParameters().get( new FactoryParameterKey( "repository-loader" ) );

    if ( "true".equals( LibDocBundleBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.docbundle.bundleloader.memory.StrictKeyCheck", "true" ) ) ) {
      try {
        final Repository repository =
          (Repository) parent.getFactoryParameters().get( new FactoryParameterKey( "repository" ) );
        if ( RepositoryUtilities.isExistsEntity( repository, RepositoryUtilities.split( path, "/" ) ) == false ) {
          throw new ResourceKeyCreationException( "The key does not exist: " + path );
        }
      } catch ( ContentIOException e ) {
        throw new ResourceKeyCreationException( "Failed to check for existing key", e );
      }
    }

    return o.deriveKey( parent, path, factoryKeys );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    return null;
  }

  public ResourceKey deserialize( final ResourceKey bundleKey, final String stringKey )
    throws ResourceKeyCreationException {
    return null;
  }

  public boolean isSupportedDeserializer( final String data ) throws ResourceKeyCreationException {
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
