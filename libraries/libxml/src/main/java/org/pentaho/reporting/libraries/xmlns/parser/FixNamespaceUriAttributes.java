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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;

/**
 * A SAX-Attributes implementation that fixes missing namespace-URIs. Attributes that define no namespace URIs on their
 * own will receive the defined default namespace.
 *
 * @author Thomas Morgner
 */
public class FixNamespaceUriAttributes implements Attributes {
  private Attributes attributes;
  private String defaultNSUri;

  /**
   * Creates a new FixNamespaceUriAttributes wrapper.
   *
   * @param defaultNSUri the default namespace that is used if no explicit namespace is defined for an attribute.
   * @param attributes   the original attributes.
   */
  public FixNamespaceUriAttributes( final String defaultNSUri,
                                    final Attributes attributes ) {
    this.attributes = attributes;
    this.defaultNSUri = defaultNSUri;
  }

  /**
   * Return the number of attributes in the list. <p/> <p>Once you know the number of attributes, you can iterate
   * through the list.</p>
   *
   * @return The number of attributes in the list.
   * @see #getURI(int)
   * @see #getLocalName(int)
   * @see #getQName(int)
   * @see #getType(int)
   * @see #getValue(int)
   */
  public int getLength() {
    return attributes.getLength();
  }

  /**
   * Look up an attribute's Namespace URI by index.
   *
   * @param index The attribute index (zero-based).
   * @return The Namespace URI, or the empty string if none is available, or null if the index is out of range.
   * @see #getLength
   */
  public String getURI( final int index ) {
    final String uri = attributes.getURI( index );
    if ( uri == null || "".equals( uri ) ) {
      return defaultNSUri;
    }
    return uri;
  }

  /**
   * Look up an attribute's local name by index.
   *
   * @param index The attribute index (zero-based).
   * @return The local name, or the empty string if Namespace processing is not being performed, or null if the index is
   * out of range.
   * @see #getLength
   */
  public String getLocalName( final int index ) {
    final String name = attributes.getLocalName( index );
    if ( name == null || "".equals( name ) ) {
      return attributes.getQName( index );
    }
    return name;
  }

  /**
   * Look up an attribute's XML qualified (prefixed) name by index.
   *
   * @param index The attribute index (zero-based).
   * @return The XML qualified name, or the empty string if none is available, or null if the index is out of range.
   * @see #getLength
   */
  public String getQName( final int index ) {
    return attributes.getQName( index );
  }

  /**
   * Look up an attribute's type by index. <p/> <p>The attribute type is one of the strings "CDATA", "ID", "IDREF",
   * "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES", or "NOTATION" (always in upper case).</p> <p/> <p>If the
   * parser has not read a declaration for the attribute, or if the parser does not report attribute types, then it must
   * return the value "CDATA" as stated in the XML 1.0 Recommendation (clause 3.3.3, "Attribute-Value
   * Normalization").</p> <p/> <p>For an enumerated attribute that is not a notation, the parser will report the type as
   * "NMTOKEN".</p>
   *
   * @param index The attribute index (zero-based).
   * @return The attribute's type as a string, or null if the index is out of range.
   * @see #getLength
   */
  public String getType( final int index ) {
    return attributes.getType( index );
  }

  /**
   * Look up an attribute's value by index. <p/> <p>If the attribute value is a list of tokens (IDREFS, ENTITIES, or
   * NMTOKENS), the tokens will be concatenated into a single string with each token separated by a single space.</p>
   *
   * @param index The attribute index (zero-based).
   * @return The attribute's value as a string, or null if the index is out of range.
   * @see #getLength
   */
  public String getValue( final int index ) {
    return attributes.getValue( index );
  }

  /**
   * Look up the index of an attribute by Namespace name.
   *
   * @param uri       The Namespace URI, or the empty string if the name has no Namespace URI.
   * @param localName The attribute's local name.
   * @return The index of the attribute, or -1 if it does not appear in the list.
   */
  public int getIndex( final String uri, final String localName ) {
    final int idx = attributes.getIndex( uri, localName );
    if ( idx >= 0 ) {
      return idx;
    }
    if ( defaultNSUri.equals( uri ) ) {
      final int index = attributes.getIndex( "", localName );
      if ( index != -1 ) {
        return index;
      }
      try {
        final int value2 = attributes.getIndex( null, localName );
        if ( value2 != -1 ) {
          return value2;
        }
      } catch ( Exception e ) {
        // ignore. Heck, Xerces breaks the SAX-Specs so we have to take weird steps to mess around their mess.
      }

      return attributes.getIndex( localName );
    }
    return -1;
  }

  /**
   * Look up the index of an attribute by XML qualified (prefixed) name.
   *
   * @param qName The qualified (prefixed) name.
   * @return The index of the attribute, or -1 if it does not appear in the list.
   */
  public int getIndex( final String qName ) {
    return attributes.getIndex( qName );
  }

  /**
   * Look up an attribute's type by Namespace name. <p/> <p>See {@link #getType(int) getType(int)} for a description of
   * the possible types.</p>
   *
   * @param uri       The Namespace URI, or the empty String if the name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute type as a string, or null if the attribute is not in the list or if Namespace processing is
   * not being performed.
   */
  public String getType( final String uri, final String localName ) {
    final String type = attributes.getType( uri, localName );
    if ( type != null ) {
      return type;
    }
    if ( defaultNSUri.equals( uri ) ) {
      final String type1 = attributes.getType( "", localName );
      if ( type1 != null ) {
        return type1;
      }
      try {
        final String value2 = attributes.getType( null, localName );
        if ( value2 != null ) {
          return value2;
        }
      } catch ( Exception e ) {
        // ignore. Heck, Xerces breaks the SAX-Specs so we have to take weird steps to mess around their mess.
      }

      return attributes.getType( localName );
    }
    return null;

  }

  /**
   * Look up an attribute's type by XML qualified (prefixed) name. <p/> <p>See {@link #getType(int) getType(int)} for a
   * description of the possible types.</p>
   *
   * @param qName The XML qualified name.
   * @return The attribute type as a string, or null if the attribute is not in the list or if qualified names are not
   * available.
   */
  public String getType( final String qName ) {
    return attributes.getType( qName );
  }

  /**
   * Look up an attribute's value by Namespace name. <p/> <p>See {@link #getValue(int) getValue(int)} for a description
   * of the possible values.</p>
   *
   * @param uri       The Namespace URI, or the empty String if the name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute value as a string, or null if the attribute is not in the list.
   */
  public String getValue( final String uri, final String localName ) {
    final String value = attributes.getValue( uri, localName );
    if ( value != null ) {
      return value;
    }
    if ( defaultNSUri.equals( uri ) ) {
      final String value1 = attributes.getValue( "", localName );
      if ( value1 != null ) {
        return value1;
      }
      try {
        final String value2 = attributes.getValue( null, localName );
        if ( value2 != null ) {
          return value2;
        }
      } catch ( Exception e ) {
        // ignore. Heck, Xerces breaks the SAX-Specs so we have to take weird steps to mess around their mess.
      }
      return attributes.getValue( localName );
    }
    return null;

  }

  /**
   * Look up an attribute's value by XML qualified (prefixed) name. <p/> <p>See {@link #getValue(int) getValue(int)} for
   * a description of the possible values.</p>
   *
   * @param qName The XML qualified name.
   * @return The attribute value as a string, or null if the attribute is not in the list or if qualified names are not
   * available.
   */
  public String getValue( final String qName ) {
    return attributes.getValue( qName );
  }
}
