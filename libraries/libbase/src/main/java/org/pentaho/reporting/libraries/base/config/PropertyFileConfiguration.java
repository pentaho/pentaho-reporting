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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A report configuration that reads its values from an arbitary property file.
 *
 * @author Thomas Morgner
 */
public class PropertyFileConfiguration extends HierarchicalConfiguration {
  /**
   * A logger for debug-messages.
   */
  private static final Log LOGGER = LogFactory.getLog( PropertyFileConfiguration.class );
  /**
   * A serialization related constant.
   */
  private static final long serialVersionUID = 2423181637547944866L;

  /**
   * Default constructor.
   */
  public PropertyFileConfiguration() {
    // nothing required
  }

  /**
   * Lods the property file from a classpath resource name. The classpath resource must be loadable via
   * <code>PropertyFileConfiguration.class.getResource(..)</code>
   *
   * @param resourceName the resource name to be loaded.
   */
  public void load( final String resourceName ) {
    load( resourceName, PropertyFileConfiguration.class );
  }

  /**
   * Loads the properties stored in the given file. This method does nothing if the file does not exist or is
   * unreadable. Appends the contents of the loaded properties to the already stored contents.
   *
   * @param resourceName   the file name of the stored properties.
   * @param resourceSource the class to which relative resource paths are resolved.
   */
  public void load( final String resourceName, final Class resourceSource ) {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream
      ( resourceName, resourceSource );
    if ( in != null ) {
      try {
        load( in );
      } finally {
        try {
          in.close();
        } catch ( IOException e ) {
          // ignore
        }
      }
    } else {
      LOGGER.debug( "Configuration file not found in the classpath: " + resourceName );
    }

  }

  /**
   * Loads the properties stored in the given file. This method does nothing if the file does not exist or is
   * unreadable. Appends the contents of the loaded properties to the already stored contents.
   *
   * @param in the input stream used to read the properties.
   */
  public void load( final InputStream in ) {
    if ( in == null ) {
      throw new NullPointerException();
    }

    try {
      final BufferedInputStream bin = new BufferedInputStream( in );
      final Properties p = new Properties();
      p.load( bin );
      //noinspection UseOfPropertiesAsHashtable
      this.getConfiguration().putAll( p );
      bin.close();
    } catch ( IOException ioe ) {
      LOGGER.warn( "Unable to read configuration", ioe );
    }

  }

}
