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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * A wrapper for the extended configuration interface around a plain configuration.
 *
 * @author Thomas Morgner
 */
public class ExtendedConfigurationWrapper implements ExtendedConfiguration {
  /**
   * A constant for serialization support.
   */
  private static final long serialVersionUID = -3600564448124904906L;
  /**
   * The base configuration.
   */
  private Configuration parent;

  /**
   * Creates a wrapper around the given configuration.
   *
   * @param parent the wrapped up configuration.
   * @throws NullPointerException if the parent is null.
   */
  public ExtendedConfigurationWrapper( final Configuration parent ) {
    if ( parent == null ) {
      throw new NullPointerException( "Parent given must not be null" );
    }
    this.parent = parent;
  }

  /**
   * Returns the boolean value of a given configuration property. The boolean value true is returned, if the contained
   * string is equal to 'true'.
   *
   * @param name the name of the property
   * @return the boolean value of the property.
   */
  public boolean getBoolProperty( final String name ) {
    return getBoolProperty( name, false );
  }

  /**
   * Returns the boolean value of a given configuration property. The boolean value true is returned, if the contained
   * string is equal to 'true'. If the property is not set, the default value is returned.
   *
   * @param name         the name of the property
   * @param defaultValue the default value to be returned if the property is not set
   * @return the boolean value of the property.
   */
  public boolean getBoolProperty( final String name,
                                  final boolean defaultValue ) {
    return "true".equals( parent.getConfigProperty( name, String.valueOf( defaultValue ) ) );
  }

  /**
   * Returns a given property as int value. Zero is returned if the property value is no number or the property is not
   * set.
   *
   * @param name the name of the property
   * @return the parsed number value or zero
   */
  public int getIntProperty( final String name ) {
    return getIntProperty( name, 0 );
  }

  /**
   * Returns a given property as int value. The specified default value is returned if the property value is no number
   * or the property is not set.
   *
   * @param name         the name of the property
   * @param defaultValue the value to be returned if the property is no integer value
   * @return the parsed number value or the specified default value
   */
  public int getIntProperty( final String name,
                             final int defaultValue ) {
    final String retval = parent.getConfigProperty( name );
    if ( retval == null ) {
      return defaultValue;
    }
    try {
      return Integer.parseInt( retval );
    } catch ( Exception e ) {
      return defaultValue;
    }
  }

  /**
   * Checks, whether a given property is defined.
   *
   * @param name the name of the property
   * @return true, if the property is defined, false otherwise.
   */
  public boolean isPropertySet( final String name ) {
    return parent.getConfigProperty( name ) != null;
  }

  /**
   * Returns all keys with the given prefix.
   *
   * @param prefix the prefix
   * @return the iterator containing all keys with that prefix
   */
  public Iterator<String> findPropertyKeys( final String prefix ) {
    return parent.findPropertyKeys( prefix );
  }

  /**
   * Returns the configuration property with the specified key.
   *
   * @param key the property key.
   * @return the property value.
   */
  public String getConfigProperty( final String key ) {
    return parent.getConfigProperty( key );
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
    return parent.getConfigProperty( key, defaultValue );
  }

  public Enumeration<String> getConfigProperties() {
    return parent.getConfigProperties();
  }

  public Object clone() throws CloneNotSupportedException {
    final ExtendedConfigurationWrapper wrapper = (ExtendedConfigurationWrapper) super.clone();
    wrapper.parent = (Configuration) parent.clone();
    return parent;
  }
}
