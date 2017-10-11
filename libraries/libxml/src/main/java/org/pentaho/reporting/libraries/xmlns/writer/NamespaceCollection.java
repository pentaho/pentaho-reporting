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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Holds a set of namespace definitions that have been declared in a libbase-configuration.
 *
 * @author Thomas Morgner
 */
public class NamespaceCollection {
  private HashMap entries;

  /**
   * Default constructor.
   */
  public NamespaceCollection() {
    entries = new HashMap();
  }

  /**
   * Configures the namespace collection and adds all namespace definitions found in the configuration under the given
   * prefix to the collection.
   *
   * @param config the configuration from where to read the namespaces.
   * @param prefix the configuration prefix for filtering the configuration entries.
   */
  public void configure( final Configuration config, final String prefix ) {
    final Iterator keys = config.findPropertyKeys( prefix );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      if ( key.endsWith( ".Uri" ) == false ) {
        continue;
      }
      final String nsPrefix = key.substring( 0, key.length() - 3 );
      final String uri = config.getConfigProperty( key );
      if ( uri == null ) {
        continue;
      }
      final String trimmedUri = uri.trim();
      if ( trimmedUri.length() == 0 ) {
        continue;
      }
      final String prefixAttr = config.getConfigProperty( nsPrefix + "Prefix" );
      if ( prefixAttr != null ) {
        entries.put( trimmedUri, prefixAttr );
      }
    }
  }

  /**
   * Returns the prefered namespace prefix for the given namespace URI.
   *
   * @param uri the namespace for which a prefix should be looked up.
   * @return the defined prefix.
   */
  public String getPrefix( final String uri ) {
    return (String) entries.get( uri );
  }

  /**
   * Returns all known namespaces for which definitions exist in this collection.
   *
   * @return the defined uris as array.
   */
  public String[] getDefinedUris() {
    return (String[]) entries.keySet().toArray( new String[ entries.size() ] );
  }
}
