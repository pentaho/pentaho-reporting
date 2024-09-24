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

import java.util.Collections;
import java.util.Enumeration;

/**
 * A property configuration based on system properties.
 *
 * @author Thomas Morgner
 * @noinspection AccessOfSystemProperties
 */
public class SystemPropertyConfiguration extends HierarchicalConfiguration {
  /**
   * A serialization constant.
   */
  private static final long serialVersionUID = 4200899352924290311L;

  /**
   * Creates a report configuration that includes all the system properties (whether they are related to reports or
   * not).  The parent configuration is a <code>PropertyFileConfiguration</code>.
   */
  public SystemPropertyConfiguration() {
  }

  /**
   * Sets a configuration property.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void setConfigProperty( final String key, final String value ) {
    throw new UnsupportedOperationException( "The SystemPropertyConfiguration is readOnly" );
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
    try {
      final String value = System.getProperty( key );
      if ( value != null ) {
        return value;
      }
    } catch ( SecurityException se ) {
      // ignore security exceptions, continue as if the property was not set..
    }
    return super.getConfigProperty( key, defaultValue );
  }

  /**
   * Checks, whether the given key is locally defined in the system properties.
   *
   * @param key the key that should be checked.
   * @return true, if the key is defined in the system properties, false otherwise.
   * @see HierarchicalConfiguration#isLocallyDefined(String)
   */
  public boolean isLocallyDefined( final String key ) {
    try {
      return System.getProperties().containsKey( key );
    } catch ( SecurityException se ) {
      return false;
    }
  }

  /**
   * Returns all defined configuration properties for the report. The enumeration contains all keys of the changed
   * properties, properties set from files or the system properties are not included.
   *
   * @return all defined configuration properties for the report.
   */
  public Enumeration<String> getConfigProperties() {
    try {
      return new StringEnumeration( System.getProperties().keys() );
    } catch ( SecurityException se ) {
      // should return an empty enumeration ...
      //noinspection unchecked
      return (Enumeration<String>) Collections.enumeration( Collections.EMPTY_LIST );
    }
  }
}
