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
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.file.FileRepository;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DirectoryResourceBundleLoader implements ResourceBundleLoader {
  public DirectoryResourceBundleLoader() {
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
  public ResourceBundleData loadBundle( final ResourceManager resourceManager, final ResourceKey key )
    throws ResourceLoadingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    try {
      final Object identifier = key.getIdentifier();
      if ( identifier instanceof File == false ) {
        return null; // Not a valid key for creating a file-repository
      }
      final File directory = (File) identifier;
      if ( directory.isDirectory() == false ) {
        return null;
      }
      final FileRepository repository = new FileRepository( directory );
      final String bundleType = BundleUtilities.getBundleType( repository );
      final String bundleMapping = BundleUtilities.getBundleMapping( bundleType );

      final HashMap<FactoryParameterKey, Object> map = new HashMap<FactoryParameterKey, Object>();
      map.put( new FactoryParameterKey( "repository" ), repository );
      map.put( new FactoryParameterKey( "repository-loader" ), this );

      final ResourceKey mainKey = new ResourceKey
        ( key, DirectoryResourceBundleLoader.class.getName(), bundleMapping, map );

      return new RepositoryResourceBundleData( key, repository, mainKey, true );
    } catch ( ContentIOException e ) {
      throw new ResourceLoadingException( "Failed", e );
    }
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key the key that should be tested.
   * @return true, if the key is supported.
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    final ResourceKey parent = key.getParent();
    if ( parent == null ) {
      return false;
    }
    if ( DirectoryResourceLoader.class.getName().equals( parent.getSchema() ) == false ) {
      return false;
    }
    if ( DirectoryResourceBundleLoader.class.getName().equals( key.getSchema() ) == false ) {
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
   * @throws ResourceKeyCreationException if the key cannot be derived for any reason.
   */
  public ResourceKey deriveKey( final ResourceKey parent,
                                final String path,
                                final Map<? extends ParameterKey, ? extends Object> factoryKeys )
    throws ResourceKeyCreationException {
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
      ( "org.pentaho.reporting.libraries.docbundle.bundleloader.directory.StrictKeyCheck", "true" ) ) ) {
      try {
        final FileRepository repository = (FileRepository)
          parent.getFactoryParameters().get( new FactoryParameterKey( "repository" ) );
        if ( RepositoryUtilities.isExistsEntity( repository, RepositoryUtilities.split( entry, "/" ) ) == false ) {
          throw new ResourceKeyCreationException( "The key does not exist: " + entry );
        }
      } catch ( ContentIOException e ) {
        throw new ResourceKeyCreationException( "Failed to check for existing key", e );
      }
    }

    return new ResourceKey( parent.getParent(), parent.getSchema(), entry, map );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    return null;
  }

  public ResourceKey deserialize( final ResourceKey bundleKey,
                                  final String stringKey ) throws ResourceKeyCreationException {
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
