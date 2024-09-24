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

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

/**
 * Default configuration.
 *
 * @author Thomas Morgner.
 */
public class DefaultConfiguration extends Properties
  implements ModifiableConfiguration {

  /**
   * A constant for serialization support.
   */
  private static final long serialVersionUID = -7812745196042984458L;

  /**
   * Creates an empty property list with no default values.
   */
  public DefaultConfiguration() {
  }

  /**
   * Returns the configuration property with the specified key.
   *
   * @param key the property key.
   * @return the property value.
   */
  public String getConfigProperty( final String key ) {
    return getProperty( key );
  }

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
  public String getConfigProperty( final String key, final String defaultValue ) {
    return getProperty( key, defaultValue );
  }

  /**
   * Searches all property keys that start with a given prefix.
   *
   * @param prefix the prefix that all selected property keys should share
   * @return the properties as iterator.
   */
  public Iterator<String> findPropertyKeys( final String prefix ) {
    String[] array = new String[ size() ];
    int found = 0;
    Enumeration enum1 = keys();
    while ( enum1.hasMoreElements() ) {
      String key = (String) enum1.nextElement();
      if ( key.startsWith( prefix ) ) {
        array[ found ] = key;
        found++;
      }
    }
    if ( found == 0 ) {
      return Collections.emptyIterator();
    } else if ( found == 1 ) {
      return Collections.singleton( array[ 0 ] ).iterator();
    } else {
      String[] returned = new String[ found ];
      System.arraycopy( array, 0, returned, 0, found );
      Arrays.sort( returned );
      return Arrays.asList( returned ).iterator();
    }
  }

  public Enumeration<String> getConfigProperties() {
    return new StringEnumeration( keys() );
  }

  /**
   * Sets the value of a configuration property.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void setConfigProperty( final String key, final String value ) {
    if ( value == null ) {
      remove( key );
    } else {
      setProperty( key, value );
    }
  }
}
