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


package org.pentaho.reporting.libraries.css.namespace;

import java.util.HashMap;

/**
 * Creation-Date: 13.04.2006, 12:38:42
 *
 * @author Thomas Morgner
 */
public class DefaultNamespaceCollection implements NamespaceCollection {
  private HashMap namespaces;

  public DefaultNamespaceCollection() {
    namespaces = new HashMap();
  }

  public void addDefinitions( final NamespaceDefinition[] definitions ) {
    for ( int i = 0; i < definitions.length; i++ ) {
      final NamespaceDefinition definition = definitions[ i ];
      namespaces.put( definition.getURI(), definition );
    }
  }

  public void addDefinition( final NamespaceDefinition definition ) {
    namespaces.put( definition.getURI(), definition );
  }

  public synchronized String[] getNamespaces() {
    return (String[]) namespaces.keySet().toArray
      ( new String[ namespaces.size() ] );
  }

  public NamespaceDefinition getDefinition( final String namespace ) {
    return (NamespaceDefinition) namespaces.get( namespace );
  }
}
