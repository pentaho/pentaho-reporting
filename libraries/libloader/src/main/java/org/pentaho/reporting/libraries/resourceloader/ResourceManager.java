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
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.cache.BundleCacheResourceWrapper;
import org.pentaho.reporting.libraries.resourceloader.cache.NullResourceBundleDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.NullResourceDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.NullResourceFactoryCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCacheEntry;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceBundleDataCacheProvider;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCacheEntry;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceDataCacheProvider;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceFactoryCache;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceFactoryCacheProvider;
import org.pentaho.reporting.libraries.resourceloader.modules.cache.ehcache.EHCacheModule;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The resource manager takes care about the loaded resources, performs caching, if needed and is the central instance
 * when dealing with resources. Resource loading is a two-step process. In the first step, the {@link ResourceLoader}
 * accesses the physical storage or network connection to read in the binary data. The loaded {@link ResourceData}
 * carries versioning information with it an can be cached indendently from the produced result. Once the loading is
 * complete, a {@link ResourceFactory} interprets the binary data and produces a Java-Object from it.
 * <p/>
 * Resources are identified by an Resource-Key and some optional loader parameters (which can be used to parametrize the
 * resource-factories).
 *
 * @author Thomas Morgner
 * @see ResourceData
 * @see ResourceLoader
 * @see ResourceFactory
 */
public final class ResourceManager {
  private static final Log logger = LogFactory.getLog( ResourceManager.class );
  private ResourceManagerBackend backend;

  public static final String BUNDLE_LOADER_PREFIX = "org.pentaho.reporting.libraries.resourceloader.bundle.loader.";
  public static final String LOADER_PREFIX = "org.pentaho.reporting.libraries.resourceloader.loader.";
  public static final String FACTORY_TYPE_PREFIX = "org.pentaho.reporting.libraries.resourceloader.factory.type.";

  private ResourceDataCache dataCache;
  private ResourceBundleDataCache bundleCache;
  private ResourceFactoryCache factoryCache;

  /**
   * A set that contains the class-names of all cache-modules, which could not be instantiated correctly. This set is
   * used to limit the number of warnings in the log to exactly one per class.
   */
  private static final Set<Class> failedModules = new HashSet<Class>();

  /**
   * Default Constructor.
   */
  public ResourceManager() {
    this( new DefaultResourceManagerBackend() );
  }

  public ResourceManager( final ResourceManagerBackend resourceManagerBackend ) {
    if ( resourceManagerBackend == null ) {
      throw new NullPointerException();
    }
    this.backend = resourceManagerBackend;
    this.bundleCache = new NullResourceBundleDataCache();
    this.dataCache = new NullResourceDataCache();
    this.factoryCache = new NullResourceFactoryCache();
    registerDefaults();
  }

  public ResourceManager( final ResourceManager parent, final ResourceManagerBackend backend ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }

    this.backend = backend;
    this.bundleCache = parent.getBundleCache();
    this.dataCache = parent.getDataCache();
    this.factoryCache = parent.getFactoryCache();
    registerDefaults();
  }

  public ResourceManagerBackend getBackend() {
    return backend;
  }

  /**
   * Creates a ResourceKey that carries no Loader-Parameters from the given object.
   *
   * @param data the key-data
   * @return the generated resource-key, never null.
   * @throws ResourceKeyCreationException if the key-creation failed.
   */
  public ResourceKey createKey( final Object data )
    throws ResourceKeyCreationException {
    return createKey( data, null );
  }

  /**
   * Creates a ResourceKey that carries the given Loader-Parameters contained in the optional map.
   *
   * @param data       the key-data
   * @param parameters an optional map of parameters.
   * @return the generated resource-key, never null.
   * @throws ResourceKeyCreationException if the key-creation failed.
   */
  public ResourceKey createKey( final Object data, final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException {
    return backend.createKey( data, parameters );
  }

  /**
   * Derives a new key from the given resource-key. Only keys for a hierarchical storage system (like file-systems or
   * URLs) can have derived keys. Since LibLoader 0.3.0 only hierarchical keys can be derived. For that, the deriving
   * path must be given as String.
   * <p/>
   * Before trying to derive the key, the system tries to interpret the path as absolute key-value.
   *
   * @param parent the parent key, must never be null
   * @param path   the relative path, that is used to derive the key.
   * @return the derived key.
   * @throws ResourceKeyCreationException if deriving the key failed.
   */
  public ResourceKey deriveKey( final ResourceKey parent, final String path )
    throws ResourceKeyCreationException {
    return deriveKey( parent, path, null );
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
  public ResourceKey deriveKey( final ResourceKey parent,
                                final String path,
                                final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException {
    return backend.deriveKey( parent, path, parameters );
  }

  /**
   * Tries to convert the resource-key into an URL. Not all resource-keys have an URL representation. This method exists
   * to make it easier to connect LibLoader to other resource-loading frameworks.
   *
   * @param key the resource-key
   * @return the URL for the key, or null if there is no such key.
   */
  public URL toURL( final ResourceKey key ) {
    return backend.toURL( key );
  }

  public Resource createDirectly( final Object keyValue, final Class target )
    throws ResourceLoadingException,
    ResourceCreationException,
    ResourceKeyCreationException {
    final ResourceKey key = createKey( keyValue );
    return create( key, null, target );
  }


  /**
   * Tries to find the first resource-bundle-loader that would be able to process the key.
   *
   * @param key the resource-key.
   * @return the resourceloader for that key, or null, if no resource-loader is able to process the key.
   * @throws ResourceLoadingException if an error occured.
   */
  public synchronized ResourceBundleData loadResourceBundle( final ResourceKey key ) throws ResourceLoadingException {
    final ResourceBundleDataCache bundleCache = getBundleCache();
    final ResourceBundleDataCacheEntry cached = bundleCache.get( key );
    if ( cached != null ) {
      final ResourceBundleData data = cached.getData();
      // check, whether it is valid.

      final long version = data.getVersion( this );
      if ( ( cached.getStoredVersion() < 0 ) ||
        ( version >= 0 && cached.getStoredVersion() == version ) ) {
        // now also make sure that the underlying data has not changed.
        // This may look a bit superfluous, but the repository may not provide
        // sensible cacheable information.
        //
        // As condition of satisfaction, try to find the first piece of data that
        // is in the cache and see whether it has changed. 
        ResourceKey bundleKey = data.getBundleKey();
        int counter = 1;
        while ( bundleKey != null ) {
          final ResourceDataCacheEntry bundleRawDataCacheEntry = getDataCache().get( bundleKey );
          if ( bundleRawDataCacheEntry != null ) {
            final ResourceData bundleRawData = bundleRawDataCacheEntry.getData();
            if ( bundleRawData != null ) {
              if ( isValidData( bundleRawDataCacheEntry, bundleRawData ) ) {
                logger.debug( "Returning cached entry [" + counter + "]" );
                return data;
              }
              getDataCache().remove( bundleRawData );
            }
          }
          bundleKey = bundleKey.getParent();
          counter += 1;
        }
      }
      bundleCache.remove( data );
    }

    final ResourceBundleData data = backend.loadResourceBundle( this, key );
    if ( data != null && isResourceDataCacheable( data ) ) {
      bundleCache.put( this, data );
    }
    return data;
  }

  private boolean isResourceDataCacheable( final ResourceData data ) {
    try {
      return data.getVersion( this ) != -1;
    } catch ( ResourceLoadingException e ) {
      return false;
    }
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    final ResourceBundleData bundle = loadResourceBundle( key );
    if ( bundle != null ) {
      logger.debug( "Loaded bundle for key " + key );
      return bundle;
    }
    final ResourceKey parent = key.getParent();
    if ( parent != null ) {
      // try to load the bundle data of the parent
      final ResourceBundleData parentData = loadResourceBundle( parent );
      if ( parentData != null ) {
        logger.debug( "Loaded bundle for key (derivate) " + key );
        return parentData.deriveData( key );
      }
    }
    return loadRawData( key );
  }

  private boolean isValidData( final ResourceDataCacheEntry cached,
                               final ResourceData data ) throws ResourceLoadingException {
    // check, whether it is valid.
    if ( cached.getStoredVersion() < 0 ) {
      // a non versioned entry is always valid. (Maybe this is from a Jar-URL?)
      return true;
    }

    final long version = data.getVersion( this );
    if ( version < 0 ) {
      // the system is no longer able to retrieve the version information?
      // (but versioning information must have been available in the past)
      // oh, that's bad. Assume the worst and re-read the data.
      return false;
    }

    if ( cached.getStoredVersion() == version ) {
      return true;
    } else {
      return false;
    }
  }

  public synchronized ResourceData loadRawData( final ResourceKey key )
    throws UnrecognizedLoaderException, ResourceLoadingException {
    final ResourceDataCache dataCache = getDataCache();
    // Alternative 3: This is a plain resource and not contained in a bundle. Load as binary data
    final ResourceDataCacheEntry cached = dataCache.get( key );
    if ( cached != null ) {
      final ResourceData data = cached.getData();
      if ( data != null ) {
        if ( isValidData( cached, data ) ) {
          return data;
        }
        dataCache.remove( data );
      }
    }

    final ResourceData data = backend.loadRawData( this, key );
    if ( data != null && isResourceDataCacheable( data ) ) {
      dataCache.put( this, data );
    }
    return data;
  }

  public Resource create( final ResourceKey key, final ResourceKey context, final Class target )
    throws ResourceLoadingException, ResourceCreationException {
    if ( target == null ) {
      throw new NullPointerException( "Target must not be null" );
    }
    if ( key == null ) {
      throw new NullPointerException( "Key must not be null." );
    }
    return create( key, context, new Class[] { target } );
  }

  public Resource create( final ResourceKey key, final ResourceKey context )
    throws ResourceLoadingException, ResourceCreationException {
    return create( key, context, (Class[]) null );
  }

  public Resource create( final ResourceKey key, final ResourceKey context, final Class[] target )
    throws ResourceLoadingException, ResourceCreationException {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final ResourceFactoryCache factoryCache = getFactoryCache();
    // ok, we have a handle to the data, and the data is current.
    // Lets check whether we also have a cached result.
    final Resource resource = factoryCache.get( key, target );
    if ( resource != null ) {
      if ( backend.isResourceUnchanged( this, resource ) ) {
        // mama, look i am a good cache manager ...
        return resource;
      } else {
        // someone evil changed one of the dependent resources ...
        factoryCache.remove( resource );
      }
    }

    final ResourceData loadedData = load( key );
    final Resource newResource;
    if ( loadedData instanceof ResourceBundleData ) {
      final ResourceBundleData resourceBundleData = (ResourceBundleData) loadedData;
      final ResourceManager derivedManager = resourceBundleData.deriveManager( this );
      newResource = backend.create( derivedManager, resourceBundleData, context, target );
      if ( isResourceCacheable( newResource ) ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Storing created bundle-resource for key: " + key );
        }
        factoryCache.put( newResource );
        if ( key != newResource.getSource() ) {
          factoryCache.put( new BundleCacheResourceWrapper( newResource, key ) );
        }
      } else {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Created bundle-resource is not cacheable for " + key );
        }
      }
    } else {
      newResource = backend.create( this, loadedData, context, target );
      if ( isResourceCacheable( newResource ) ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Storing created resource for key: " + key );
        }
        factoryCache.put( newResource );
      } else {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Created resource is not cacheable for " + key );
        }
      }
    }
    return newResource;
  }

  private boolean isResourceCacheable( final Resource newResource ) {
    final ResourceKey source = newResource.getSource();
    if ( newResource.isTemporaryResult() ) {
      return false;
    }
    if ( newResource.getVersion( source ) == -1 ) {
      return false;
    }
    final ResourceKey[] keys = newResource.getDependencies();
    for ( int i = 0; i < keys.length; i++ ) {
      if ( newResource.getVersion( keys[ i ] ) == -1 ) {
        return false;
      }
    }
    return true;
  }


  public ResourceDataCache getDataCache() {
    return dataCache;
  }

  public void setDataCache( final ResourceDataCache dataCache ) {
    if ( dataCache == null ) {
      throw new NullPointerException();
    }
    this.dataCache = dataCache;
  }

  public ResourceFactoryCache getFactoryCache() {
    return factoryCache;
  }

  public void setFactoryCache( final ResourceFactoryCache factoryCache ) {
    if ( factoryCache == null ) {
      throw new NullPointerException();
    }
    this.factoryCache = factoryCache;
  }

  public ResourceBundleDataCache getBundleCache() {
    return bundleCache;
  }

  public void setBundleCache( final ResourceBundleDataCache bundleCache ) {
    if ( bundleCache == null ) {
      throw new NullPointerException();
    }
    this.bundleCache = bundleCache;
  }

  public void registerDefaults() {
    // Create all known resource loaders ...
    registerDefaultLoaders();

    // Register all known factories ...
    registerDefaultFactories();
    // add the caches ..
    registerDataCache();
    registerBundleDataCache();
    registerFactoryCache();
  }

  public void registerDefaultFactories() {
    backend.registerDefaultFactories();
  }

  public void registerBundleDataCache() {
    try {
      final ObjectFactory objectFactory = LibLoaderBoot.getInstance().getObjectFactory();
      final ResourceBundleDataCacheProvider maybeDataCacheProvider =
        objectFactory.get( ResourceBundleDataCacheProvider.class );
      final ResourceBundleDataCache cache = maybeDataCacheProvider.createBundleDataCache();
      if ( cache != null ) {
        setBundleCache( cache );
      }
    } catch ( Throwable e ) {
      // ok, did not work ...
      synchronized( failedModules ) {
        if ( failedModules.contains( ResourceBundleDataCacheProvider.class ) == false ) {
          logger.warn( "Failed to create data cache: " + e.getLocalizedMessage() );
          failedModules.add( ResourceBundleDataCacheProvider.class );
        }
      }
    }
  }

  public void registerDataCache() {
    try {
      final ObjectFactory objectFactory = LibLoaderBoot.getInstance().getObjectFactory();
      final ResourceDataCacheProvider maybeDataCacheProvider = objectFactory.get( ResourceDataCacheProvider.class );
      final ResourceDataCache cache = maybeDataCacheProvider.createDataCache();
      if ( cache != null ) {
        setDataCache( cache );
      }
    } catch ( Throwable e ) {
      // ok, did not work ...
      synchronized( failedModules ) {
        if ( failedModules.contains( ResourceDataCacheProvider.class ) == false ) {
          logger.warn( "Failed to create data cache: " + e.getLocalizedMessage() );
          failedModules.add( ResourceDataCacheProvider.class );
        }
      }
    }
  }

  public void registerFactoryCache() {
    try {
      final ObjectFactory objectFactory = LibLoaderBoot.getInstance().getObjectFactory();
      final ResourceFactoryCacheProvider maybeDataCacheProvider =
        objectFactory.get( ResourceFactoryCacheProvider.class );
      final ResourceFactoryCache cache = maybeDataCacheProvider.createFactoryCache();
      if ( cache != null ) {
        setFactoryCache( cache );
      }
    } catch ( Throwable e ) {
      synchronized( failedModules ) {
        if ( failedModules.contains( ResourceFactoryCacheProvider.class ) == false ) {
          logger.warn( "Failed to create factory cache: " + e.getLocalizedMessage() );
          failedModules.add( ResourceFactoryCacheProvider.class );
        }
      }
    }
  }

  public void registerDefaultLoaders() {
    backend.registerDefaultLoaders();
  }

  public void registerBundleLoader( final ResourceBundleLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException();
    }
    backend.registerBundleLoader( loader );
  }

  public void registerLoader( final ResourceLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException();
    }
    backend.registerLoader( loader );
  }

  public void registerFactory( final ResourceFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    backend.registerFactory( factory );
  }

  public void shutDown() {
    factoryCache.shutdown();
    dataCache.shutdown();
  }

  /**
   * Creates a String version of the <code>ResourceKey</code> that can be deserialized with the
   * <code>deserialize()</code> method.
   *
   * @param bundleKey the key to the bundle containing the resource, or null if no bundle exists.
   * @param key       the key to be serialized
   * @throws ResourceException    indicates an error trying to serialize the key
   * @throws NullPointerException indicates the supplied key is <code>null</code>
   */
  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    return backend.serialize( bundleKey, key );
  }

  /**
   * Converts a serialized version of a <code>ResourceKey</code> into an actual <code>ResourceKey</code> by locating the
   * proper <code>ResourceLoader</code> that can perform the deserialization.
   *
   * @param serializedKey the String serialized key to be deserialized
   * @return the <code>ResourceKey</code> that has been deserialized
   * @throws ResourceKeyCreationException indicates an error trying to create the <code>ResourceKey</code> from the
   *                                      deserialized version
   */
  public ResourceKey deserialize( final ResourceKey bundleKey,
                                  final String serializedKey ) throws ResourceKeyCreationException {
    return backend.deserialize( bundleKey, serializedKey );
  }

  public ResourceKey createOrDeriveKey( final ResourceKey context,
                                        final Object value,
                                        final Object baseURL ) throws ResourceKeyCreationException {
    if ( value == null ) {
      throw new ResourceKeyCreationException( "Empty key is invalid" );
    }
    final ResourceKey key;
    if ( value instanceof ResourceKey ) {
      key = (ResourceKey) value;
    } else if ( value instanceof Blob ) {
      try {
        final Blob b = (Blob) value;
        final byte[] data = IOUtils.getInstance().readBlob( b );
        key = createKey( data );
      } catch ( IOException ioe ) {
        throw new ResourceKeyCreationException( "Failed to load data from blob", ioe );
      } catch ( SQLException e ) {
        throw new ResourceKeyCreationException( "Failed to load data from blob", e );
      }
    } else if ( value instanceof String ) {
      final String source = (String) value;
      if ( StringUtils.isEmpty( source ) ) {
        throw new ResourceKeyCreationException( "Empty key is invalid" );
      }
      try {
        if ( baseURL instanceof String ) {
          final ResourceKey baseKey = createKeyFromString( null, (String) baseURL );
          return createKeyFromString( baseKey, source );
        } else if ( baseURL instanceof ResourceKey ) {
          final ResourceKey baseKey = (ResourceKey) baseURL;
          return createKeyFromString( baseKey, source );
        } else if ( baseURL != null ) {
          // if a base-url object is given, we assume that it is indeed valid.
          final ResourceKey baseKey = createKey( baseURL );
          return createKeyFromString( baseKey, source );
        }
      } catch ( ResourceException rke ) {
        logger
          .debug( "Failed to resolve key via given base-url. Try to treat resource as absolute resource instead", rke );
      }

      key = createKeyFromString( context, source );
    } else {
      // URLs, Files, byte-arrays etc are treated as absolute objects
      key = createKey( value );
    }

    return key;
  }


  private ResourceKey createKeyFromString( final ResourceKey contextKey,
                                           final String file ) throws ResourceKeyCreationException {
    try {
      if ( contextKey != null ) {
        return deriveKey( contextKey, file );
      }
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load datasource as derived path: ", re );
    }

    try {
      return createKey( new URL( file ) );
    } catch ( ResourceException re ) {
      logger.debug( "Failed to load datasource as URL: ", re );
    } catch ( MalformedURLException e ) {
      //
    }

    try {
      return createKey( new File( file ) );
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load datasource as file: ", re );
    }

    return createKey( file );
  }
}
