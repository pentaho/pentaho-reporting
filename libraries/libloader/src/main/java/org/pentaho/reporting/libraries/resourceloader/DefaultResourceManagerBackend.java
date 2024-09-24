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

package org.pentaho.reporting.libraries.resourceloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class DefaultResourceManagerBackend implements ResourceManagerBackend {
  private static final Log logger = LogFactory.getLog( DefaultResourceManagerBackend.class );

  private ArrayList<ResourceLoader> resourceLoaders;
  private ArrayList<ResourceBundleLoader> resourceBundleLoaders;
  private ArrayList<ResourceFactory> resourceFactories;
  private boolean registeredFactories;
  private boolean registeredLoaders;

  public DefaultResourceManagerBackend() {
    resourceLoaders = new ArrayList<ResourceLoader>();
    resourceBundleLoaders = new ArrayList<ResourceBundleLoader>();
    resourceFactories = new ArrayList<ResourceFactory>();
  }

  public synchronized ResourceKey createKey( final Object data,
                                             final Map<? extends ParameterKey, ?> parameters )
    throws ResourceKeyCreationException {
    if ( data == null ) {
      throw new NullPointerException( "Key data must not be null." );
    }

    final Iterator values = resourceLoaders.iterator();
    while ( values.hasNext() ) {
      final ResourceLoader loader = (ResourceLoader) values.next();
      try {
        final ResourceKey key = loader.createKey( data, parameters );
        if ( key != null ) {
          return key;
        }
      } catch ( ResourceKeyCreationException rkce ) {
        // ignore it.
      }
    }

    throw new ResourceKeyCreationException( "Unable to create key: No loader was able to handle the given key data: "
      + data );
  }

  /**
   * Derives a new key from the given resource-key. Only keys for a hierarchical storage system (like file-systems or
   * URLs) can have derived keys. Since LibLoader 0.3.0 only hierarchical keys can be derived. For that, the deriving
   * path must be given as String.
   * <p/>
   * The optional parameter-map will be applied to the derived key after the parent's parameters have been copied to the
   * new key.
   * <p/>
   * Before trying to derive the key, the system tries to interpret the path as absolute key-value.
   *
   * @param parent     the parent key, or null to interpret the path as absolute key.
   * @param path       the relative path, that is used to derive the key.
   * @param parameters a optional map containing resource-key parameters.
   * @return the derived key.
   * @throws ResourceKeyCreationException if deriving the key failed.
   */
  public synchronized ResourceKey deriveKey( final ResourceKey parent,
                                             final String path,
                                             final Map<? extends ParameterKey, ?> parameters )
    throws ResourceKeyCreationException {
    if ( parent == null ) {
      if ( path == null ) {
        throw new NullPointerException();
      }

      return createKey( path, parameters );
    }

    ResourceKeyCreationException rce = null;
    for ( int i = 0; i < resourceBundleLoaders.size(); i++ ) {
      final ResourceBundleLoader bundleLoader = resourceBundleLoaders.get( i );
      if ( bundleLoader.isSupportedKey( parent ) == false ) {
        continue;
      }

      try {
        final ResourceKey key = bundleLoader.deriveKey( parent, path, parameters );
        if ( key != null ) {
          return key;
        }
      } catch ( ResourceKeyCreationException rcke ) {
        rce = rcke;
      }
    }

    // First, try to derive the resource directly. This makes sure, that we preserve the parent's context.
    // If a file is derived, we assume that the result will be a file; and only if that fails we'll try to
    // query the other contexts. If the parent is an URL-context, the result is assumed to be an URL as well.
    for ( int i = 0; i < resourceLoaders.size(); i++ ) {
      final ResourceLoader loader = resourceLoaders.get( i );
      if ( loader.isSupportedKey( parent ) == false ) {
        continue;
      }
      try {
        final ResourceKey key = loader.deriveKey( parent, path, parameters );
        if ( key != null ) {
          return key;
        }
      } catch ( ResourceKeyCreationException rcke ) {
        rce = rcke;
      }
    }

    if ( path != null ) {
      // Second, try to load the key as absolute value.
      // This assumes, that we have no catch-all implementation.
      for ( int i = 0; i < resourceLoaders.size(); i++ ) {
        final ResourceLoader loader = resourceLoaders.get( i );
        final ResourceKey key = loader.createKey( path, parameters );
        if ( key != null ) {
          return key;
        }
      }
    }

    final ResourceKey secondParent = parent.getParent();
    if ( secondParent != null ) {
      // Desperate measures: Maybe the key is relative to the bundle. 
      for ( int i = 0; i < resourceLoaders.size(); i++ ) {
        final ResourceLoader loader = resourceLoaders.get( i );
        if ( loader.isSupportedKey( secondParent ) == false ) {
          continue;
        }
        try {
          final ResourceKey key = loader.deriveKey( secondParent, path, parameters );
          if ( key != null ) {
            return key;
          }
        } catch ( ResourceKeyCreationException rcke ) {
          rce = rcke;
        }
      }
    }

    if ( rce != null ) {
      throw rce;
    }
    throw new ResourceKeyCreationException( "Unable to create key: No such schema or the key was not recognized." );
  }

  /**
   * Tries to find the first resource-loader that would be able to process the key.
   *
   * @param key the resource-key.
   * @return the resourceloader for that key, or null, if no resource-loader is able to process the key.
   */
  private ResourceLoader findBySchema( final ResourceKey key ) {
    for ( int i = 0; i < resourceLoaders.size(); i++ ) {
      final ResourceLoader loader = resourceLoaders.get( i );
      if ( loader.isSupportedKey( key ) ) {
        return loader;
      }
    }
    return null;
  }

  public synchronized URL toURL( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    final ResourceLoader loader = findBySchema( key );
    if ( loader == null ) {
      return null;
    }
    return loader.toURL( key );
  }

  public synchronized Resource create( final ResourceManager frontEnd, final ResourceData data,
                                       final ResourceKey context,
                                       final Class[] target )
    throws ResourceLoadingException, ResourceCreationException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( data == null ) {
      throw new NullPointerException( "Data must not be null." );
    }

    // AutoMode ..
    if ( target == null ) {
      return autoCreateResource( frontEnd, data, context );
    }

    ResourceCreationException exception = null;
    final int factoryCount = resourceFactories.size();
    final ResourceFactory[] factories = resourceFactories.toArray( new ResourceFactory[ factoryCount ] );
    for ( int targetIdx = 0; targetIdx < target.length; targetIdx++ ) {
      final Class targetClass = target[ targetIdx ];
      for ( int i = 0; i < factoryCount; i++ ) {
        final ResourceFactory fact = factories[ i ];
        if ( isSupportedTarget( targetClass, fact ) == false ) {
          // Unsupported keys: Try the next factory ..
          continue;
        }

        try {
          return fact.create( frontEnd, data, context );
        } catch ( ContentNotRecognizedException ce ) {
          // Ignore it, unless it is the last one.
        } catch ( ResourceCreationException rex ) {
          // ignore it, try the next factory ...
          exception = rex;
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Failed at " + fact.getClass() + ": ", rex );
          }
        }
      }
    }

    if ( exception != null ) {
      throw exception;
    }
    throw new ContentNotRecognizedException( "None of the selected factories was able to handle the given data: "
      + data.getKey() );
  }

  private boolean isSupportedTarget( final Class target, final ResourceFactory fact ) {
    final Class<?> factoryType = fact.getFactoryType();
    // strict tests. We do no longer allow sub-class matching, as this yields
    if ( target != null && factoryType != null && factoryType.equals( target ) ) {
      return true;
    }
    return false;
  }

  private Resource autoCreateResource( final ResourceManager frontEnd,
                                       final ResourceData data,
                                       final ResourceKey context )
    throws ResourceLoadingException, ResourceCreationException {
    final Iterator it = resourceFactories.iterator();
    while ( it.hasNext() ) {
      final ResourceFactory fact = (ResourceFactory) it.next();
      try {
        final Resource res = fact.create( frontEnd, data, context );
        if ( res != null ) {
          return res;
        }
      } catch ( ResourceCreationException rex ) {
        // ignore it, try the next factory ...
      }
    }
    throw new ResourceCreationException( "No known factory was able to handle the given data." );
  }

  public boolean isResourceUnchanged( final ResourceManager frontEnd, final Resource resource )
    throws ResourceLoadingException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( resource == null ) {
      throw new NullPointerException();
    }

    final ResourceKey[] deps = resource.getDependencies();
    for ( int i = 0; i < deps.length; i++ ) {
      final ResourceKey dep = deps[ i ];
      final long version = resource.getVersion( dep );
      if ( version == -1 ) {
        // non-versioning key, ignore it.
        continue;
      }

      final ResourceData data = frontEnd.load( dep );
      if ( data.getVersion( frontEnd ) != version ) {
        // oh, my bad, an outdated or changed entry.
        // We have to re-read the whole thing.
        return false;
      }
    }
    // all versions have been confirmed to be valid. Nice, we can use the
    // cached product.
    return true;
  }

  /**
   * Tries to find the first resource-bundle-loader that would be able to process the key.
   *
   * @param key the resource-key.
   * @return the resourceloader for that key, or null, if no resource-loader is able to process the key.
   * @throws ResourceLoadingException if an error occured.
   */
  public synchronized ResourceBundleData loadResourceBundle( final ResourceManager frontEnd, final ResourceKey key )
    throws ResourceLoadingException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( key == null ) {
      throw new NullPointerException();
    }

    for ( int i = 0; i < resourceBundleLoaders.size(); i++ ) {
      final ResourceBundleLoader loader = resourceBundleLoaders.get( i );
      final ResourceBundleData resourceBundle = loader.loadBundle( frontEnd, key );
      if ( resourceBundle != null ) {
        return resourceBundle;
      }
    }
    return null;
  }

  public ResourceData loadRawData( final ResourceManager frontEnd, final ResourceKey key )
    throws UnrecognizedLoaderException, ResourceLoadingException {
    if ( frontEnd == null ) {
      throw new NullPointerException();
    }
    if ( key == null ) {
      throw new NullPointerException();
    }

    final ResourceLoader loader = findBySchema( key );
    if ( loader == null ) {
      throw new UnrecognizedLoaderException(
        "Invalid key: No resource-loader registered for schema: " + key.getSchema() );
    }
    logger.debug( "Loaded " + key );
    return loader.load( key );
  }

  public void registerDefaultFactories() {
    if ( registeredFactories == true ) {
      return;
    }

    registeredFactories = true;
    final Configuration config = LibLoaderBoot.getInstance().getGlobalConfig();
    final Iterator itType = config.findPropertyKeys( ResourceManager.FACTORY_TYPE_PREFIX );
    while ( itType.hasNext() ) {
      final String key = (String) itType.next();
      final String factoryClass = config.getConfigProperty( key );

      final ResourceFactory factory =
        ObjectUtilities.loadAndInstantiate( factoryClass, ResourceManager.class, ResourceFactory.class );
      if ( factory == null ) {
        continue;
      }

      factory.initializeDefaults();
      registerFactory( factory );
    }
  }

  public void registerDefaultLoaders() {
    if ( registeredLoaders == true ) {
      return;
    }

    registeredLoaders = true;

    final Configuration config = LibLoaderBoot.getInstance().getGlobalConfig();
    final Iterator<String> it = config.findPropertyKeys( ResourceManager.LOADER_PREFIX );
    while ( it.hasNext() ) {
      final String key = it.next();
      final String value = config.getConfigProperty( key );
      final ResourceLoader loader = ObjectUtilities.loadAndInstantiate( value, ResourceManager.class,
        ResourceLoader.class );
      if ( loader != null ) {
        //Log.debug("Registering loader for " + loader.getSchema());
        registerLoader( loader );
      }
    }

    final Iterator bit = config.findPropertyKeys( ResourceManager.BUNDLE_LOADER_PREFIX );
    while ( bit.hasNext() ) {
      final String key = (String) bit.next();
      final String value = config.getConfigProperty( key );
      final ResourceBundleLoader loader = ObjectUtilities.loadAndInstantiate( value,
        ResourceManager.class, ResourceBundleLoader.class );
      if ( loader != null ) {
        //Log.debug("Registering loader for " + loader.getSchema());
        registerBundleLoader( loader );
      }
    }
  }

  public void registerBundleLoader( final ResourceBundleLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException( "ResourceLoader must not be null." );
    }
    resourceBundleLoaders.add( loader );
  }

  public void registerLoader( final ResourceLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException( "ResourceLoader must not be null." );
    }
    resourceLoaders.add( loader );
  }

  public void registerFactory( final ResourceFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException( "ResourceFactory must not be null." );
    }
    resourceFactories.add( factory );
  }

  /**
   * Converts a serialized version of a <code>ResourceKey</code> into an actual <code>ResourceKey</code> by locating the
   * proper <code>ResourceLoader</code> that can perform the deserialization.
   *
   * @param bundleKey
   * @param serializedKey the String serialized key to be deserialized  @returns the <code>ResourceKey</code> that has
   *                      been deserialized
   * @throws ResourceKeyCreationException indicates an error trying to create the <code>ResourceKey</code> from the
   *                                      deserialized version
   */
  public ResourceKey deserialize( final ResourceKey bundleKey,
                                  final String serializedKey ) throws ResourceKeyCreationException {
    if ( serializedKey == null ) {
      throw new NullPointerException( "Key data must not be null." );
    }

    final Iterator values = resourceLoaders.iterator();
    while ( values.hasNext() ) {
      final ResourceLoader loader = (ResourceLoader) values.next();
      if ( loader.isSupportedDeserializer( serializedKey ) ) {
        final ResourceKey key = loader.deserialize( bundleKey, serializedKey );
        return key;
      }
    }

    throw new ResourceKeyCreationException
      ( "Unable to create key: No loader was able to handle the deserialization of the given key data: "
        + serializedKey );
  }

  /**
   * Creates a String version of the <code>ResourceKey</code> that can be deserialized with the
   * <code>deserialize()</code> method.
   *
   * @param bundleKey
   * @param key       @throw ResourceException indicates an error trying to serialize the key
   * @throws NullPointerException indicates the supplied key is <code>null</code>
   */
  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    if ( key == null ) {
      throw new NullPointerException( "Key data must not be null." );
    }

    final Iterator values = resourceLoaders.iterator();
    while ( values.hasNext() ) {
      final ResourceLoader loader = (ResourceLoader) values.next();
      if ( loader.isSupportedKey( key ) ) {
        return loader.serialize( bundleKey, key );
      }
    }

    throw new ResourceException( "Unable to find resource loader for specified key: " + key );
  }

}
