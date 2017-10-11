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
