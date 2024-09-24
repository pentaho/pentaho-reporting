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

package org.pentaho.reporting.libraries.xmlns.common;

import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Arrays;
import java.util.Iterator;

/**
 * The attribute list is used by a writer to specify the attributes of an XML element in a certain order.
 *
 * @author Thomas Morgner
 */
public class AttributeList {
  /**
   * A constant containing the XML-Namespace namespace identifier.
   */
  public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";
  /**
   * A constant containing the XML namespace identifier.
   */
  public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

  /**
   * A name/value pair of the attribute list.
   */
  public static class AttributeEntry {
    /**
     * The namespace of the attribute entry.
     */
    private String namespace;

    /**
     * The name of the attribute entry.
     */
    private String name;

    /**
     * The value of the attribute entry.
     */
    private String value;

    /**
     * Creates a new attribute entry for the given name and value.
     *
     * @param namespace the namespace of the attribute.
     * @param name      the attribute name (<code>null</code> not permitted).
     * @param value     the attribute value (<code>null</code> not permitted).
     */
    public AttributeEntry( final String namespace,
                           final String name,
                           final String value ) {
      if ( name == null ) {
        throw new NullPointerException( "Name must not be null. ["
          + name + ", " + value + ']' );
      }
      if ( value == null ) {
        throw new NullPointerException( "Value must not be null. ["
          + name + ", " + value + ']' );
      }
      this.namespace = namespace;
      this.name = name;
      this.value = value;
    }

    /**
     * Returns the attribute name.
     *
     * @return the name.
     */
    public String getName() {
      return this.name;
    }

    /**
     * Returns the value of this attribute entry.
     *
     * @return the value of the entry.
     */
    public String getValue() {
      return this.value;
    }

    /**
     * Returns the attribute namespace (which can be null).
     *
     * @return the namespace.
     */
    public String getNamespace() {
      return namespace;
    }

    /**
     * Compares this attribute entry for equality with an other object.
     *
     * @param o the other object.
     * @return true, if this object is equal, false otherwise.
     */
    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final AttributeEntry that = (AttributeEntry) o;

      if ( !name.equals( that.name ) ) {
        return false;
      }
      if ( namespace != null ? !namespace.equals( that.namespace ) : that.namespace != null ) {
        return false;
      }

      return true;
    }

    /**
     * Computes a hashcode for this attribute entry.
     *
     * @return the attribute entry's hashcode.
     */
    public int hashCode() {
      int result = ( namespace != null ? namespace.hashCode() : 0 );
      result = 29 * result + name.hashCode();
      return result;
    }

    protected void update( final String namespace, final String name, final String value ) {
      if ( name == null ) {
        throw new NullPointerException( "Name must not be null. ["
          + name + ", " + value + ']' );
      }
      if ( value == null ) {
        throw new NullPointerException( "Value must not be null. ["
          + name + ", " + value + ']' );
      }
      this.namespace = namespace;
      this.name = name;
      this.value = value;
    }
  }

  /**
   * The storage for all entries of this list.
   */
  private LinkedMap entryList;
  private AttributeEntry lookupKey;
  private transient AttributeEntry[] arrayCache;

  /**
   * Creates an empty attribute list with no default values.
   */
  public AttributeList() {
    this.entryList = new LinkedMap();
    this.lookupKey = new AttributeEntry( null, "lookup", "value" );
  }

  /**
   * Returns an iterator over the entry list. The iterator returns AttributeList.AttributeEntry objects.
   *
   * @return the iterator over the entries contained in this list.
   * @deprecated use toArray instead.
   */
  public Iterator iterator() {
    return Arrays.asList( entryList.values() ).iterator();
  }

  public AttributeEntry[] toArray() {
    if ( arrayCache == null ) {
      arrayCache = (AttributeEntry[]) entryList.values( new AttributeEntry[ entryList.size() ] );
    }
    return (AttributeEntry[]) arrayCache.clone();
  }

  /**
   * Defines an attribute.
   *
   * @param namespace the namespace of the attribute.
   * @param name      the name of the attribute to be defined
   * @param value     the value of the attribute.
   */
  public void setAttribute( final String namespace,
                            final String name,
                            final String value ) {
    if ( value == null ) {
      removeAttribute( namespace, name );
      return;
    }

    final AttributeEntry entry = new AttributeEntry( namespace, name, value );
    this.arrayCache = null;
    this.entryList.put( entry, entry );
  }

  /**
   * Returns the attribute value for the given attribute name or null, if the attribute is not defined in this list.
   *
   * @param namespace the namespace of the attribute.
   * @param name      the name of the attribute
   * @return the attribute value or null.
   */
  public String getAttribute( final String namespace,
                              final String name ) {
    return getAttribute( namespace, name, null );
  }

  /**
   * Returns the attribute value for the given attribute name or the given defaultvalue, if the attribute is not defined
   * in this list.
   *
   * @param namespace    the namespace of the attribute.
   * @param name         the name of the attribute.
   * @param defaultValue the default value.
   * @return the attribute value or the defaultValue.
   */
  public String getAttribute( final String namespace,
                              final String name,
                              final String defaultValue ) {
    lookupKey.update( namespace, name, "" );
    final AttributeEntry entry = (AttributeEntry) this.entryList.get( lookupKey );
    if ( entry != null ) {
      return entry.getValue();
    }
    return defaultValue;
  }

  /**
   * Removes the attribute with the given name from the list.
   *
   * @param namespace the namespace of the attribute that should be removed.
   * @param name      the name of the attribute which should be removed..
   */
  public void removeAttribute( final String namespace,
                               final String name ) {
    lookupKey.update( namespace, name, "" );
    entryList.remove( lookupKey );
    this.arrayCache = null;
  }

  /**
   * Checks, whether this list is empty.
   *
   * @return true, if the list is empty, false otherwise.
   */
  public boolean isEmpty() {
    return this.entryList.isEmpty();
  }

  /**
   * Adds a namespace declaration. In XML, Namespaces are declared by using a special attribute-syntax. As this syntax
   * is confusing and complicated, this method encapsulates this and make defining namespaces less confusing.
   *
   * @param prefix       the desired namespace prefix (can be null or empty to define the default namespace.
   * @param namespaceUri the URI of the namespace.
   */
  public void addNamespaceDeclaration( final String prefix,
                                       final String namespaceUri ) {
    if ( namespaceUri == null ) {
      throw new NullPointerException();
    }

    if ( prefix == null || "".equals( prefix ) ) {
      setAttribute( AttributeList.XMLNS_NAMESPACE, "", namespaceUri );
    } else {
      setAttribute( AttributeList.XMLNS_NAMESPACE, prefix, namespaceUri );
    }
  }

  /**
   * Removes a namespace declaration from this attribute list.
   *
   * @param prefix the declared namespace prefix.
   */
  public void removeNamespaceDeclaration( final String prefix ) {
    if ( prefix == null || "".equals( prefix ) ) {
      removeAttribute( AttributeList.XMLNS_NAMESPACE, "" );
    } else {
      removeAttribute( AttributeList.XMLNS_NAMESPACE, prefix );
    }
  }

  /**
   * Checks, whether the given prefix is defined.
   *
   * @param prefix the namespace prefix.
   * @return true, if the prefix is defined, false otherwise.
   */
  public boolean isNamespacePrefixDefined( final String prefix ) {
    return getAttribute( AttributeList.XMLNS_NAMESPACE, prefix ) != null;
  }

  /**
   * Checks, whether the given namespace URI has a defined prefix.
   *
   * @param uri the uri.
   * @return true, if there is at least one namespace declaration matching the given URI, false otherwise.
   */
  public boolean isNamespaceUriDefined( final String uri ) {
    if ( this.entryList.isEmpty() ) {
      return false;
    }

    final AttributeEntry[] objects = toArray();
    for ( int i = 0; i < objects.length; i++ ) {
      final AttributeEntry ae = objects[ i ];
      if ( ObjectUtilities.equal( ae.getValue(), uri ) &&
        AttributeList.XMLNS_NAMESPACE.equals( ae.getNamespace() ) ) {
        return true;
      }
    }
    return false;
  }
}
