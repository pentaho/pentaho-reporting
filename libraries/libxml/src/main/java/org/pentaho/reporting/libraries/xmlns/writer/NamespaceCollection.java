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
