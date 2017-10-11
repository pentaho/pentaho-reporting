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

import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A immutable namespace collection. Any attempt to modify the declared namespaces creates a new copy of the map.
 *
 * @author Thomas Morgner
 */
public final class DeclaredNamespaces {
  private HashMap namespaces;

  /**
   * Creates a new namespaces collection.
   */
  public DeclaredNamespaces() {
  }

  /**
   * Creates a new namespaces collection using the given namespaces as initial values.
   *
   * @param namespaces the namespaces, never null.
   */
  public DeclaredNamespaces( final DeclaredNamespaces namespaces ) {
    if ( namespaces == null ) {
      throw new NullPointerException();
    }

    if ( namespaces.namespaces != null ) {
      this.namespaces = (HashMap) namespaces.namespaces.clone();
    }
  }

  /**
   * Adds all namespaces from the given hashmap into this map. The namespaces map must only contain string keys and
   * string values and must not contain either null-keys or null-values.
   *
   * @param newNamespaces the namespaces collection.
   * @return the created namespaces object.
   */
  public DeclaredNamespaces add( final HashMap newNamespaces ) {
    if ( newNamespaces == null ) {
      throw new NullPointerException();
    }

    final DeclaredNamespaces retval = new DeclaredNamespaces();
    if ( this.namespaces == null ) {
      retval.namespaces = new HashMap();
    } else {
      retval.namespaces = (HashMap) this.namespaces.clone();
    }

    final Iterator iterator = newNamespaces.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final String value = (String) entry.getValue();
      final String o = (String) entry.getKey();
      if ( value == null || o == null ) {
        throw new NullPointerException();
      }
      retval.namespaces.put( o, value );

    }
    return retval;
  }

  /**
   * Adds all declared namespaces from the given attribute-list into the namespaces collection.
   *
   * @param attributes the attribute list containing namespace definitions.
   * @return the new namespaces collection.
   */
  public DeclaredNamespaces add( final AttributeList attributes ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }

    DeclaredNamespaces retval = null;
    final AttributeList.AttributeEntry[] entries = attributes.toArray();
    for ( int i = 0; i < entries.length; i++ ) {
      final AttributeList.AttributeEntry entry = entries[ i ];
      final String prefix = entry.getName();
      if ( "xmlns".equals( prefix ) ) {
        if ( entry.getNamespace() == null || "".equals( entry.getNamespace() ) ) {
          if ( retval == null ) {
            retval = new DeclaredNamespaces();
            if ( namespaces != null ) {
              retval.namespaces = (HashMap) namespaces.clone();
            } else {
              retval.namespaces = new HashMap();
            }
          }
          retval.namespaces.put( entry.getValue(), "" );
        }
      } else if ( AttributeList.XMLNS_NAMESPACE.equals( entry.getNamespace() ) ) {
        if ( retval == null ) {
          retval = new DeclaredNamespaces();
          if ( namespaces != null ) {
            retval.namespaces = (HashMap) namespaces.clone();
          } else {
            retval.namespaces = new HashMap();
          }
        }
        retval.namespaces.put( entry.getValue(), prefix );
      }
    }

    if ( retval == null ) {
      return this;
    }
    return retval;
  }

  /**
   * Adds a single namespace definition to the collection.
   *
   * @param uri    the URI of the namespace.
   * @param prefix the prefix to be used for the namespace.
   * @return the new namespaces collection.
   */
  public DeclaredNamespaces add( final String uri, final String prefix ) {
    if ( uri == null ) {
      throw new NullPointerException();
    }
    if ( prefix == null ) {
      throw new NullPointerException();
    }
    final DeclaredNamespaces retval = new DeclaredNamespaces();
    if ( namespaces == null ) {
      retval.namespaces = new HashMap();
    } else {
      retval.namespaces = (HashMap) namespaces.clone();
    }
    retval.namespaces.put( uri, prefix );
    return retval;
  }

  /**
   * Looksup the prefix for the given URI. This returns null if the URI is not a declared namespace.
   *
   * @param uri the URI.
   * @return the prefix for the given URI or null, if the URI is not part of this collection.
   */
  public String getPrefix( final String uri ) {
    if ( uri == null ) {
      throw new NullPointerException();
    }

    if ( namespaces == null ) {
      return null;
    }
    return (String) namespaces.get( uri );
  }

  /**
   * Checks, whether the namespace marked by the given URI is defined in this collection.
   *
   * @param uri the URI to be checked.
   * @return true, if the URI is known, false otherwise.
   */
  public boolean isNamespaceDefined( final String uri ) {
    if ( uri == null ) {
      throw new NullPointerException();
    }

    if ( namespaces == null ) {
      return false;
    }
    return namespaces.containsKey( uri );
  }

  /**
   * Returns all known namespaces as unmodifiable map.
   *
   * @return the namespaces.
   */
  public Map getNamespaces() {
    return Collections.unmodifiableMap( namespaces );
  }

  /**
   * Checks whether the given prefix is already defined in the collection.
   *
   * @param prefix the prefix.
   * @return true, if the prefix is already used, false otherwise.
   */
  public boolean isPrefixDefined( final String prefix ) {
    return namespaces.containsValue( prefix );
  }
}
