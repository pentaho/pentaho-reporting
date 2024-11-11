/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader.loader.zip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.loader.LoaderUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 05.04.2006, 15:53:21
 *
 * @author Thomas Morgner
 */
public class ZipResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = ZipResourceLoader.class.getName();
  private static final Log logger = LogFactory.getLog( ZipResourceLoader.class );

  public ZipResourceLoader() {
  }

  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key
   * @return
   */
  public boolean isSupportedKey( final ResourceKey key ) {
    return SCHEMA_NAME.equals( key.getSchema() );
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
    if ( value instanceof ZipEntryKey == false ) {
      return null;
    }

    final ZipEntryKey entryKey = (ZipEntryKey) value;
    final ResourceKey parentKey = entryKey.getZipFile().getKey();

    return new ResourceKey( parentKey, SCHEMA_NAME, entryKey.getEntryName(), factoryKeys );
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

    final String entry;
    if ( path != null ) {
      entry = LoaderUtils.mergePaths( (String) parent.getIdentifier(), path );
    } else {
      entry = (String) parent.getIdentifier();
    }

    final Map map;
    if ( factoryKeys != null ) {
      map = new HashMap();
      map.putAll( parent.getFactoryParameters() );
      map.putAll( factoryKeys );
    } else {
      map = parent.getFactoryParameters();
    }
    return new ResourceKey( parent.getParent(), parent.getSchema(), entry, map );
  }

  public URL toURL( final ResourceKey key ) {
    return null;
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "Key format is not recognized." );
    }
    return new ZipResourceData( key );
  }

  public ResourceKey deserialize( final ResourceKey bundleKey, String stringKey ) throws ResourceKeyCreationException {
    // For now, we are just going to have to pass on this one
    throw new ResourceKeyCreationException( "Can not deserialize a ZipResourceKey" );
  }

  public String serialize( ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Not supported" );
    }

    final ZipEntryKey entryKey = (ZipEntryKey) key.getIdentifier();
    final String name = entryKey.getEntryName();
    final ResourceKey zipKey = entryKey.getZipFile().getKey();

    // For now, we are just going to have to pass on this one
    throw new ResourceException( "Can not serialize a ZipResourceKey" );
  }

  public boolean isSupportedDeserializer( String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
