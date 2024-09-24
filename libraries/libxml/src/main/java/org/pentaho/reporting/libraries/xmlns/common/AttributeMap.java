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
* Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A attribute map holding &lt;namespace;name&gt;-value pairs.
 *
 * @author Thomas Morgner
 */
public class AttributeMap<T> implements Serializable, Cloneable {
  public static class DualKey implements Serializable {
    public final String namespace;
    public final String name;

    private DualKey( final String namespace, final String name ) {
      this.namespace = namespace;
      this.name = name;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final DualKey dualKey = (DualKey) o;

      if ( !name.equals( dualKey.name ) ) {
        return false;
      }
      if ( !namespace.equals( dualKey.namespace ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = name.hashCode();
      result = 31 * result + namespace.hashCode();
      return result;
    }

    public String toString() {
      return String.format( "DualKey(%s, %s)", namespace, name );
    }
  }

  private static final String[] EMPTY_NAMESPACES = new String[0];

  private static final long serialVersionUID = -7442871030874215436L;
  private LinkedHashMap<DualKey, T> content;

  /**
   * Default constructor.
   */
  public AttributeMap() {
  }

  /**
   * Creates a new attribute map using the given parameter as source for the initial values.
   *
   * @param copy the attribute map that should be copied.
   * @noinspection unchecked
   */
  public AttributeMap( final AttributeMap copy ) {
    if ( copy == null ) {
      return;
    }

    if ( copy.content != null ) {
      this.content = (LinkedHashMap<DualKey, T>) copy.content.clone();
    }
  }

  /**
   * Creates a copy of this map.
   *
   * @return the clone.
   * @noinspection CloneDoesntDeclareCloneNotSupportedException, unchecked
   */
  public AttributeMap<T> clone() {
    try {
      final AttributeMap<T> map = (AttributeMap<T>) super.clone();
      if ( content != null ) {
        map.content = (LinkedHashMap<DualKey, T>) content.clone();
      }
      return map;
    } catch ( final CloneNotSupportedException cne ) {
      // ignored
      throw new IllegalStateException( "Cannot happen: Clone not supported exception" );
    }
  }

  /**
   * Defines the attribute for the given namespace and attribute name.
   *
   * @param namespace the namespace under which the value should be stored.
   * @param attribute the attribute name under which the value should be stored within the namespace.
   * @param value     the value.
   * @return the previously stored value at that position.
   */
  public T setAttribute( final String namespace,
                         final String attribute,
                         final T value ) {
    if ( namespace == null ) {
      throw new NullPointerException( "Attribute namespace must not be null" );
    }
    if ( attribute == null ) {
      throw new NullPointerException( "Attribute name must not be null" );
    }

    if ( content == null ) {
      content = new LinkedHashMap<DualKey, T>();
    }
    if ( value != null ) {
      return content.put( new DualKey( namespace, attribute ), value );
    } else {
      return content.remove( new DualKey( namespace, attribute ) );
    }
  }

  /**
   * Returns the attribute value for the given namespace and attribute-name.
   *
   * @param namespace the namespace.
   * @param attribute the attribute name.
   * @return the value or null, if there is no such namespace/attribute name combination.
   */
  public T getAttribute( final String namespace,
                         final String attribute ) {
    if ( namespace == null ) {
      throw new NullPointerException( "Attribute namespace must not be null" );
    }
    if ( attribute == null ) {
      throw new NullPointerException( "Attribute name must not be null" );
    }
    if ( content == null ) {
      return null;
    }
    return content.get( new DualKey( namespace, attribute ) );
  }

  /**
   * Looks up all namespaces and returns the value from the first namespace that has this attribute defined.
   *
   * @param attribute the the attribute name.
   * @return the object from the first namespace that carries this attribute or null, if none of the namespaces has such
   * an attribute defined.
   */
  public T getFirstAttribute( final String attribute ) {
    if ( attribute == null ) {
      throw new NullPointerException( "Attribute name must not be null" );
    }

    if ( content != null ) {
      for ( final Map.Entry<DualKey, T> entry : content.entrySet() ) {
        if ( attribute.equals( entry.getKey().name ) ) {
          return entry.getValue();
        }
      }
    }

    return null;
  }

  /**
   * Returns all attributes of the given namespace as unmodifiable map.
   *
   * @param namespace the namespace for which the attributes should be returned.
   * @return the map, never null.
   */
  public Map<String, T> getAttributes( final String namespace ) {
    if ( namespace == null ) {
      throw new NullPointerException( "Attribute namespace must not be null" );
    }

    if ( content == null ) {
      return Collections.emptyMap();
    }
    LinkedHashMap<String, T> entries = new LinkedHashMap<String, T>();
    for ( final Map.Entry<DualKey, T> entry : content.entrySet() ) {
      DualKey key = entry.getKey();
      if ( namespace.equals( key.namespace ) ) {
        entries.put( key.name, entry.getValue() );
      }
    }
    return Collections.unmodifiableMap( entries );
  }

  /**
   * Returns all names for the given namespace that have values in this map.
   *
   * @param namespace the namespace for which known attribute names should be looked up.
   * @return the names stored for the given namespace.
   */
  public String[] getNames( final String namespace ) {
    if ( namespace == null ) {
      throw new NullPointerException( "Attribute namespace must not be null" );
    }

    if ( content == null ) {
      return AttributeMap.EMPTY_NAMESPACES;
    }

    List<String> entries = new ArrayList<String>();
    for ( final Map.Entry<DualKey, T> entry : content.entrySet() ) {
      DualKey key = entry.getKey();
      if ( namespace.equals( key.namespace ) ) {
        entries.add( key.name );
      }
    }

    return entries.toArray( new String[ entries.size() ] );
  }

  public Set<DualKey> keySet() {
    if ( content == null ) {
      return Collections.emptySet();
    }
    return content.keySet();
  }

  /**
   * Returns all namespaces that have values in this map.
   *
   * @return the namespaces stored in this map.
   */
  public String[] getNameSpaces() {
    if ( content == null ) {
      return AttributeMap.EMPTY_NAMESPACES;
    }

    LinkedHashSet<String> entries = new LinkedHashSet<String>();
    for ( final Map.Entry<DualKey, T> entry : content.entrySet() ) {
      entries.add( entry.getKey().namespace );
    }

    return entries.toArray( new String[entries.size()] );
  }

  public void putAll( final AttributeMap<T> attributeMap ) {
    if ( attributeMap.content == null ) {
      return;
    }

    if ( content == null ) {
      content = (LinkedHashMap<DualKey, T>) attributeMap.content.clone();
    } else {
      content.putAll( attributeMap.content );
    }
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final AttributeMap that = (AttributeMap) o;
    if ( content != null ? !content.equals( that.content ) : that.content != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    if ( content != null ) {
      return content.hashCode();
    }
    return 0;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "AttributeMap" );
    sb.append( "{content=" ).append( content );
    sb.append( '}' );
    return sb.toString();
  }

  public void clear() {
    if ( content != null ) {
      content.clear();
    }
  }
}
