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
