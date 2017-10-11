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

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.docbundle.bundleloader.RepositoryResourceBundleLoader;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.resourceloader.DefaultResourceManagerBackend;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceBundleLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceManagerBackend;
import org.pentaho.reporting.libraries.resourceloader.UnrecognizedLoaderException;

import java.net.URL;
import java.util.Map;

/**
 * A resource-manager that first tries to full-fill any requests using the current bundle before it calls back into the
 * original resource-manager.
 *
 * @author Thomas Morgner
 */
public class BundleResourceManagerBackend extends DefaultResourceManagerBackend {
  private ResourceManagerBackend backend;
  private RepositoryResourceBundleLoader loader;
  private ResourceKey outsideContextKey;

  public BundleResourceManagerBackend( final Repository repository,
                                       final ResourceManagerBackend backend,
                                       final ResourceKey outsideContextKey ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( backend == null ) {
      throw new NullPointerException();
    }

    this.backend = backend;
    this.outsideContextKey = outsideContextKey;
    this.loader = new RepositoryResourceBundleLoader( repository, outsideContextKey );
  }

  public ResourceKey getOutsideContextKey() {
    return outsideContextKey;
  }

  public ResourceKey getBundleMainKey() {
    return loader.getMainKey();
  }

  public ResourceKey createKey( final Object data, final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException {
    if ( data == null ) {
      throw new NullPointerException();
    }
    return backend.createKey( data, parameters );
  }

  public ResourceKey deriveKey( final ResourceKey parent,
                                final String path,
                                final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException {
    if ( parent == null ) {
      if ( path == null ) {
        throw new NullPointerException();
      }

      return createKey( path, parameters );
    }

    if ( loader.isSupportedKey( parent ) ) {
      try {
        return loader.deriveKey( parent, path, parameters );
      } catch ( ResourceKeyCreationException rkce ) {
        ResourceKey context = computeContextKey( parent );
        while ( context != null ) {
          try {
            return backend.deriveKey( context, path, parameters );
          } catch ( ResourceKeyCreationException rkce2 ) {
            rkce = rkce2;
            // ignore ... we are just guessing ...
          }
          context = computeContextKey( context );
        }
        throw rkce;
      }
    }
    return backend.deriveKey( parent, path, parameters );
  }

  private ResourceKey computeContextKey( final ResourceKey key ) {
    ResourceKey maybeParent = key.getParent();
    while ( maybeParent != null && loader.isSupportedKey( maybeParent ) ) {
      maybeParent = maybeParent.getParent();
    }
    return maybeParent;
  }

  public URL toURL( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    return backend.toURL( key );
  }

  public Resource create( final ResourceManager frontEnd,
                          final ResourceData data,
                          final ResourceKey context,
                          final Class[] target )
    throws ResourceLoadingException, ResourceCreationException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( data == null ) {
      throw new NullPointerException();
    }
    return backend.create( frontEnd, data, context, target );
  }

  public ResourceBundleData loadResourceBundle( final ResourceManager frontEnd, final ResourceKey key )
    throws ResourceLoadingException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( loader.isSupportedKey( key ) ) {
      return loader.loadBundle( frontEnd, key );
    }
    return backend.loadResourceBundle( frontEnd, key );
  }

  public void registerDefaultFactories() {
    backend.registerDefaultFactories();
  }

  public void registerDefaultLoaders() {
    backend.registerDefaultLoaders();
  }

  public void registerBundleLoader( final ResourceBundleLoader loader ) {
    backend.registerBundleLoader( loader );
  }

  public void registerLoader( final ResourceLoader loader ) {
    backend.registerLoader( loader );
  }

  public void registerFactory( final ResourceFactory factory ) {
    backend.registerFactory( factory );
  }

  public ResourceData loadRawData( final ResourceManager frontEnd, final ResourceKey key )
    throws ResourceLoadingException, UnrecognizedLoaderException {
    return backend.loadRawData( frontEnd, key );
  }

  public ResourceKey deserialize( final ResourceKey bundleKey,
                                  final String serializedKey ) throws ResourceKeyCreationException {
    if ( loader.isSupportedDeserializer( serializedKey ) ) {
      return loader.deserialize( bundleKey, serializedKey );
    }

    return backend.deserialize( bundleKey, serializedKey );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    if ( loader.isSupportedKey( key ) ) {
      return loader.serialize( bundleKey, key );
    }
    final String serializedVersion = backend.serialize( bundleKey, key );
    if ( serializedVersion != null ) {
      return serializedVersion;
    }
    return super.serialize( bundleKey, key );
  }
}
