/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
