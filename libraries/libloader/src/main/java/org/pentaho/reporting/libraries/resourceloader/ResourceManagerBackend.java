/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader;

import java.net.URL;
import java.util.Map;

public interface ResourceManagerBackend {
  public ResourceKey createKey( final Object data, final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException;

  public ResourceKey deriveKey( final ResourceKey parent, final String path,
                                final Map<? extends ParameterKey, ? extends Object> parameters )
    throws ResourceKeyCreationException;

  public URL toURL( final ResourceKey key );

  public Resource create( final ResourceManager frontEnd, final ResourceData key, final ResourceKey context,
                          final Class[] target )
    throws ResourceLoadingException, ResourceCreationException;

  public ResourceBundleData loadResourceBundle( final ResourceManager frontEnd, final ResourceKey key )
    throws ResourceLoadingException;

  public void registerDefaultFactories();

  public void registerDefaultLoaders();

  public void registerBundleLoader( final ResourceBundleLoader loader );

  public void registerLoader( final ResourceLoader loader );

  public void registerFactory( final ResourceFactory factory );

  public ResourceData loadRawData( final ResourceManager frontEnd, final ResourceKey key )
    throws ResourceLoadingException, UnrecognizedLoaderException;

  public boolean isResourceUnchanged( final ResourceManager resourceManager, final Resource resource )
    throws ResourceLoadingException;

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException;

  public ResourceKey deserialize( final ResourceKey bundleKey, final String serializedKey )
    throws ResourceKeyCreationException;
}
