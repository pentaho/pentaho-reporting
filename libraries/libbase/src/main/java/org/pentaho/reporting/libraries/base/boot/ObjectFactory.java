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
