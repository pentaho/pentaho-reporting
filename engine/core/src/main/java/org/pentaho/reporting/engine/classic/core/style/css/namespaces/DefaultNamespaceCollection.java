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

package org.pentaho.reporting.engine.classic.core.style.css.namespaces;

import java.util.HashMap;

public class DefaultNamespaceCollection implements NamespaceCollection {
  private HashMap<String, NamespaceDefinition> namespaces;
  private HashMap<String, String> namespaceMapping;
  private String defaultNamespace;

  public DefaultNamespaceCollection() {
    namespaces = new HashMap<String, NamespaceDefinition>();
    namespaceMapping = new HashMap<String, String>();
  }

  public void addDefinitions( final NamespaceDefinition[] definitions ) {
    for ( int i = 0; i < definitions.length; i++ ) {
      final NamespaceDefinition definition = definitions[i];
      addDefinition( definition );
    }
  }

  public void addNamespaceMapping( String prefix, String uri ) {
    namespaceMapping.put( prefix, uri );
  }

  public String lookupNamespaceURI( final String prefix ) {
    return namespaceMapping.get( prefix );
  }

  public void addDefinition( final NamespaceDefinition definition ) {
    namespaces.put( definition.getURI(), definition );
    namespaceMapping.put( definition.getPrefix(), definition.getURI() );
  }

  public void setDefaultNamespaceURI( final String defaultNamespace ) {
    this.defaultNamespace = defaultNamespace;
  }

  public String getDefaultNamespaceURI() {
    return defaultNamespace;
  }

  public synchronized String[] getNamespaces() {
    return namespaces.keySet().toArray( new String[namespaces.size()] );
  }

  public NamespaceDefinition getDefinition( final String namespace ) {
    return namespaces.get( namespace );
  }
}
