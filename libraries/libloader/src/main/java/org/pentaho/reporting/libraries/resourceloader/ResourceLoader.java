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

/**
 * A resource loader knows how to get binary rawdata from a location specified by an resource key. A resource key is a
 * wrapper around any kind of data that is suitable to identify a resource location. The resource key can also hold
 * configuration data for the factory.
 * <p/>
 * If the storage system is hierarchical, a new resource key can be derived from a given path-string.
 *
 * @author Thomas Morgner
 */
public interface ResourceLoader {
  /**
   * Checks, whether this resource loader implementation was responsible for creating this key.
   *
   * @param key the key that should be tested.
   * @return true, if the key is supported.
   */
  public boolean isSupportedKey( ResourceKey key );

  /**
   * Creates a new resource key from the given object and the factory keys.
   *
   * @param value       the key value.
   * @param factoryKeys optional parameter map (can be null).
   * @return the created key or null, if the format was not recognized.
   * @throws ResourceKeyCreationException if creating the key failed.
   */
  public ResourceKey createKey( Object value,
                                Map factoryKeys )
    throws ResourceKeyCreationException;

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
  public ResourceKey deriveKey( ResourceKey parent,
                                String path,
                                Map factoryKeys )
    throws ResourceKeyCreationException;

  /**
   * Loads the binary data represented by this key.
   *
   * @param key
   * @return
   * @throws ResourceLoadingException
   */
  public ResourceData load( final ResourceKey key )
    throws ResourceLoadingException;

  /**
   * Generates a <code>URL</code> version of the supplied <code>ResourceKey</code>.
   *
   * @param key the <code>ResourceKey</code> from which a <code>URL</code> will be created
   * @return the URL representation of the <code>ResourceKey</code>
   */
  public URL toURL( ResourceKey key );


  /**
   * Determines if the resource loader is capable of deserializing the serialized version of the ResourceKey.
   *
   * @param data the serialized version of the resource key
   * @return <code>true</code> if this <code>ResourceLoader</code> is capable of deserializing the serialized version of
   * this resource key, <code>false</code> otherwise.
   */
  public boolean isSupportedDeserializer( final String data );


  /**
   * Serializes the resource key to a String representation which can be recreated using the
   * <code>deserialize(ResourceKey)<code> method.
   *
   * @param bundleKey
   * @param key
   * @return a <code>String<code> which is a serialized version of the <code>ResourceKey</code>
   * @throws ResourceException indicates an error serializing the resource key
   */
  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException;

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
  public ResourceKey deserialize( final ResourceKey bundleKey, final String stringKey )
    throws ResourceKeyCreationException;
}
