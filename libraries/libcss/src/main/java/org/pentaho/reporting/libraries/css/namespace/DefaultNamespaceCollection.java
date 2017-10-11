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
