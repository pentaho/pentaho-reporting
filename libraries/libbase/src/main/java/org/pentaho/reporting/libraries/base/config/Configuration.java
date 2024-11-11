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


package org.pentaho.reporting.libraries.base.config;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * A simple query interface for a configuration.
 *
 * @author Thomas Morgner
 */
public interface Configuration extends Serializable, Cloneable {

  /**
   * Returns the configuration property with the specified key.
   *
   * @param key the property key.
   * @return the property value.
   */
  public String getConfigProperty( String key );

  /**
   * Returns the configuration property with the specified key (or the specified default value if there is no such
   * property).
   * <p/>
   * If the property is not defined in this configuration, the code will lookup the property in the parent
   * configuration.
   *
   * @param key          the property key.
   * @param defaultValue the default value.
   * @return the property value.
   */
  public String getConfigProperty( String key, String defaultValue );

  /**
   * Returns all keys with the given prefix.
   *
   * @param prefix the prefix
   * @return the iterator containing all keys with that prefix
   */
  public Iterator<String> findPropertyKeys( String prefix );

  /**
   * Returns the configuration properties.
   *
   * @return The configuration properties.
   */
  public Enumeration<String> getConfigProperties();

  /**
   * Returns a clone of the object.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if cloning is not supported for some reason.
   */
  public Object clone() throws CloneNotSupportedException;

}
