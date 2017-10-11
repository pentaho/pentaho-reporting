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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...  
 * All rights reserved.
 */

package org.pentaho.reporting.libraries.base.boot;

public interface ObjectFactory {
  /**
   * Retrieves an instance of a Pentaho BI Server API interface using the simple interface name (interfaceClass name
   * without the package) as the object key.  If an appropriate implementation does not exist the factory implementation
   * should create it.
   *
   * @param interfaceClass the type of object to retrieve (retrieved object will be returned as this type)
   * @return the implementation object typed to interfaceClass, never null.
   * @throws ObjectFactoryException if the object is undefined.
   */
  public <T> T get( Class<T> interfaceClass );

  /**
   * Retrieves an instance of a Pentaho BI Server API interface by the given object key. If an appropriate
   * implementation does not exist the factory implementation should create it.
   *
   * @param interfaceClass the type of object to retrieve (retrieved object will be returned as this type)
   * @param key            the object identifier, typically the interface name
   * @return the implementation object typed to interfaceClass, never null.
   * @throws ObjectFactoryException if the object is undefined.
   */
  public <T> T get( Class<T> interfaceClass, String key );
}
